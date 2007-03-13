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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ArmorType;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;

/**
 * Deals with ARMORTYPE token
 */
public class ArmortypeToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "ARMORTYPE";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setArmorType(value);
		return true;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint(getTokenName()
				+ " has no PIPE character: Must be of the form old|new");
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.errorPrint(getTokenName()
				+ " has too many PIPE characters: "
				+ "Must be of the form old|new");
			return false;
		}
		/*
		 * TODO Are the ArmorTypes really a subset of Encumbrence?
		 */
		try
		{
			ArmorType oldType = ArmorType.valueOf(value.substring(0, pipeLoc));
			ArmorType newType = ArmorType.valueOf(value.substring(pipeLoc + 1));
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}
		/*
		 * TODO Need some check if the Armor Types in value are not valid...
		 * does the above throw exceptions, etc.
		 */
		/*
		 * TODO This gets interesting to see how it should really be set - just
		 * like CHANGEPROF (global token)
		 */
		//FIXME a hack for now
		return true;
		// mod.setArmorType(value);
		// return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		// FIXME Auto-generated method stub
		return null;
	}
}
