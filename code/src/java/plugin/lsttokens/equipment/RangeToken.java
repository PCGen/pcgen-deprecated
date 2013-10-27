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

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with RANGE token
 */
public class RangeToken implements EquipmentLstToken, CDOMPrimaryToken<CDOMEquipment>
{

	public String getTokenName()
	{
		return "RANGE";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setRange(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMEquipment eq, String value)
	{
		try
		{
			Integer range = Integer.valueOf(value);
			if (range.intValue() < 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(eq, IntegerKey.RANGE, range);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
				+ " expected an integer.  Tag must be of the form: "
				+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, CDOMEquipment eq)
	{
		Integer range =
				context.getObjectContext().getInteger(eq, IntegerKey.RANGE);
		if (range == null)
		{
			return null;
		}
		if (range.intValue() < 0)
		{
			context
				.addWriteMessage(getTokenName() + " must be an integer >= 0");
			return null;
		}
		return new String[]{range.toString()};
	}

	public Class<CDOMEquipment> getTokenClass()
	{
		return CDOMEquipment.class;
	}
}
