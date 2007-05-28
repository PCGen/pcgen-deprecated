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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.choice.PCChooser;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class CSkillsToken implements ChooseLstToken
{

	public boolean parse(PObject po, String value)
	{
		if (value == null)
		{
			// No args - legal
			po.setChoiceString(value);
			return true;
		}
		Logging.errorPrint("CHOOSE:" + getTokenName()
			+ " may not have arguments: " + value);
		return false;
	}

	public String getTokenName()
	{
		return "CSKILLS";
	}

	public ChoiceSet<?> parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (value != null)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " may not have arguments: " + value);
			return null;
		}
		// No args - legal
		/*
		 * TODO Not sure if this is really the correct reference here - what is
		 * the place where one can gather ALL of the Class Skills for a given
		 * PC?
		 */
		PCChooser<Skill> chooser = new PCChooser<Skill>(Skill.class);
		chooser.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);
		return chooser;
	}
}
