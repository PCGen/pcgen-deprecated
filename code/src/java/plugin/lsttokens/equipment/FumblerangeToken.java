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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Equipment;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with FUMBLERANGE token
 */
public class FumblerangeToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "FUMBLERANGE";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setFumbleRange(value);
		return true;
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (value.length() == 0)
		{
			return false;
		}
		context.getObjectContext().put(eq, StringKey.FUMBLE_RANGE,
			Constants.LST_DOT_CLEAR.equals(value) ? null : value);
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		String range =
				context.getObjectContext()
					.getString(eq, StringKey.FUMBLE_RANGE);
		if (range == null)
		{
			return null;
		}
		return new String[]{range};
	}
}
