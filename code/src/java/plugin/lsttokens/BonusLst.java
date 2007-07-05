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
import pcgen.core.PObject;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 */
public class BonusLst implements GlobalLstToken
{

	/**
	 * Returns token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "BONUS";
	}

	/**
	 * Parse BONUS token
	 * 
	 * @param obj
	 * @param value
	 * @param anInt
	 * @return true or false
	 */
	public boolean parse(PObject obj, String value, int anInt)
	{
		boolean result = false;
		value = CoreUtility.replaceAll(value, "<this>", obj.getKeyName());
		if (anInt > -9)
		{
			result = obj.addBonusList(anInt + "|" + value);
		}
		else
		{
			result = obj.addBonusList(value);
		}
		return result;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		value = CoreUtility.replaceAll(value, "<this>", obj.getKeyName());
		// TODO FIXME Hack to keep BONUSes working!
		return ((PObject) obj).addBonusList(value);
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
