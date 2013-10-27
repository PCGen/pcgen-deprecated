/*
 * VisibleToken.java
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

package plugin.lsttokens.kit.startpack;

import pcgen.cdom.inst.CDOMKit;
import pcgen.core.Kit;
import pcgen.persistence.lst.KitStartpackLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * VISIBLE token for KitsStartpack
 */
public class VisibleToken implements KitStartpackLstToken,
		CDOMSecondaryToken<CDOMKit>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "VISIBLE";
	}

	/**
	 * parse
	 * 
	 * @param kit
	 *            Kit
	 * @param value
	 *            String
	 * @return boolean
	 */
	public boolean parse(Kit kit, String value)
	{
		kit.setVisible(value);
		return true;
	}

	public Class<CDOMKit> getTokenClass()
	{
		return CDOMKit.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKit kit, String value)
	{
		Visibility vis;
		if (value.equals("QUALIFY"))
		{
			vis = Visibility.QUALIFY;
		}
		else if (value.equals("NO"))
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
		kit.setVisibility(vis);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKit kit)
	{
		Visibility vis = kit.getVisibility();
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.YES))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.QUALIFY))
		{
			visString = "QUALIFY";
		}
		else if (vis.equals(Visibility.NO))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis
					+ " is not a valid Visibility for a Kit");
			return null;
		}
		return new String[] { visString };
	}

}
