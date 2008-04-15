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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Deals with BASEITEM token
 */
public class BaseitemToken extends AbstractToken implements EquipmentLstToken,
		CDOMPrimaryToken<CDOMEquipment>
{
	private static final Class<CDOMEquipment> EQUIPMENT_CLASS = CDOMEquipment.class;

	@Override
	public String getTokenName()
	{
		return "BASEITEM";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setBaseItem(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMEquipment eq, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext().put(eq, ObjectKey.BASE_ITEM,
				context.ref.getCDOMReference(EQUIPMENT_CLASS, value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMEquipment eq)
	{
		CDOMSingleRef<CDOMEquipment> ref = context.getObjectContext()
				.getObject(eq, ObjectKey.BASE_ITEM);
		if (ref == null)
		{
			return null;
		}
		return new String[] { ref.getLSTformat() };
	}

	public Class<CDOMEquipment> getTokenClass()
	{
		return CDOMEquipment.class;
	}
}
