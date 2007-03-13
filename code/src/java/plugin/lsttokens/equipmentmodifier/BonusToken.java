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

import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with BONUS token
 */
public class BonusToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "BONUS";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.addBonusList(value);
		return true;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value) throws PersistenceLayerException
	{
		// FIXME Auto-generated method stub
		// Blank because I think this token is useless - <this> only valid
		// outside of eqmods...
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		// FIXME Auto-generated method stub
		return null;
	}
}
