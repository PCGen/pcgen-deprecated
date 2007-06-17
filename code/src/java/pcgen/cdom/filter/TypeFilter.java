/*
 * Copyright 2006 (C) Tom Parker <thpr@sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on October 29, 2006.
 * 
 * Current Ver: $Revision: 1111 $ Last Editor: $Author: boomer70 $ Last Edited:
 * $Date: 2006-06-22 21:22:44 -0400 (Thu, 22 Jun 2006) $
 */
package pcgen.cdom.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.helper.ChoiceFilter;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public class TypeFilter implements ChoiceFilter<PObject>
{

	private List<Type> typeList;

	public TypeFilter(Collection<Type> types)
	{
		super();
		if (types == null)
		{
			throw new IllegalArgumentException("Type Collection cannot be null");
		}
		// Copy before test for empty (thread safety)
		typeList = new ArrayList<Type>(types);
		if (typeList.isEmpty())
		{
			throw new IllegalArgumentException(
				"Type Collection cannot be empty");
		}
	}

	public boolean remove(PlayerCharacter pc, PObject obj)
	{
		for (Type t : typeList)
		{
			if (!obj.containsInList(ListKey.TYPE, t))
			{
				return false;
			}
		}
		return true;
	}

	public String getLSTformat()
	{
		Set<String> set = new TreeSet<String>();
		for (Type t : typeList)
		{
			set.add(t.toString());
		}
		return "TYPE=" + StringUtil.join(set, ".");
	}

}
