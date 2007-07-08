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
 * Current Ver: $Revision: 3182 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-06-17 12:53:46 -0400 (Sun, 17 Jun 2007) $
 */
package plugin.lstcompatibility.equipment;

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentLstCompatibilityToken;

/**
 * Deals with WT token
 */
public class Wt512Token implements EquipmentLstCompatibilityToken
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

	/*
	 * Note that weight is kept separate for speed of processing character
	 * weight... (will this actually work with containers like bags of holding?)
	 */
	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Double.valueOf(value);
			return false;
		}
		catch (NumberFormatException nfe)
		{
			context.obj.put(eq, ObjectKey.WEIGHT, BigDecimal.ZERO);
			return true;
		}
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}
}
