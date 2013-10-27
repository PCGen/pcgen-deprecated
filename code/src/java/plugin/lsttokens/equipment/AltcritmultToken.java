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

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with ALTCRITMULT token
 */
public class AltcritmultToken implements EquipmentLstToken, CDOMPrimaryToken<CDOMEquipment>
{

	public String getTokenName()
	{
		return "ALTCRITMULT";
	}

	public boolean parse(Equipment eq, String value)
	{
		if ((value.length() > 0) && (value.charAt(0) == 'x'))
		{
			try
			{
				eq.setAltCritMult(Integer.parseInt(value.substring(1)));
			}
			catch (NumberFormatException nfe)
			{
				return false;
			}
			return true;
		}
		else if (value.equals("-"))
		{
			eq.setAltCritMult(-1);
			return true;
		}
		return false;
	}

	public boolean parse(LoadContext context, CDOMEquipment eq, String value)
	{
		Integer cm = null;
		if ((value.length() > 0) && (value.charAt(0) == 'x'))
		{
			try
			{
				cm = Integer.valueOf(value.substring(1));
				if (cm.intValue() <= 0)
				{
					Logging.errorPrint(getTokenName() + " cannot be <= 0");
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint(getTokenName()
					+ " was expecting an Integer: " + value);
				return false;
			}
		}
		else if ("-".equals(value))
		{
			cm = Integer.valueOf(-1);
		}
		if (cm == null)
		{
			Logging.errorPrint(getTokenName()
				+ " was expecting x followed by an integer "
				+ "or the special value '-' (representing no value)");
			return false;
		}
		EquipmentHead altHead = eq.getEquipmentHead(2);
		context.getObjectContext().put(altHead, IntegerKey.CRIT_MULT, cm);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMEquipment eq)
	{
		EquipmentHead head = eq.getEquipmentHeadReference(2);
		if (head == null)
		{
			return null;
		}
		Integer mult =
				context.getObjectContext().getInteger(head,
					IntegerKey.CRIT_MULT);
		if (mult == null)
		{
			return null;
		}
		int multInt = mult.intValue();
		String retString;
		if (multInt == -1)
		{
			retString = "-";
		}
		else
		{
			retString = "x" + multInt;
		}
		return new String[]{retString};
	}

	public Class<CDOMEquipment> getTokenClass()
	{
		return CDOMEquipment.class;
	}
}
