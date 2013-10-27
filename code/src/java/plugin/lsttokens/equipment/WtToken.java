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
package plugin.lsttokens.equipment;

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with WT token
 */
public class WtToken implements EquipmentLstToken, CDOMPrimaryToken<CDOMEquipment>
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
	 * Parse WT token, set the equipment weight
	 * 
	 * @param eq
	 * @param value
	 * @return true
	 */
	public boolean parse(Equipment eq, String value)
	{
		eq.setWeight(value);
		return true;
	}

	/*
	 * Note that weight is kept separate for speed of processing character
	 * weight... (will this actually work with containers like bags of holding?)
	 */
	public boolean parse(LoadContext context, CDOMEquipment eq, String value)
	{
		try
		{
			BigDecimal weight = new BigDecimal(value);
			if (weight.compareTo(BigDecimal.ZERO) < 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " was expecting a decimal value >= 0 : " + value);
				return false;
			}
			context.getObjectContext().put(eq, ObjectKey.WEIGHT, weight);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Expected a Double for "
				+ getTokenName() + ": " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, CDOMEquipment eq)
	{
		BigDecimal weight =
				context.getObjectContext().getObject(eq, ObjectKey.WEIGHT);
		if (weight == null)
		{
			return null;
		}
		return new String[]{weight.toString()};
	}

	public Class<CDOMEquipment> getTokenClass()
	{
		return CDOMEquipment.class;
	}
}
