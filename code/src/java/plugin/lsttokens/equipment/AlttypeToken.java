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

import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Equipment;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.util.Logging;

/**
 * Deals with ALTTYPE token
 */
public class AlttypeToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "ALTTYPE";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.addToAltTypeList(value);
		return true;
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		if (value.charAt(0) == '.')
		{
			if (Constants.LST_DOT_CLEAR.equals(value))
			{
				context.obj.removeList(eq, ListKey.ALT_TYPE);
				return true;
			}
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with . : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '.')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with . : " + value);
			return false;
		}
		if (value.indexOf("..") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator .. : " + value);
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(value.trim(), Constants.DOT);

		boolean removeType = false;
		while (aTok.hasMoreTokens())
		{
			String aType = aTok.nextToken();

			if (Constants.LST_ADD.equals(aType))
			{
				removeType = false;
			}
			else if (Constants.LST_REMOVE.equals(aType))
			{
				removeType = true;
			}
			else
			{
				Type typeCon = Type.getConstant(aType);
				if (removeType)
				{
					context.obj.removeFromList(eq, ListKey.ALT_TYPE, typeCon);
					removeType = false;
				}
				/*
				 * BUG TODO Not sure how to handle this - it's a SET!
				 */
				else if (!eq.containsInList(ListKey.ALT_TYPE, typeCon))
				{
					context.obj.addToList(eq, ListKey.ALT_TYPE, typeCon);
				}
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Changes<Type> changes =
				context.obj.getListChanges(eq, ListKey.ALT_TYPE);
		if (changes == null)
		{
			return null;
		}
		return new String[]{StringUtil.join(changes.getAdded(), Constants.DOT)};
	}
}
