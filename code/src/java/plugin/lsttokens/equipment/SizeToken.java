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

import pcgen.base.formula.Resolver;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.formula.FixedSizeResolver;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMSizeAdjustment;
import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with SIZE token
 */
public class SizeToken implements EquipmentLstToken, CDOMPrimaryToken<CDOMEquipment>
{

	public String getTokenName()
	{
		return "SIZE";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setSize(value, true);
		return true;
	}

	public boolean parse(LoadContext context, CDOMEquipment eq, String value)
	{
		CDOMSizeAdjustment size =
				context.ref.getAbbreviatedObject(CDOMSizeAdjustment.class,
					value);
		if (size == null)
		{
			Logging.errorPrint("Unable to find Size: " + value);
			return false;
		}
		context.getObjectContext().put(eq, ObjectKey.SIZE,
			new FixedSizeResolver(size));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMEquipment eq)
	{
		Resolver<CDOMSizeAdjustment> res =
				context.getObjectContext().getObject(eq, ObjectKey.SIZE);
		if (res == null)
		{
			return null;
		}
		return new String[]{res.toLSTFormat()};
	}

	public Class<CDOMEquipment> getTokenClass()
	{
		return CDOMEquipment.class;
	}
}
