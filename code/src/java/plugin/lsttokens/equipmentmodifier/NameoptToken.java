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
		String optString = value;
		if (optString.startsWith("TEXT="))
		{
			optString = "TEXT";
			mod.put(StringKey.NAME_TEXT, value.substring(5));
		}
		try
		{
			mod.put(ObjectKey.NAME_OPT, EqModNameOpt.valueOf(optString));
			return true;
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Invalid Naming Option provided in "
				+ getTokenName() + ": " + value);
			return false;
		}
	}

	public String unparse(LoadContext context, EquipmentModifier mod)
	{
		EqModNameOpt opt = mod.get(ObjectKey.NAME_OPT);
		String text = mod.get(StringKey.NAME_TEXT);
		if (opt == null && text == null)
		{
			return null;
		}
		if (opt != null && text != null)
		{
			context.addWriteMessage("Cannot have both NAME_OPT and "
				+ "NAME_TEXT in EquipmentModifier");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName()).append(':');
		if (text == null)
		{
			assert opt != null;
			sb.append(opt);
		}
		else
		{
			sb.append("TEXT=").append(text);
		}
		return sb.toString();
	}
}
