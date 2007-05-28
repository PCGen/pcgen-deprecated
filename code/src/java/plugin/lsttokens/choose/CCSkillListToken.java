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
import pcgen.cdom.choice.CompoundAndChooser;
import pcgen.cdom.choice.PCChooser;
import pcgen.cdom.choice.RefSetChooser;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class CCSkillListToken implements ChooseLstToken
{

	public boolean parse(PObject po, String value)
	{
		if (value.indexOf('|') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain | : " + value);
			return false;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return false;
		}
		if (value.charAt(0) == ',')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with , : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == ',')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with , : " + value);
			return false;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}
		po.setChoiceString(value);
		return true;
	}

	public String getTokenName()
	{
		return "CCSKILLLIST";
	}

	public ChoiceSet<?> parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (value.indexOf('|') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain | : " + value);
			return null;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return null;
		}
		if (value.charAt(0) == ',')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with , : " + value);
			return null;
		}
		if (value.charAt(value.length() - 1) == ',')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with , : " + value);
			return null;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return null;
		}
		/*
		 * TODO Not sure if this is really the correct reference here - what is
		 * the place where one can gather ALL of the Class Skills for a given
		 * PC?
		 */
		PCChooser<Skill> pcChooser = new PCChooser<Skill>(Skill.class);
		pcChooser.setAssociation(AssociationKey.SKILL_COST,
			SkillCost.CROSS_CLASS);
		if (Constants.LST_LIST.equals(value))
		{
			return pcChooser;
		}
		else
		{
			StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
			List<CDOMReference<Skill>> skillList =
					new ArrayList<CDOMReference<Skill>>();
			while (tok.hasMoreTokens())
			{
				String tokString = tok.nextToken();
				if (Constants.LST_ANY.equals(tokString))
				{
					Logging.errorPrint("Cannot use ANY and another qualifier: "
						+ value);
					return null;
				}
				else if (Constants.LST_LIST.equals(tokString))
				{
					Logging
						.errorPrint("Cannot use LIST and another qualifier: "
							+ value);
					return null;
				}
				else
				{
					skillList.add(context.ref.getCDOMReference(Skill.class,
						tokString));
				}
			}
			RefSetChooser<Skill> setChooser = new RefSetChooser<Skill>(skillList);
			CompoundAndChooser<Skill> chooser = new CompoundAndChooser<Skill>();
			chooser.addChoiceSet(setChooser);
			chooser.addChoiceSet(pcChooser);
			return chooser;
		}
	}
}
