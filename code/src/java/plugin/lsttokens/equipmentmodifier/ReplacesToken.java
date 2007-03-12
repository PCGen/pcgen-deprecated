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

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;

/**
 * Deals with REPLACES token
 */
public class ReplacesToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "REPLACES";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setReplacement(value);
		return true;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " argument may not be empty : "
				+ value);
			return false;
		}
		if (value.charAt(0) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with , : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with , : " + value);
			return false;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}
		/*
		 * FIXME Should this actually be a Factory of some sort, since it IS
		 * mosifying a piece of equipment? - yes, these are REMOVERs :) - akin
		 * to how TEMPLATE:REMOVE works
		 */
		StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);
		while (tok.hasMoreTokens())
		{
			CDOMSimpleSingleRef<EquipmentModifier> ref =
					context.ref.getCDOMReference(EquipmentModifier.class, tok
						.nextToken());
			mod.addToListFor(ListKey.REPLACED_KEYS, ref);
		}
		return true;
	}

	public String unparse(LoadContext context, EquipmentModifier mod)
	{
		List<CDOMSimpleSingleRef<EquipmentModifier>> keys =
				mod.getListFor(ListKey.REPLACED_KEYS);
		if (keys == null || keys.isEmpty())
		{
			return null;
		}
		StringBuilder sb =
				new StringBuilder().append(getTokenName()).append(':');
		boolean needComma = false;
		for (CDOMSimpleSingleRef<EquipmentModifier> at : keys)
		{
			if (needComma)
			{
				sb.append(Constants.COMMA);
			}
			needComma = true;
			sb.append(at.getLSTformat());
		}
		return sb.toString();
	}
}
