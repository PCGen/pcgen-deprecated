/*
 * Copyright 2007 (C) Tom Parker <thpr@sourceforge.net>
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
 */
package pcgen.cdom.lists;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.AssociatedObject;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.core.spell.Spell;

public class PCGenLists
{
	private final DoubleKeyMap<CDOMList<? extends CDOMObject>, CDOMObject, AssociatedPrereqObject> lists =
			new DoubleKeyMap<CDOMList<? extends CDOMObject>, CDOMObject, AssociatedPrereqObject>();

	public <LT extends CDOMObject, T extends CDOMList<LT>> Set<T> getLists(
		Class<T> name)
	{
		Set returnSet = new HashSet();
		for (CDOMList<? extends CDOMObject> list : lists.getKeySet())
		{
			if (name.equals(list.getReferenceClass()))
			{
				returnSet.add(list);
			}
		}
		return returnSet;
	}

	public <LT extends CDOMObject, T extends CDOMList<LT>> Collection<LT> getListContents(
		T list)
	{
		Set secondaryKeySet = lists.getSecondaryKeySet(list);
		return secondaryKeySet;
	}

	public <LT extends CDOMObject, T extends CDOMList<LT>> AssociatedObject getListAssociation(
		T sl, LT sk)
	{
		return lists.get(sl, sk);
	}

	public <LT extends CDOMObject, T extends CDOMList<LT>> void addToList(
		T list, CDOMReference<LT> ref, AssociatedPrereqObject apo)
	{
		for (LT obj : ref.getContainedObjects())
		{
			lists.put(list, obj, apo);
		}
	}

	public <LT extends CDOMObject, T extends CDOMList<LT>> boolean listContains(
		T list, LT obj)
	{
		return lists.containsKey(list, obj);
	}
}
