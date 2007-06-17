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
 * Current Ver: $Revision: 2959 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-05-20 00:02:34 -0400 (Sun, 20 May 2007) $
 */
package plugin.lsttokens.equipmentmodifier;

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;

/**
 * Deals with WT token
 */
public class WtToken implements EquipmentModifierLstToken
{

	/**
	 * Get token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "WT";
	}

	/**
	 * Parse WT token, set the equipment modifier weight
	 * 
	 * @param eq
	 * @param value
	 * @return true
	 */
	public boolean parse(EquipmentModifier mod, String value)
	{
		// Not valid in old system
		return false;
	}

	/*
	 * Note that weight is kept separate for speed of processing character
	 * weight... (will this actually work with containers like bags of holding?)
	 */
	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		try
		{
			BigDecimal weight = new BigDecimal(value);
			if (weight.compareTo(BigDecimal.ZERO) < 0)
			{
				Logging.errorPrint(getTokenName()
					+ " was expecting a decimal value >= 0 : " + value);
				return false;
			}
			context.obj.put(mod, ObjectKey.WEIGHT, weight);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Expected a Double for " + getTokenName() + ": "
				+ value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		BigDecimal weight = context.obj.getObject(mod, ObjectKey.WEIGHT);
		if (weight == null)
		{
			return null;
		}
		return new String[]{weight.toString()};
	}
}
