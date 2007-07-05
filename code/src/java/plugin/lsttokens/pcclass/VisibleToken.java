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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		String visType = value.toUpperCase();
		if (visType.startsWith("Y"))
		{
			if (!"YES".equals(visType))
			{
				Logging.deprecationPrint("Abbreviation used in "
					+ getTokenName() + " in Class");
				Logging.deprecationPrint(" " + visType
					+ " is not a valid value for " + getTokenName());
				Logging
					.deprecationPrint(" assuming you meant YES, please use YES (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			pcclass.setVisibility(Visibility.YES);
		}
		else
		{
			if (!"NO".equals(visType))
			{
				Logging.deprecationPrint("Unexpected value used in "
					+ getTokenName() + " in Class");
				Logging.deprecationPrint(" " + visType
					+ " is not a valid value for " + getTokenName());
				Logging
					.deprecationPrint(" Valid values in Class are NO and YES");
				Logging
					.deprecationPrint(" assuming you meant NO, please use NO (exact String, upper case) in the LST file");
			}
			pcclass.setVisibility(Visibility.NO);
		}
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		Visibility vis;
		if (value.equals("NO"))
		{
			vis = Visibility.NO;
		}
		else if (value.equals("YES"))
		{
			vis = Visibility.YES;
		}
		else
		{
			Logging.errorPrint("Can't understand Visibility: " + value);
			return false;
		}
		context.obj.put(pcc, ObjectKey.VISIBILITY, vis);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Visibility vis = context.obj.getObject(pcc, ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.YES))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.NO))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis
				+ " is not a valid Visibility for a PCClass");
			return null;
		}
		return new String[]{visString};
	}
}
