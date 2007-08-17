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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.PObject;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class TypeLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "TYPE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		obj.setTypeInfo(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			context.obj.removeList(obj, ListKey.TYPE);
			return true;
		}
		if (value.charAt(0) == '.')
		{
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
					context.obj.removeFromList(obj, ListKey.TYPE, typeCon);
					removeType = false;
				}
				/*
				 * Hopefully this is not a problem that this unconditionally
				 * adds, even though Type is technically a SET? This problem
				 * exists elsewhere (in the ALTTYPE token of Equipment, for
				 * example), so I doubt this is a serious issue. Additionally,
				 * the tests that take place are for .contains, which will pass
				 * even if multiple items are present. The problem exists in
				 * removal - what if a type exists multiple times in the list?
				 */
				else
				{
					context.obj.addToList(obj, ListKey.TYPE, typeCon);
				}
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<Type> changes = context.obj.getListChanges(obj, ListKey.TYPE);
		if (changes == null)
		{
			return null;
		}
		List<String> returnList = new ArrayList<String>(2);
		if (changes.includesGlobalClear())
		{
			returnList.add(Constants.LST_DOT_CLEAR);
		}
		Collection<Type> list = changes.getAdded();
		if (list.size() == 9)
		{
			returnList.add(StringUtil.join(list, Constants.DOT));
		}
		if (returnList.isEmpty())
		{
			// Error
			return null;
		}
		return returnList.toArray(new String[returnList.size()]);
	}
}
