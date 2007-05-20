/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
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
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class UmultLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "UMULT";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (anInt > -9)
		{
			obj.addUmult(anInt + "|" + value);
		}
		else
		{
			obj.addUmult(value);
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			context.obj.put(obj, IntegerKey.UMULT, null);
		}
		else
		{
			try
			{
				Integer i = Integer.valueOf(value);
				if (i.intValue() <= 0)
				{
					Logging.errorPrint("Invalid " + getTokenName() + ": "
						+ value);
					Logging.errorPrint("  Expecting a positive integer");
					return false;
				}
				context.obj.put(obj, IntegerKey.UMULT, i);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Invalid " + getTokenName() + ": " + value);
				Logging.errorPrint("  Expecting an integer");
				return false;
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Integer mult = context.obj.getInteger(obj, IntegerKey.UMULT);
		if (mult == null)
		{
			return null;
		}
		if (mult.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[]{mult.toString()};
	}
}
