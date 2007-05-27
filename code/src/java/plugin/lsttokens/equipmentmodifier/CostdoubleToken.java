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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;

/**
 * Deals with COSTDOUBLE token
 */
public class CostdoubleToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "COSTDOUBLE";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			// 514 abbreviation cleanup
//			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
//			{
//				Logging.errorPrint("You should use 'YES' or 'NO' as the " + getTokenName());
//				Logging.errorPrint("Abbreviations will fail after PCGen 5.12");
//			}
			set = true;
		}
		else
		{
			// 514 abbreviation cleanup
//			if (firstChar != 'N' && firstChar != 'n'
//				&& !value.equalsIgnoreCase("NO"))
//			{
//				Logging.errorPrint("You should use 'YES' or 'NO' as the "
//						+ getTokenName());
//				Logging.errorPrint("Abbreviations will fail after PCGen 5.12");
//			}
			set = false;
		}
		mod.setCostDouble(set);
		return true;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		Boolean set;
		if (value.equalsIgnoreCase("NO"))
		{
			set = Boolean.FALSE;
		}
		else if (value.equalsIgnoreCase("YES"))
		{
			set = Boolean.TRUE;
		}
		else
		{
			Logging.errorPrint("Did not understand " + getTokenName()
				+ " value: " + value);
			Logging.errorPrint("Must be YES or NO");
			return false;
		}
		context.obj.put(mod, ObjectKey.COST_DOUBLE, set);
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Boolean stacks = context.obj.getObject(mod, ObjectKey.COST_DOUBLE);
		if (stacks == null)
		{
			return null;
		}
		return new String[]{stacks.booleanValue() ? "YES" : "NO"};
	}
}
