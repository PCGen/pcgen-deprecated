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
package pcgen.cdom.choice;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public class ListKeyTransformer<T extends PObject> extends
		AbstractTransformer<T>
{

	private ListKey<? extends CDOMReference<T>> listkey;

	public ListKeyTransformer(ChoiceSet<? extends PObject> cs,
		ListKey<? extends CDOMReference<T>> lk)
	{
		super(cs);
		if (lk == null)
		{
			throw new IllegalArgumentException("List Key cannot be null");
		}
		listkey = lk;
	}

	public Set<T> getSet(PlayerCharacter pc)
	{
		Set<T> set = new HashSet<T>();
		for (PObject obj : getBaseSet(pc))
		{
			List<? extends CDOMReference<T>> objList = obj.getListFor(listkey);
			if (objList != null)
			{
				for (CDOMReference<T> ref : objList)
				{
					set.addAll(ref.getContainedObjects());
				}
			}
		}
		return set;
	}

	@Override
	public String toString()
	{
		return getCount().toString() + '<' + getMaxSelections().toString()
			+ Constants.PIPE + "DeRef: " + listkey;
	}

	@Override
	public int hashCode()
	{
		return transformerHashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof ListKeyTransformer))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		ListKeyTransformer<?> cs = (ListKeyTransformer) o;
		return listkey.equals(cs.listkey) && transformerEquals(cs);
	}

	public Class<T> getChoiceClass()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getLSTformat()
	{
		return listkey.toString();
	}
}
