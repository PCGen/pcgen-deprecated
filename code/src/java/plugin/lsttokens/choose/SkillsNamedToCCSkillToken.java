/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.choose;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.choice.AnyChooser;
import pcgen.cdom.choice.CompoundOrChooser;
import pcgen.cdom.choice.PCListChooser;
import pcgen.cdom.choice.ReferenceChooser;
import pcgen.cdom.choice.RemovingChooser;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.filter.NegatingFilter;
import pcgen.cdom.filter.ObjectKeyFilter;
import pcgen.cdom.filter.PCListFilter;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.ClassSkillList;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class SkillsNamedToCCSkillToken extends AbstractToken implements
		ChooseLstToken
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value);
			return false;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return false;
		}
		if (hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringBuilder sb = new StringBuilder();
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName()).append('|').append(value);
		po.setChoiceString(sb.toString());
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "SKILLSNAMEDTOCCSKILL";
	}

	public ChoiceSet<?> parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value);
			return null;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return null;
		}
		if (hasIllegalSeparator('|', value))
		{
			return null;
		}

		ChoiceSet<Skill> base;
		if (Constants.LST_ANY.equals(value) || Constants.LST_ALL.equals(value))
		{
			base = AnyChooser.getAnyChooser(Skill.class);
		}
		else
		{
			base = parseChoices(context, value);
			if (base == null)
			{
				return null;
			}
		}
		RemovingChooser<Skill> rc = new RemovingChooser<Skill>(base, true);
		PCListFilter<Skill> pcFilter =
				new PCListFilter<Skill>(ClassSkillList.class);
		pcFilter.setAssociation(AssociationKey.SKILL_COST,
			SkillCost.CROSS_CLASS);
		rc.addRemovingChoiceFilter(NegatingFilter.getNegatingFilter(pcFilter),
			false);
		return rc;
	}

	private ChoiceSet<Skill> parseChoices(LoadContext context, String value)
	{
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		List<ChoiceSet<Skill>> list = new ArrayList<ChoiceSet<Skill>>();
		List<CDOMReference<Skill>> skillList =
				new ArrayList<CDOMReference<Skill>>();
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			if (Constants.LST_ANY.equals(tokString)
				|| Constants.LST_ALL.equals(tokString))
			{
				Logging.errorPrint("Cannot use ALL and another qualifier: "
					+ value);
				return null;
			}
			else if (Constants.LST_CLASS.equals(tokString))
			{
				PCListChooser<Skill> pcc =
						new PCListChooser<Skill>(ClassSkillList.class);
				pcc.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);
				list.add(pcc);
			}
			else if (Constants.LST_CROSSCLASS.equals(tokString))
			{
				PCListChooser<Skill> pcc =
						new PCListChooser<Skill>(ClassSkillList.class);
				pcc.setAssociation(AssociationKey.SKILL_COST,
					SkillCost.CROSS_CLASS);
				list.add(pcc);
			}
			else if (Constants.LST_EXCLUSIVE.equals(tokString))
			{
				PCListChooser<Skill> pcc =
						new PCListChooser<Skill>(ClassSkillList.class);
				pcc.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);
				pcc.setAssociation(AssociationKey.SKILL_COST,
					SkillCost.CROSS_CLASS);
				RemovingChooser<Skill> rc =
						new RemovingChooser<Skill>(pcc, false);
				ObjectKeyFilter<Skill> of =
						new ObjectKeyFilter<Skill>(Skill.class);
				of.setObjectFilter(ObjectKey.EXCLUSIVE, Boolean.TRUE);
				rc.addRemovingChoiceFilter(of, true);
				list.add(rc);
			}
			else if (Constants.LST_NORANK.equals(tokString))
			{
				// TODO Need an implementation here :(
			}
			else
			{
				CDOMReference<Skill> ref =
						TokenUtilities.getTypeOrPrimitive(context, Skill.class,
							tokString);
				if (ref == null)
				{
					Logging.errorPrint("Invalid Reference: " + tokString
						+ " in CHOOSE:" + getTokenName() + ": " + value);
					return null;
				}
				skillList.add(ref);
			}
		}
		ChoiceSet<Skill> listSet;
		if (skillList.isEmpty())
		{
			listSet = null;
		}
		else
		{
			listSet = new ReferenceChooser<Skill>(skillList);
		}
		if (list.isEmpty())
		{
			return listSet;
		}
		else
		{
			CompoundOrChooser<Skill> chooser = new CompoundOrChooser<Skill>();
			chooser.addAllChoiceSets(list);
			if (listSet != null)
			{
				chooser.addChoiceSet(listSet);
			}
			return chooser;
		}
	}

	public String unparse(LoadContext context, ChoiceSet<?> chooser)
	{
		return chooser.getLSTformat();
	}
}
