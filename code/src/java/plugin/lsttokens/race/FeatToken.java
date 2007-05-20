/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.race;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMCategorizedSingleRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CategorizedCDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.Race;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with FEAT Token
 */
public class FeatToken extends AbstractToken implements RaceLstToken
{
	public static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	public boolean parse(Race race, String value)
	{
		race.setFeatList(value);
		return true;
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		return parseFeat(context, race, value);
	}

	public boolean parseFeat(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		String token = tok.nextToken();

		if (token.startsWith("PRE") || token.startsWith("!PRE"))
		{
			Logging.errorPrint("Cannot have only PRExxx subtoken in "
				+ getTokenName());
			return false;
		}

		List<CDOMReference<Ability>> abilityList =
				new ArrayList<CDOMReference<Ability>>();

		while (true)
		{
			CDOMCategorizedSingleRef<Ability> ability =
					context.ref.getCDOMReference(ABILITY_CLASS,
						AbilityCategory.FEAT, token);
			abilityList.add(ability);

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				finish(context, obj, abilityList, null);
				return true;
			}
			token = tok.nextToken();
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
		}

		List<Prerequisite> prereqs = new ArrayList<Prerequisite>();
		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put feats after the "
					+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			prereqs.add(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}

		finish(context, obj, abilityList, prereqs);

		return true;
	}

	private void finish(LoadContext context, CDOMObject obj,
		List<CDOMReference<Ability>> abilityList, List<Prerequisite> prereqs)
	{
		for (CDOMReference<Ability> ability : abilityList)
		{
			PCGraphGrantsEdge edge =
					context.graph.grant(getTokenName(), obj, ability);
			edge.setAssociation(AssociationKey.ABILITY_NATURE,
				AbilityNature.NORMAL);
			if (prereqs != null)
			{
				for (Prerequisite prereq : prereqs)
				{
					edge.addPrerequisite(prereq);
				}
			}
		}
	}

	public String[] unparse(LoadContext context, Race race)
	{
		GraphChanges<Ability> changes =
				context.graph.getChangesFromToken(getTokenName(), race,
					ABILITY_CLASS);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		HashMapToList<Set<Prerequisite>, LSTWriteable> m =
				new HashMapToList<Set<Prerequisite>, LSTWriteable>();
		for (LSTWriteable ab : added)
		{
			AssociatedPrereqObject assoc = changes.getAddedAssociation(ab);
			AbilityNature an =
					assoc.getAssociation(AssociationKey.ABILITY_NATURE);
			if (!AbilityNature.NORMAL.equals(an))
			{
				context.addWriteMessage("Abilities awarded by "
					+ getTokenName() + " must be of NORMAL AbilityNature");
				return null;
			}
			if (!AbilityCategory.FEAT
				.equals(((CategorizedCDOMReference<Ability>) ab)
					.getCDOMCategory()))
			{
				context.addWriteMessage("Abilities awarded by "
					+ getTokenName() + " must be of CATEGORY FEAT");
				return null;
			}
			m.addToListFor(new HashSet<Prerequisite>(assoc
				.getPrerequisiteList()), ab);
		}

		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		SortedSet<LSTWriteable> set =
				new TreeSet<LSTWriteable>(TokenUtilities.WRITEABLE_SORTER);

		Set<String> list = new TreeSet<String>();

		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			List<LSTWriteable> abilities = m.getListFor(prereqs);
			set.clear();
			set.addAll(abilities);
			String ab = ReferenceUtilities.joinLstFormat(set, Constants.PIPE);
			if (prereqs != null && !prereqs.isEmpty())
			{
				TreeSet<String> prereqSet = new TreeSet<String>();
				for (Prerequisite p : prereqs)
				{
					StringWriter swriter = new StringWriter();
					try
					{
						prereqWriter.write(swriter, p);
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage("Error writing Prerequisite: "
							+ e);
						return null;
					}
					prereqSet.add(swriter.toString());
				}
				ab =
						ab + Constants.PIPE
							+ StringUtil.join(prereqSet, Constants.PIPE);
			}
			list.add(ab);
		}
		return list.toArray(new String[list.size()]);
	}
}
