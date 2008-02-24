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

import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Deals with DAMAGE token
 */
public class DamageToken implements EquipmentLstToken, CDOMPrimaryToken<CDOMEquipment>
{

	public String getTokenName()
	{
		return "DAMAGE";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setDamage(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMEquipment eq, String value)
	{
		context.getObjectContext().put(
			eq.getEquipmentHead(1),
			StringKey.DAMAGE, value);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMEquipment eq)
	{
		EquipmentHead head = eq.getEquipmentHeadReference(1);
		if (head == null)
		{
			return null;
		}
		String damage =
				context.getObjectContext().getString(head, StringKey.DAMAGE);
		if (damage == null)
		{
			return null;
		}
		return new String[]{damage};
	}

	public Class<CDOMEquipment> getTokenClass()
	{
		return CDOMEquipment.class;
	}
}
