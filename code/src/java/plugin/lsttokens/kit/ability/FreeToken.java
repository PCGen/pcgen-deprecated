/*
 * AbilityToken.java
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
 * Created on March 3, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.ability;

import pcgen.cdom.kit.CDOMKitAbility;
import pcgen.core.kit.KitAbilities;
import pcgen.persistence.lst.KitAbilityLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * FREE Token for KitAbility
 */
public class FreeToken implements KitAbilityLstToken,
		CDOMSecondaryToken<CDOMKitAbility>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "FREE";
	}

	public boolean parse(KitAbilities kitAbility, String value)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.deprecationPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName());
				Logging
						.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
			set = true;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n'
					&& !value.equalsIgnoreCase("NO"))
			{
				Logging.deprecationPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName());
				Logging
						.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
			set = false;
		}
		kitAbility.setFree(set);
		return true;
	}

	public Class<CDOMKitAbility> getTokenClass()
	{
		return CDOMKitAbility.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitAbility kitAbility,
			String value)
	{
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
				{
					Logging.errorPrint("You should use 'YES' or 'NO' as the "
							+ getTokenName() + ": " + value);
					return false;
				}
			}
			set = Boolean.FALSE;
		}
		kitAbility.setFree(set);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitAbility kitAbility)
	{
		Boolean mult = kitAbility.getFree();
		if (mult == null)
		{
			return null;
		}
		return new String[] { mult.booleanValue() ? "YES" : "NO" };
	}
}
