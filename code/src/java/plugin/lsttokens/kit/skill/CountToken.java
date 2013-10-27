/*
 * CountToken.java
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
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.skill;

import pcgen.cdom.kit.CDOMKitSkill;
import pcgen.core.kit.KitSkill;
import pcgen.persistence.lst.KitSkillLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * COUNT Token for KitSkill
 */
public class CountToken implements KitSkillLstToken,
		CDOMSecondaryToken<CDOMKitSkill>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "COUNT";
	}

	/**
	 * parse
	 * 
	 * @param kitSkill
	 *            KitSkill
	 * @param value
	 *            String
	 * @return boolean
	 */
	public boolean parse(KitSkill kitSkill, String value)
	{
		kitSkill.setChoiceCount(value);
		return true;
	}

	public Class<CDOMKitSkill> getTokenClass()
	{
		return CDOMKitSkill.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitSkill kitSkill,
			String value)
	{
		try
		{
			Integer quan = Integer.valueOf(value);
			if (quan.intValue() <= 0)
			{
				Logging.errorPrint(getTokenName() + " expected an integer > 0");
				return false;
			}
			kitSkill.setCount(quan);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int>");
			return false;
		}

	}

	public String[] unparse(LoadContext context, CDOMKitSkill kitSkill)
	{
		Integer bd = kitSkill.getCount();
		if (bd == null)
		{
			return null;
		}
		return new String[] { bd.toString() };
	}
}
