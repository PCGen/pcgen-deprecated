/*
 * AbilityToken.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on March 20, 2007
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.lsttokens.add;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.factory.GrantFactory;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.ReferenceChoiceSet;
import pcgen.core.Ability;
import pcgen.core.PObject;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.AddLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * <code>AbilityToken</code> parses ADD:ABILITY entries.
 * 
 * <p>
 * <b>Tag Name</b>: <code>ADD:ABILITY</code>|w|x|y|z,z<br />
 * <b>Variables Used (w)</b>: Count (Optional Number, Variable or Formula -
 * Number of choices granted).<br />
 * <b>Variables Used (x)</b>: Ability Category (The Ability Category this
 * ability will be added to).<br />
 * <b>Variables Used (y)</b>: Ability Nature (The nature of the added ability:
 * <tt>NORMAL</tt>, <tt>AUTOMATIC</tt>, or <tt>VIRTUAL</tt>)<br />
 * <b>Variables Used (z)</b>: Ability Key or TYPE(The Ability to add. Can have
 * choices specified in &quot;()&quot;)<br />
 * <p />
 * <b>What it does:</b><br/>
 * <ul>
 * <li>Adds an Ability to a character, providing choices if these are required.</li>
 * <li>The Ability is added to the Ability Category specified.</li>
 * <li>Choices can be specified by including them in parenthesis after the
 * ability key name (whitespace is ignored).</li>
 * 
 * Last Editor: $Author$ Last Edited: $Date: 2007-03-25 08:09:48 -0400
 * (Sun, 25 Mar 2007) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class AbilityToken extends AbstractToken implements AddLstToken
{
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.AddLstToken#parse(pcgen.core.PObject,
	 *      java.lang.String, int)
	 */
	public boolean parse(PObject target, String value, int level)
	{
		String[] tokens = value.split("\\|");

		String countString;
		int index = 0;

		if (tokens.length < 3)
		{
			Logging.errorPrint("Syntax of ADD:" + getTokenName()
				+ " only allows three or four | : " + value);
			return false;
		}
		try
		{
			Integer.parseInt(tokens[0]);
			countString = tokens[0];
			index++;
			if (tokens.length != 4)
			{
				Logging.errorPrint("Syntax of ADD:" + getTokenName()
					+ " requires four | when a count is present: " + value);
				return false;
			}
		}
		catch (Exception e)
		{
			countString = "1";
			if (tokens.length != 3)
			{
				Logging
					.errorPrint("Syntax of ADD:" + getTokenName()
						+ " requires three | when a count is not present: "
						+ value);
				return false;
			}
		}

		// Category, nature, abilities
		String category = tokens[index++];
		if (category == null)
		{
			Logging.errorPrint("Malformed ADD Token: Missing Category: "
				+ value);
			return false;
		}
		String nature = tokens[index++];
		if (nature == null)
		{
			Logging.errorPrint("Malformed ADD Token: Missing Nature: " + value);
			return false;
		}
		String abilities = tokens[index++];

		if (abilities == null)
		{
			Logging.errorPrint("Malformed ADD Token: Missing Abilities: "
				+ value);
			return false;
		}

		StringBuffer addString = new StringBuffer();
		addString.append(getTokenName());
		addString.append("(CATEGORY=");
		addString.append(category);
		addString.append(",NATURE=");
		addString.append(nature);
		addString.append(",");
		addString.append(abilities);
		addString.append(")");
		addString.append(countString);
		target.addAddList(level, addString.toString());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "ABILITY";
	}

	public boolean parse(LoadContext context, PObject obj, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);

		String nextToken = st.nextToken();
		int count;
		try
		{
			count = Integer.parseInt(nextToken);
			if (count < 1)
			{
				Logging.errorPrint("Count in ADD:" + getTokenName()
					+ " must be > 0");
				return false;
			}
			if (!st.hasMoreTokens())
			{
				Logging.errorPrint("Invalid " + getTokenName()
					+ ": has only a Count: " + value);
				return false;
			}
			nextToken = st.nextToken();
		}
		catch (NumberFormatException e)
		{
			count = 1;
			// This is OK, count is optional
		}
		// 2 left, because the first was already fetched
		if (st.countTokens() != 2)
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ ": does not have the proper number of arguments: " + value);
			return false;
		}

		AbilityCategory ac;
		try
		{
			ac = AbilityCategory.valueOf(nextToken);
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Invalid Ability Category in " + getTokenName()
				+ ": " + value);
			return false;
		}

		AbilityNature nat;
		try
		{
			nat = AbilityNature.valueOf(st.nextToken());
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Invalid Ability Nature in " + getTokenName()
				+ ": " + value);
			return false;
		}

		String items = st.nextToken();
		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			return false;
		}
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);

		List<CDOMReference<Ability>> refs =
				new ArrayList<CDOMReference<Ability>>();
		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<Ability> ref;
			if (Constants.LST_ANY.equalsIgnoreCase(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(ABILITY_CLASS, ac);
			}
			else
			{
				foundOther = true;
				ref =
						TokenUtilities.getTypeOrPrimitive(context,
							ABILITY_CLASS, ac, token);
				if (ref == null)
				{
					Logging
						.errorPrint("  Error was encountered while parsing ADD:"
							+ getTokenName()
							+ ": "
							+ token
							+ " is not a valid reference: " + value);
					return false;
				}
			}
			refs.add(ref);
		}

		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		ReferenceChoiceSet<Ability> rcs = new ReferenceChoiceSet<Ability>(refs);
		ChoiceSet<Ability> cs = new ChoiceSet<Ability>("ADD", rcs);
		PCGraphGrantsEdge edge = context.graph.grant(getTokenName(), obj, cs);
		edge.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
			.getFormulaFor(count));
		edge.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
			.getFormulaFor(Integer.MAX_VALUE));
		GrantFactory<Ability> gf = new GrantFactory<Ability>(edge);
		/*
		 * FUTURE Technically, this Category item should not be in the
		 * GrantFactory, as it really belogs as something that can be extracted
		 * from the ChoiceSet...
		 */
		gf.setAssociation(AssociationKey.ABILITY_CATEGORY, ac);
		gf.setAssociation(AssociationKey.ABILITY_NATURE, nat);
		context.graph.grant(getTokenName(), obj, gf);
		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
	{
		GraphChanges<ChoiceSet> choiceChanges =
				context.graph.getChangesFromToken(getTokenName(), obj,
					ChoiceSet.class);
		if (choiceChanges == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = choiceChanges.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token present
			return null;
		}
		GraphChanges<GrantFactory> grantChanges =
				context.graph.getChangesFromToken(getTokenName(), obj,
					GrantFactory.class);
		Collection<LSTWriteable> grantAdded = grantChanges.getAdded();
		if (grantAdded == null || grantAdded.isEmpty())
		{
			// Zero indicates no Token present
			return null;
		}
		List<String> addStrings = new ArrayList<String>();
		for (LSTWriteable lstw : added)
		{
			ChoiceSet<?> cs = (ChoiceSet<?>) lstw;
			if (ABILITY_CLASS.equals(cs.getChoiceClass()))
			{
				AbilityNature nat = null;
				AbilityCategory cat = null;
				for (LSTWriteable gw : grantAdded)
				{
					GrantFactory<?> gf = (GrantFactory<?>) gw;
					if (gf.usesChoiceSet(cs))
					{
						cat =
								gf
									.getAssociation(AssociationKey.ABILITY_CATEGORY);
						nat = gf.getAssociation(AssociationKey.ABILITY_NATURE);
						break;
					}
				}
				if (nat == null)
				{
					context
						.addWriteMessage("Unable to find Nature for GrantFactory");
					return null;
				}
				if (cat == null)
				{
					context
						.addWriteMessage("Unable to find Category for GrantFactory");
					return null;
				}
				if (AbilityCategory.FEAT.equals(cat)
					&& AbilityNature.NORMAL.equals(nat))
				{
					// will be done with FEAT
					continue;
				}
				if (AbilityCategory.FEAT.equals(cat)
					&& AbilityNature.VIRTUAL.equals(nat))
				{
					// will be done with VFEAT
					continue;
				}
				AssociatedPrereqObject assoc =
						choiceChanges.getAddedAssociation(lstw);
				Formula f = assoc.getAssociation(AssociationKey.CHOICE_COUNT);
				if (f == null)
				{
					context.addWriteMessage("Unable to find Choice Count");
					return null;
				}
				String fString = f.toString();
				StringBuilder sb = new StringBuilder();
				if (!"1".equals(fString))
				{
					sb.append(fString).append(Constants.PIPE);
				}
				sb.append(cat).append(Constants.PIPE);
				sb.append(nat).append(Constants.PIPE);
				sb.append(cs.getLSTformat());
				addStrings.add(sb.toString());

				// assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}
}
