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

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.kit.CDOMKitAbility;
import pcgen.core.kit.KitAbilities;
import pcgen.persistence.lst.KitAbilityLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * Deals with ABILITY lst token within KitAbility
 */
public class AbilityToken extends AbstractToken implements KitAbilityLstToken,
		CDOMSecondaryToken<CDOMKitAbility>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "ABILITY";
	}

	/**
	 * Parse Ability token for kit
	 * 
	 * @param kitAbility
	 * @param value
	 * @return false
	 */
	public boolean parse(KitAbilities kitAbility, String value)
	{
		Logging.errorPrint("Ignoring second FEAT or ABILITY tag \"" + value
				+ "\" in Kit.");
		return false;
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
		if (isEmpty(value))
		{
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging
					.addParseMessage(
							Logging.LST_ERROR,
							"No pipe found.  ABILITY token "
									+ "in a Kit requires CATEGORY=<cat>|<ability>,<ability>");
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Two pipes found.  ABILITY token "
							+ "in a Kit requires CATEGORY=<cat>|<abilities>");
			return false;
		}
		String catString = value.substring(0, pipeLoc);
		if (!catString.startsWith("CATEGORY="))
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"No CATEGORY= found.  ABILITY token "
							+ "in a Kit requires CATEGORY=<cat>|<abilities>");
			return false;
		}
		if (catString.length() < 10)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"No category found.  ABILITY token "
							+ "in a Kit requires CATEGORY=<cat>|<abilities>");
			return false;
		}
		CDOMAbilityCategory cat = CDOMAbilityCategory.valueOf(catString
				.substring(9));
		String rest = value.substring(pipeLoc + 1);
		if (rest == null || rest.length() == 0)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"No abilities found.  ABILITY token "
							+ "in a Kit requires CATEGORY=<cat>|<abilities>");
			return false;
		}
		kitAbility.setAbility(TokenUtilities.getTypeOrPrimitive(context,
				CDOMAbility.class, cat, value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitAbility kitAbility)
	{
		CDOMReference<CDOMAbility> ref = kitAbility.getAbility();
		if (ref == null)
		{
			return null;
		}
		return new String[] { ref.getLSTformat() };
	}
}
