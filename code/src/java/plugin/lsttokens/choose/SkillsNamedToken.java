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
import pcgen.cdom.choice.ObjectFilter;
import pcgen.cdom.choice.PCChooser;
import pcgen.cdom.choice.RefSetChooser;
import pcgen.cdom.choice.RemovingChooser;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class SkillsNamedToken implements ChooseLstToken
{

	public boolean parse(PObject po, String value)
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
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}
		po.setChoiceString(value);
		return true;
	}

	public String getTokenName()
	{
		return "SKILLSNAMED";
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
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with | : " + value);
			return null;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with | : " + value);
			return null;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator || : " + value);
			return null;
		}
		if (Constants.LST_ALL.equals(value))
		{
			return AnyChooser.getAnyChooser(Skill.class);
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		List<ChoiceSet<Skill>> list = new ArrayList<ChoiceSet<Skill>>();
		List<CDOMReference<Skill>> skillList =
				new ArrayList<CDOMReference<Skill>>();
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			if (Constants.LST_ALL.equals(tokString))
			{
				Logging.errorPrint("Cannot use ALL and another qualifier: "
					+ value);
				return null;
			}
			else if (Constants.LST_CLASS.equals(tokString))
			{
				PCChooser<Skill> pcc = new PCChooser<Skill>(Skill.class);
				pcc.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);
				list.add(pcc);
			}
			else if (Constants.LST_CROSSCLASS.equals(tokString))
			{
				PCChooser<Skill> pcc = new PCChooser<Skill>(Skill.class);
				pcc.setAssociation(AssociationKey.SKILL_COST, SkillCost.CROSS_CLASS);
				list.add(pcc);
			}
			else if (Constants.LST_EXCLUSIVE.equals(tokString))
			{
				PCChooser<Skill> pcc = new PCChooser<Skill>(Skill.class);
				pcc.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);
				pcc.setAssociation(AssociationKey.SKILL_COST, SkillCost.CROSS_CLASS);
				RemovingChooser<Skill> rc = new RemovingChooser<Skill>(pcc);
				ObjectFilter<Skill> of = new ObjectFilter<Skill>(Skill.class);
				of.setObjectFilter(ObjectKey.EXCLUSIVE, Boolean.TRUE);
				rc.addRemovingChoiceFilter(of);
				list.add(rc);
			}
			else if (Constants.LST_NORANK.equals(tokString))
			{
				//TODO Need an implementation here :(
			}
			else
			{
				CDOMReference<Skill> ref =
						TokenUtilities.getTypeOrPrimitive(context, Skill.class,
							tokString);
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
			listSet = new RefSetChooser<Skill>(skillList);
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
}
