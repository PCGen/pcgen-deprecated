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
package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;

/**
 * Deals with FUMBLERANGE token
 */
public class FumblerangeToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "FUMBLERANGE";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setFumbleRange(value);
		return true;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		context.getObjectContext().put(mod, StringKey.FUMBLE_RANGE, value);
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		String range =
				context.getObjectContext().getString(mod,
					StringKey.FUMBLE_RANGE);
		if (range == null)
		{
			return null;
		}
		return new String[]{range};
	}
}
