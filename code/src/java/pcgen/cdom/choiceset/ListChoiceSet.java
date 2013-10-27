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
package pcgen.cdom.choiceset;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.character.CharacterDataStore;

public class ListChoiceSet<T extends CDOMObject> implements
		PrimitiveChoiceSet<T>
{

	private final CDOMList<T> list;

	public ListChoiceSet(CDOMList<T> cdomList)
	{
		super();
		if (cdomList == null)
		{
			throw new IllegalArgumentException(
				"Choice Collection cannot be null");
		}
		list = cdomList;
	}

	public String getLSTformat()
	{
		return "LIST:" + list.toString();
	}

	public Class<T> getChoiceClass()
	{
		return list.getListClass();
	}

	public Set<T> getSet(CharacterDataStore pc)
	{
		/*
		 * FUTURE This seems to be wrapping a Collection into a Set... can
		 * getSet relax to a Collection or can getCODMListContents tighten to a
		 * set?
		 */
		return new HashSet<T>(pc.getCDOMListContents(list));
	}

	@Override
	public int hashCode()
	{
		return list.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof ListChoiceSet)
		{
			ListChoiceSet<?> other = (ListChoiceSet<?>) o;
			return list.equals(other.list);
		}
		return false;
	}
}
