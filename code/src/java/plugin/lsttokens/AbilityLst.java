/*
 * AbilityLst.java
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.Logging;
import pcgen.cdom.base.CDOMCategorizedSingleRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.QualifiedObject;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.PropertyFactory;

/**
 * Implements the ABILITY: global LST token.
 * 
 * <p>
 * <b>Tag Name</b>: <code>ABILITY</code>:x|y|z|z<br />
 * <b>Variables Used (x)</b>: Ability Category (The Ability Category this
 * ability will be added to).<br />
 * <b>Variables Used (y)</b>: Ability Nature (The nature of the added ability:
 * <tt>NORMAL</tt>, <tt>AUTOMATIC</tt>, or <tt>VIRTUAL</tt>)<br />
 * <b>Variables Used (z)</b>: Ability Key or TYPE(The Ability to add. Can have
 * choices specified in &quot;()&quot;)<br />
 * <b>Prereqs Allowed</b>: Yes <br />
 * <p />
 * <b>What it does:</b><br/>
 * <ul>
 * <li>Adds an Ability to a character.</li>
 * <li>The Ability is added to the Ability Category specied and that category's
 * pool will be charged if the Nature is <tt>NORMAL</tt></li>
 * <li>This tag will <b>not</b> cause a chooser to appear so all required
 * choices must be specified in the tag</li>
 * <li>Choices can be specified by including them in parenthesis after the
 * ability key name (whitespace is ignored).</li>
 * <li>A <tt>CATEGORY</tt> tag can be added to the ability key to specify
 * that the innate ability category specified be searched for a matching
 * ability.</li>
 * <li>If no <tt>CATEGORY</tt> is specified the standard list for the ability
 * category will be used to find a matching ability.</li>
 * <li>This tag is a replacement for the following tags: <tt>FEAT</tt>,
 * <tt>VFEAT</tt>, and <tt>FEATAUTO</tt>.
 * </ul>
 * <b>Where it is used:</b><br />
 * Global tag can be used anywhere.
 * <p />
 * <b>Examples:</b><br />
 * <code>ABILITY:FEAT|AUTOMATIC|TYPE=Metamagic</code><br />
 * Adds a Metamagic feat as an Auto feat.
 * <p />
 * 
 * <code>ABILITY:CLASSFEATURE|VIRTUAL|CATEGORY=FEAT:Stunning Fist</code><br />
 * Adds the Stunning Fist feat as a virtual class feature.
 * <p />
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 * 
 */
public class AbilityLst extends AbstractToken implements GlobalLstToken
{

	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	/**
	 * @see pcgen.persistence.lst.GlobalLstToken#parse(pcgen.core.PObject,
	 *      java.lang.String, int)
	 */
	public boolean parse(PObject anObj, String aValue, int anInt)
		throws PersistenceLayerException
	{
		final StringTokenizer tok = new StringTokenizer(aValue, Constants.PIPE);

		final String cat = tok.nextToken();
		final pcgen.core.AbilityCategory category =
				SettingsHandler.getGame().getAbilityCategory(cat);
		if (category == null)
		{
			throw new PersistenceLayerException(PropertyFactory
				.getFormattedString("Errors.LstTokens.ValueNotFound", //$NON-NLS-1$
					getClass().getName(), "Ability Category", cat));
		}

		if (tok.hasMoreTokens())
		{
			final String natureKey = tok.nextToken();
			final Ability.Nature nature = Ability.Nature.valueOf(natureKey);
			if (nature == null)
			{
				throw new PersistenceLayerException(PropertyFactory
					.getFormattedString("Errors.LstTokens.ValueNotFound", //$NON-NLS-1$
						getClass().getName(), "Ability Nature", cat));
			}

			ArrayList<Prerequisite> preReqs = new ArrayList<Prerequisite>();
			if (anInt > -9)
			{
				try
				{
					PreParserFactory factory = PreParserFactory.getInstance();
					String preLevelString = "PRELEVEL:" + anInt; //$NON-NLS-1$
					if (anObj instanceof PCClass)
					{
						// Classes handle this differently
						preLevelString =
								"PRECLASS:1," + anObj.getKeyName() + "=" + anInt; //$NON-NLS-1$ //$NON-NLS-2$
					}
					Prerequisite r = factory.parse(preLevelString);
					preReqs.add(r);
				}
				catch (PersistenceLayerException notUsed)
				{
					return false;
				}
			}
			final List<String> abilityList = new ArrayList<String>();
			while (tok.hasMoreTokens())
			{
				final String key = tok.nextToken();
				if (PreParserFactory.isPreReqString(key))
				{
					final PreParserFactory factory =
							PreParserFactory.getInstance();
					final Prerequisite r = factory.parse(key);
					preReqs.add(r);
				}
				else
				{
					abilityList.add(key);
				}
			}
			for (final String ability : abilityList)
			{
				anObj.addAbility(category, nature, new QualifiedObject<String>(
					ability, preReqs));
			}
			return true;
		}

		throw new PersistenceLayerException(PropertyFactory.getFormattedString(
			"Errors.LstTokens.InvalidTokenFormat", //$NON-NLS-1$
			getClass().getName(), aValue));
	}

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "ABILITY"; //$NON-NLS-1$
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		AbilityCategory ac;
		String cat = tok.nextToken();
		try
		{
			ac = AbilityCategory.valueOf(cat);
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint(getTokenName()
				+ " refers to invalid Ability Category: " + cat);
			return false;
		}

