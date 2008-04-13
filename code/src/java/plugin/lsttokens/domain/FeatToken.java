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
package plugin.lsttokens.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.base.CategorizedCDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.inst.CDOMDomain;
import pcgen.core.Domain;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.lst.DomainLstToken;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

public class FeatToken extends AbstractToken implements DomainLstToken,
		CDOMPrimaryToken<CDOMDomain>
{
	public static final Class<CDOMAbility> ABILITY_CLASS = CDOMAbility.class;

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	public boolean parse(Domain domain, String value)
	{
		domain.addFeat(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMDomain domain, String value)
	{
		return parseFeat(context, domain, value);
	}

	public boolean parseFeat(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		String token = tok.nextToken();

		if (token.startsWith("PRE") || token.startsWith("!PRE"))
		{
			Logging.errorPrint("Cannot have only PRExxx subtoken in "
					+ getTokenName());
			return false;
		}

		ArrayList<AssociatedPrereqObject> edgeList = new ArrayList<AssociatedPrereqObject>();

		while (true)
		{
			CDOMSingleRef<CDOMAbility> ability = context.ref.getCDOMReference(
					ABILITY_CLASS, CDOMAbilityCategory.FEAT, token);
			AssociatedPrereqObject edge = context.getGraphContext().grant(
					getTokenName(), obj, ability);
			edge.setAssociation(AssociationKey.ABILITY_NATURE,
					AbilityNature.NORMAL);
			edgeList.add(edge);

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return true;
			}
			token = tok.nextToken();
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put feats after the "
						+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			for (AssociatedPrereqObject edge : edgeList)
			{
				edge.addPrerequisite(prereq);
			}
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}

		return true;
	}

	public String[] unparse(LoadContext context, CDOMDomain domain)
	{
		AssociatedChanges<CDOMReference<CDOMAbility>> changes = context.getGraphContext()
				.getChangesFromToken(getTokenName(), domain, ABILITY_CLASS);
		MapToList<CDOMReference<CDOMAbility>, AssociatedPrereqObject> mtl = changes
				.getAddedAssociations();
		if (mtl == null || mtl.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		MapToList<Set<Prerequisite>, LSTWriteable> m = new HashMapToList<Set<Prerequisite>, LSTWriteable>();
		for (CDOMReference<CDOMAbility> ab : mtl.getKeySet())
		{
			for (AssociatedPrereqObject assoc : mtl.getListFor(ab))
			{
				AbilityNature an = assoc
						.getAssociation(AssociationKey.ABILITY_NATURE);
				if (!AbilityNature.NORMAL.equals(an))
				{
					context.addWriteMessage("Abilities awarded by "
							+ getTokenName()
							+ " must be of NORMAL AbilityNature");
					return null;
				}
				if (!CDOMAbilityCategory.FEAT
						.equals(((CategorizedCDOMReference<CDOMAbility>) ab)
								.getCDOMCategory()))
				{
					context.addWriteMessage("Abilities awarded by "
							+ getTokenName() + " must be of CATEGORY FEAT");
					return null;
				}
				m.addToListFor(new HashSet<Prerequisite>(assoc
						.getPrerequisiteList()), ab);
			}
		}

		Set<String> list = new TreeSet<String>();

		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			String ab = ReferenceUtilities.joinLstFormat(m.getListFor(prereqs),
					Constants.PIPE);
			if (prereqs != null && !prereqs.isEmpty())
			{
				ab = ab + Constants.PIPE
						+ getPrerequisiteString(context, prereqs);
			}
			list.add(ab);
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMDomain> getTokenClass()
	{
		return CDOMDomain.class;
	}
}
