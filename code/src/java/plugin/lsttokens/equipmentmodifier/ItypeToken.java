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

import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with ITYPE token
 */
public class ItypeToken extends AbstractToken implements
		EquipmentModifierLstToken
{

	@Override
	public String getTokenName()
	{
		return "ITYPE";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setItemType(value);
		return true;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('.', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.DOT);
		while (tok.hasMoreTokens())
		{
			Type t = Type.getConstant(tok.nextToken());
			context.obj.addToList(mod, ListKey.ITEM_TYPES, t);
		}
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Changes<Type> changes =
				context.obj.getListChanges(mod, ListKey.ITEM_TYPES);
		if (changes == null)
		{
			return null;
		}
		return new String[]{StringUtil.join(changes.getAdded(), Constants.DOT)};
	}
}
