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
 * Current Ver: $Revision: 2959 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-05-20 00:02:34 -0400 (Sun, 20 May 2007) $
 */
package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with DAMAGE token
 */
public class DamageToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "DAMAGE";
	}

	public boolean parse(EquipmentModifier eq, String value)
	{
		// Not valid in old system
		return false;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		context.getObjectContext().put(mod, StringKey.DAMAGE, value);
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		String damage =
				context.getObjectContext().getString(mod, StringKey.DAMAGE);
		if (damage == null)
		{
			return null;
		}
		return new String[]{damage};
	}
}
