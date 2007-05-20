/*
 * NumPagesToken.java
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Feb 22, 2006
 *
 * $Id$
 *
 */
package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Equipment;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.util.Logging;

/**
 * <code>NumPagesToken</code> deals with NUMPAGES token
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class NumPagesToken implements EquipmentLstToken
{

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "NUMPAGES";
	}

	/**
	 * @see pcgen.persistence.lst.EquipmentLstToken#parse(pcgen.core.Equipment,
	 *      java.lang.String)
	 */
	public boolean parse(Equipment eq, String value)
	{
		try
		{
			eq.setNumPages(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer pages = Integer.valueOf(value);
			if (pages.intValue() <= 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer > 0");
				return false;
			}
			context.obj.put(eq, IntegerKey.NUM_PAGES, pages);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " expected an integer.  Tag must be of the form: "
				+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Integer pages = context.obj.getInteger(eq, IntegerKey.NUM_PAGES);
		if (pages == null)
		{
			return null;
		}
		if (pages.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[]{pages.toString()};
	}

}
