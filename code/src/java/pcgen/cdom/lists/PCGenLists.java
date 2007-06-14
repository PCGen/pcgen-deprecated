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
import java.util.Set;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.AssociatedObject;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;

public class PCGenLists
{
	private final DoubleKeyMap<CDOMList<? extends CDOMObject>, CDOMObject, AssociatedPrereqObject> lists =
			new DoubleKeyMap<CDOMList<? extends CDOMObject>, CDOMObject, AssociatedPrereqObject>();

	public <LT extends CDOMObject, T extends CDOMList<LT>> Set<T> getLists(
		Class<T> name)
	{
		DoubleKeyMap<T, LT, AssociatedPrereqObject> dkm =
				(DoubleKeyMap<T, LT, AssociatedPrereqObject>) lists;
		return dkm.getKeySet();
	}

	public <LT extends CDOMObject, T extends CDOMList<LT>> Collection<LT> getListContents(
		T list)
	{
		DoubleKeyMap<T, LT, AssociatedPrereqObject> dkm =
				(DoubleKeyMap<T, LT, AssociatedPrereqObject>) lists;
		return dkm.getSecondaryKeySet(list);
	}

	public <LT extends CDOMObject, T extends CDOMList<LT>> AssociatedObject getListAssociation(
		T sl, LT sk)
	{
		DoubleKeyMap<T, LT, AssociatedPrereqObject> dkm =
				(DoubleKeyMap<T, LT, AssociatedPrereqObject>) lists;
		return dkm.get(sl, sk);
	}

	public <LT extends CDOMObject, T extends CDOMList<LT>> void addToList(
		T list, CDOMReference<LT> ref, AssociatedPrereqObject apo)
	{
		for (LT obj : ref.getContainedObjects())
		{
			lists.put(list, obj, apo);
		}
	}
}
