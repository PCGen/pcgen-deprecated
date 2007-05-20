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
package plugin.lsttokens.weaponprof;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.WeaponProfLstToken;
import pcgen.util.Logging;

/**
 * Class deals with HANDS Token
 */
public class HandsToken implements WeaponProfLstToken
{

	public String getTokenName()
	{
		return "HANDS";
	}

	public boolean parse(WeaponProf prof, String value)
	{
		prof.setHands(value);
		return true;
	}

	public boolean parse(LoadContext context, WeaponProf prof, String value)
	{
		int hands;
		if ("1IFLARGERTHANWEAPON".equals(value))
		{
			hands = Constants.HANDS_SIZEDEPENDENT;
		}
		else
		{
			try
			{
				hands = Integer.parseInt(value);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Misunderstood " + getTokenName() + ": "
					+ value);
				return false;
			}
			if (hands < 0)
			{
				Logging.errorPrint(getTokenName() + " value: " + value
					+ " must be greater than or equal to zero");
				return false;
			}
		}
		context.obj.put(prof, IntegerKey.HANDS, Integer.valueOf(hands));
		return true;
	}

	public String[] unparse(LoadContext context, WeaponProf prof)
	{
		Integer i = context.obj.getInteger(prof, IntegerKey.HANDS);
		/*
		 * Not a required Token, so it's possible it was never set. If so, don't
		 * write anything.
		 */
		if (i == null)
		{
			return null;
		}
		String hands;
		int intValue = i.intValue();
		if (intValue == Constants.HANDS_SIZEDEPENDENT)
		{
			hands = "1IFLARGERTHANWEAPON";
		}
		else if (intValue < 0)
		{
			context.addWriteMessage(getTokenName()
				+ " must be greater than or equal to zero or special value "
				+ Constants.HANDS_SIZEDEPENDENT + " for 1IFLARGERTHANWEAPON");
			return null;
		}
		else
		{
			hands = i.toString();
		}
		return new String[] { hands };
	}
}
