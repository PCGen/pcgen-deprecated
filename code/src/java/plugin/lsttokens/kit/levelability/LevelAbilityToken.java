/*
 * LevelAbilityToken.java
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

package plugin.lsttokens.kit.levelability;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.kit.CDOMKitLevelAbility;
import pcgen.core.kit.KitLevelAbility;
import pcgen.persistence.lst.KitLevelAbilityLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * Level Ability token (a component of Kits)
 */
public class LevelAbilityToken implements KitLevelAbilityLstToken,
		CDOMSecondaryToken<CDOMKitLevelAbility>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "LEVELABILITY";
	}

	/**
	 * parse
	 * 
	 * @param kitLA
	 *            KitLevelAbility
	 * @param value
	 *            String
	 * @return boolean
	 */
	public boolean parse(KitLevelAbility kitLA, String value)
	{
		Logging.errorPrint("Ignoring second LEVELABILITY tag \"" + value
				+ "\" in Kit.");
		return false;
	}

	public Class<CDOMKitLevelAbility> getTokenClass()
	{
		return CDOMKitLevelAbility.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitLevelAbility kitLA,
			String value)
	{
		int equalLoc = value.indexOf('=');
		if (equalLoc == -1)
		{
			Logging.errorPrint(getTokenName() + " requires an =: " + value);
			return false;
		}
		if (equalLoc != value.lastIndexOf('='))
		{
			Logging.errorPrint(getTokenName() + " requires a single =: "
					+ value);
			return false;
		}
		String className = value.substring(0, equalLoc);
		String level = value.substring(equalLoc + 1);
		CDOMReference<CDOMPCClass> cl = context.ref.getCDOMReference(
				CDOMPCClass.class, className);
		try
		{
			Integer lvl = Integer.valueOf(level);
			if (lvl.intValue() <= 0)
			{
				Logging.errorPrint(getTokenName() + " expected an integer > 0");
				return false;
			}
			kitLA.setApplyLevel(lvl);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int>");
			return false;
		}
		kitLA.setApplyClass(cl);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitLevelAbility kitLA)
	{
		CDOMReference<CDOMPCClass> cl = kitLA.getApplyClass();
		Integer lvl = kitLA.getApplyLevel();
		return new String[] { cl.getLSTformat() + '=' + lvl };
	}
}
