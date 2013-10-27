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
package plugin.lsttokens.skill;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.cdom.inst.CDOMSkill;
import pcgen.core.Skill;
import pcgen.persistence.lst.SkillLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ACHECK Token
 */
public class AcheckToken implements SkillLstToken, CDOMPrimaryToken<CDOMSkill>
{

	public String getTokenName()
	{
		return "ACHECK";
	}

	public boolean parse(Skill skill, String value)
	{
		skill.setACheck(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMSkill skill, String value)
	{
		try
		{
			context.getObjectContext().put(skill, ObjectKey.ARMOR_CHECK,
				SkillArmorCheck.valueOf(value));
			return true;
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Misunderstood " + getTokenName() + ": " + value
				+ " is not a valid value");
			return false;
		}
	}

	public String[] unparse(LoadContext context, CDOMSkill skill)
	{
		SkillArmorCheck sac =
				context.getObjectContext().getObject(skill,
					ObjectKey.ARMOR_CHECK);
		if (sac == null)
		{
			return null;
		}
		return new String[]{sac.toString()};
	}

	public Class<CDOMSkill> getTokenClass()
	{
		return CDOMSkill.class;
	}
}
