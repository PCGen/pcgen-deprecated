/*
 * GearToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.gear;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.kit.CDOMKitGear;
import pcgen.core.kit.KitGear;
import pcgen.persistence.lst.KitGearLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * GEAR Token for KitGear
 */
public class GearToken extends AbstractToken implements KitGearLstToken,
		CDOMSecondaryToken<CDOMKitGear>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "GEAR";
	}

	/**
	 * parse
	 * 
	 * @param kitGear
	 *            KitGear
	 * @param value
	 *            String
	 * @return boolean
	 */
	public boolean parse(KitGear kitGear, String value)
	{
		Logging
				.errorPrint("Ignoring second GEAR tag \"" + value
						+ "\" in Kit.");
		return false;
	}

	public Class<CDOMKitGear> getTokenClass()
	{
		return CDOMKitGear.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitGear kitGear, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		kitGear.setEquipment(context.ref
				.getCDOMReference(CDOMEquipment.class, value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitGear kitGear)
	{
		CDOMReference<CDOMEquipment> ref = kitGear.getEquipment();
		if (ref == null)
		{
			return null;
		}
		return new String[] { ref.getLSTformat() };
	}

}
