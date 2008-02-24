/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with MAXLEVEL Token
 */
public class MaxlevelToken implements PCClassLstToken, CDOMPrimaryToken<CDOMPCClass>
{

	public String getTokenName()
	{
		return "MAXLEVEL";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		if ("NOLIMIT".equalsIgnoreCase(value))
		{
			pcclass.setMaxLevel(PCClass.NO_LEVEL_LIMIT);
			return true;
		}
		try
		{
			pcclass.setMaxLevel(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public boolean parse(LoadContext context, CDOMPCClass pcc, String value)
	{
		Integer lim;
		if ("NOLIMIT".equalsIgnoreCase(value))
		{
			lim = PCClass.NO_LEVEL_LIMIT;
		}
		else
		{
			try
			{
				lim = Integer.valueOf(value);
				if (lim.intValue() <= 0)
				{
					Logging.errorPrint("Value less than 1 is not valid for "
						+ getTokenName() + ": " + value);
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Value was not a number for "
					+ getTokenName() + ": " + value);
				return false;
			}
		}
		context.getObjectContext().put(pcc, IntegerKey.LEVEL_LIMIT, lim);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMPCClass pcc)
	{
		Integer lim =
				context.getObjectContext().getInteger(pcc,
					IntegerKey.LEVEL_LIMIT);
		if (lim == null)
		{
			return null;
		}
		String returnString = lim.toString();
		if (lim.equals(PCClass.NO_LEVEL_LIMIT))
		{
			returnString = "NOLIMIT";
		}
		else if (lim.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[]{returnString};
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
