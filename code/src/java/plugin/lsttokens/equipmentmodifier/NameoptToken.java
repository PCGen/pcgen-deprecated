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

import pcgen.cdom.enumeration.EqModNameOpt;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;

/**
 * Deals with NAMEOPT token
 */
public class NameoptToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "NAMEOPT";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setNamingOption(value);
		return true;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " cannot be empty");
			return false;
		}
		String optString = value;
		if (optString.startsWith("TEXT"))
		{
			if (optString.length() < 6 || optString.charAt(4) != '=')
			{
				Logging.errorPrint(getTokenName()
					+ " has invalid TEXT argument: " + value);
				return false;
			}
			optString = "TEXT";
			context.obj.put(mod, StringKey.NAME_TEXT, value.substring(5));
		}
		try
		{
			context.obj.put(mod, ObjectKey.NAME_OPT, EqModNameOpt
				.valueOf(optString));
			return true;
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Invalid Naming Option provided in "
				+ getTokenName() + ": " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		EqModNameOpt opt = context.obj.getObject(mod, ObjectKey.NAME_OPT);
		String text = context.obj.getString(mod, StringKey.NAME_TEXT);
		if (opt == null)
		{
			if (text == null)
			{
				return null;
			}
			else
			{
				context.addWriteMessage("Cannot have both NAME_TEXT without "
					+ "NAME_OPT in EquipmentModifier");
				return null;
			}
		}
		String retString;
		if (opt.equals(EqModNameOpt.TEXT))
		{
			if (text == null)
			{
				context.addWriteMessage("Must have NAME_TEXT with "
					+ "NAME_OPT TEXT in EquipmentModifier");
				return null;
			}
			else
			{
				retString = "TEXT=" + text;
			}
		}
		else
		{
			if (text == null)
			{
				retString = opt.toString();
			}
			else
			{
				context.addWriteMessage("Cannot have NAME_TEXT without "
					+ "NAME_OPT TEXT in EquipmentModifier");
				return null;
			}
		}
		return new String[]{retString};
	}
}
