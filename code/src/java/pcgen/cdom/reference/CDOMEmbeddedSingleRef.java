/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.reference;

import java.util.Collection;
import java.util.Collections;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.ObjectKey;

public class CDOMEmbeddedSingleRef<T extends PrereqObject> extends
		CDOMSingleRef<T>
{

	private final CDOMObject parentObj;

	private final ObjectKey<T> key;

	public CDOMEmbeddedSingleRef(CDOMObject obj, Class<T> cl, ObjectKey<T> ok,
		String nm)
	{
		super(cl, nm);
		parentObj = obj;
		key = ok;
	}

	@Override
	public boolean contains(T obj)
	{
		return obj != null && obj.equals(parentObj.get(key));
	}

	@Override
	public T resolvesTo()
	{
		return parentObj.get(key);
	}

	@Override
	public String getPrimitiveFormat()
	{
		return getName();
	}

	@Override
	public String getLSTformat()
	{
		return getName();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDOMEmbeddedSingleRef)
		{
			CDOMEmbeddedSingleRef<?> ref = (CDOMEmbeddedSingleRef) o;
			return getReferenceClass().equals(ref.getReferenceClass())
				&& getName().equals(ref.getName());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode() ^ getName().hashCode();
	}

	@Override
	public void addResolution(T obj)
	{
		throw new IllegalStateException("Cannot resolve an Embedded Reference");
	}

	@Override
	public Collection<T> getContainedObjects()
	{
		T ref = parentObj.get(key);
		if (ref == null)
		{
			return Collections.emptyList();
		}
		else
		{
			return Collections.singleton(ref);
		}
	}
}
