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

import pcgen.cdom.enumeration.EqModControl;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.util.Logging;

/**
 * Deals with MODS token
 */
public class ModsToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "MODS";
	}

	public boolean parse(Equipment eq, String value)
	{
		switch (value.charAt(0))
		{
			case 'R':
			case 'r':
				eq.setModifiersAllowed(true);
				eq.setModifiersRequired(true);
				break;

			case 'Y':
			case 'y':
				eq.setModifiersAllowed(true);
				eq.setModifiersRequired(false);
				break;

			case 'N':
			case 'n':
				eq.setModifiersAllowed(false);
				eq.setModifiersRequired(false);
				break;

			default:
				return false;
		}
		return true;
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			context.getObjectContext().put(eq, ObjectKey.MOD_CONTROL,
				EqModControl.valueOf(value));
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Invalid Mod Control provided in "
				+ getTokenName() + ": " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		EqModControl control =
				context.getObjectContext().getObject(eq, ObjectKey.MOD_CONTROL);
		if (control == null)
		{
			return null;
		}
		return new String[]{control.toString()};
	}
}
