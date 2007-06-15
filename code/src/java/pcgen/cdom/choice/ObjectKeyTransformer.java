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
import java.util.Set;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public class ObjectKeyTransformer<T extends PObject> extends
		AbstractTransformer<T>
{

	private ObjectKey<? extends CDOMReference<T>> objkey;

	public ObjectKeyTransformer(ChoiceSet<? extends PObject> cs,
		ObjectKey<? extends CDOMReference<T>> lk)
	{
		super(cs);
		if (lk == null)
		{
			throw new IllegalArgumentException("List Key cannot be null");
		}
		objkey = lk;
	}

	public Set<T> getSet(PlayerCharacter pc)
	{
		Set<T> set = new HashSet<T>();
		for (PObject obj : getBaseSet(pc))
		{
			CDOMReference<T> ref = obj.get(objkey);
			set.addAll(ref.getContainedObjects());
		}
		return set;
	}

	@Override
	public String toString()
	{
		return getCount().toString() + '<' + getMaxSelections().toString()
			+ Constants.PIPE + "DeRefObj: " + objkey;
	}

	@Override
	public int hashCode()
	{
		return transformerHashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof ObjectKeyTransformer))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		ObjectKeyTransformer<?> cs = (ObjectKeyTransformer) o;
		return objkey.equals(cs.objkey) && transformerEquals(cs);
	}
}