		if (!tok.hasMoreTokens())
		{
			Logging
				.errorPrint(getTokenName()
					+ " must have a Nature, Format is: CATEGORY|NATURE|AbilityName: "
					+ value);
			return false;
		}

		String natureKey = tok.nextToken();
		AbilityNature an;
		try
		{
			an = AbilityNature.valueOf(natureKey);
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint(getTokenName()
				+ " refers to invalid Ability Category: " + natureKey);
			return false;
		}

		if (!tok.hasMoreTokens())
		{
			Logging
				.errorPrint(getTokenName()
					+ " must have abilities, Format is: CATEGORY|NATURE|AbilityName: "
					+ value);
			return false;
		}

		while (tok.hasMoreTokens())
		{
			CDOMCategorizedSingleRef<Ability> ability =
					context.ref.getCDOMReference(ABILITY_CLASS, ac, tok
						.nextToken());
			PCGraphGrantsEdge edge =
					context.graph.linkObjectIntoGraph(getTokenName(), obj,
						ability);
			edge.setAssociation(AssociationKey.ABILITY_NATURE, an);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> edgeSet =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					ABILITY_CLASS);

		if (edgeSet == null || edgeSet.isEmpty())
		{
			return null;
		}
		DoubleKeyMapToList<AbilityNature, Category<Ability>, CDOMCategorizedSingleRef<Ability>> m =
				new DoubleKeyMapToList<AbilityNature, Category<Ability>, CDOMCategorizedSingleRef<Ability>>();
		for (PCGraphEdge edge : edgeSet)
		{
			AbilityNature nature =
					edge.getAssociation(AssociationKey.ABILITY_NATURE);
			CDOMCategorizedSingleRef<Ability> ab =
					(CDOMCategorizedSingleRef<Ability>) edge.getSinkNodes()
						.get(0);
			m.addToListFor(nature, ab.getCDOMCategory(), ab);
		}

		SortedSet<CategorizedCDOMReference<Ability>> set =
				new TreeSet<CategorizedCDOMReference<Ability>>(
					TokenUtilities.CAT_REFERENCE_SORTER);
		Set<String> returnSet = new TreeSet<String>();
		for (AbilityNature nature : m.getKeySet())
		{
			for (Category<Ability> category : m.getSecondaryKeySet(nature))
			{
				StringBuilder sb = new StringBuilder();
				sb.append(category).append(Constants.PIPE);
				sb.append(nature);
				set.clear();
				set.addAll(m.getListFor(nature, category));
				for (CategorizedCDOMReference<Ability> a : set)
				{
					sb.append(Constants.PIPE).append(a.getLSTformat());
				}
				returnSet.add(sb.toString());
			}
		}
		return returnSet.toArray(new String[returnSet.size()]);
	}

}
