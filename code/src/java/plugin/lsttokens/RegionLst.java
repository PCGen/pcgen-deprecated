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
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * @author djones4
 * 
 */
public class RegionLst implements GlobalLstToken, CDOMPrimaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "REGION";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (anInt > -9)
		{
			obj.setRegionString(anInt + "|" + value);
		}
		else
		{
			obj.setRegionString("0|" + value);
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		context.getObjectContext().put(obj, StringKey.REGION, value);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		String region =
				context.getObjectContext().getString(obj, StringKey.REGION);
		if (region == null)
		{
			return null;
		}
		return new String[]{region};
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
