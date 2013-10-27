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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.inst.CDOMRace;
import pcgen.cdom.inst.CDOMSkill;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with MONCSKILL Token
 */
public class MoncskillToken extends AbstractToken implements RaceLstToken,
		CDOMPrimaryToken<CDOMRace>
{
	private static final Class<CDOMSkill> SKILL_CLASS = CDOMSkill.class;

	private static final Class<ClassSkillList> SKILLLIST_CLASS = ClassSkillList.class;

	@Override
	public String getTokenName()
	{
		return "MONCSKILL";
	}

	public boolean parse(Race race, String value)
	{
		race.setMonCSkillList(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMRace race, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		CDOMReference<ClassSkillList> ref = context.ref.getCDOMReference(
				SKILLLIST_CLASS, "*Monster");

		boolean firstToken = true;
		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				if (!firstToken)
				{
					Logging.errorPrint("Non-sensical situation was "
							+ "encountered while parsing " + getTokenName()
							+ ": When used, .CLEAR must be the first argument");
					return false;
				}
				context.getListContext().removeAllFromList(getTokenName(),
						race, ref);
			}
			else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				CDOMReference<CDOMSkill> skill;
				String clearText = tokText.substring(7);
				if (Constants.LST_ALL.equals(clearText))
				{
					skill = context.ref.getCDOMAllReference(SKILL_CLASS);
				}
				else
				{
					skill = TokenUtilities.getTypeOrPrimitive(context,
							SKILL_CLASS, clearText);
				}
				if (skill == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
							+ getTokenName());
					return false;
				}
				context.getListContext().removeFromList(getTokenName(), race,
						ref, skill);
			}
			else
			{
				/*
				 * Note this is done one-by-one, because .CLEAR. token type
				 * needs to be able to perform the unlink. That could be
				 * changed, but the increase in complexity isn't worth it.
				 * (Changing it to a grouping object that didn't place links in
				 * the graph would also make it harder to trace the source of
				 * class skills, etc.)
				 */
				CDOMReference<CDOMSkill> skill;
				if (Constants.LST_ALL.equals(tokText))
				{
					foundAny = true;
					skill = context.ref.getCDOMAllReference(SKILL_CLASS);
				}
				else
				{
					foundOther = true;
					skill = TokenUtilities.getTypeOrPrimitive(context,
							SKILL_CLASS, tokText);
				}
				if (skill == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
							+ getTokenName());
					return false;
				}
				AssociatedPrereqObject apo = context.getListContext()
						.addToList(getTokenName(), race, ref, skill);
				apo.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);
			}
			firstToken = false;
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMRace race)
	{
		CDOMReference<ClassSkillList> swl = context.ref.getCDOMReference(
				SKILLLIST_CLASS, "*Monster");
		AssociatedChanges<CDOMReference<CDOMSkill>> changes = context
				.getListContext().getChangesInList(getTokenName(), race, swl);
		List<String> list = new ArrayList<String>();
		Collection<CDOMReference<CDOMSkill>> removedItems = changes.getRemoved();
		if (removedItems != null && !removedItems.isEmpty())
		{
			if (changes.includesGlobalClear())
			{
				context.addWriteMessage("Non-sensical relationship in "
						+ getTokenName()
						+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			list.add(Constants.LST_DOT_CLEAR_DOT
					+ ReferenceUtilities.joinLstFormat(removedItems,
							"|.CLEAR."));
		}
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		MapToList<CDOMReference<CDOMSkill>, AssociatedPrereqObject> map = changes
				.getAddedAssociations();
		if (map != null && !map.isEmpty())
		{
			Set<CDOMReference<CDOMSkill>> added = map.getKeySet();
			for (CDOMReference<CDOMSkill> ab : added)
			{
				for (AssociatedPrereqObject assoc : map.getListFor(ab))
				{
					if (!SkillCost.CLASS.equals(assoc
							.getAssociation(AssociationKey.SKILL_COST)))
					{
						context.addWriteMessage("Skill Cost must be "
								+ "CLASS for Token " + getTokenName());
						return null;
					}
				}
			}
			list.add(ReferenceUtilities.joinLstFormat(added, Constants.PIPE));
		}
		if (list.isEmpty())
		{
			// Zero indicates no add or clear
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMRace> getTokenClass()
	{
		return CDOMRace.class;
	}
}
