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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;

public class CDOMCompoundAndReference<T extends PrereqObject> extends
		CDOMGroupRef<T>
{

	private ArrayList<CDOMReference<T>> references =
			new ArrayList<CDOMReference<T>>();

	public CDOMCompoundAndReference(Class<T> cl, String nm)
	{
		super(cl, nm);
	}

	public void addReference(CDOMReference<T> ref)
	{
		references.add(ref);
	}

	@Override
	public boolean contains(T obj)
	{
		for (CDOMReference<T> ref : references)
		{
			if (!ref.contains(obj))
			{
				return false;
			}
		}
		return !references.isEmpty();
	}

	public void trimToSize()
	{
		references.trimToSize();
	}

	@Override
	public String getPrimitiveFormat()
	{
		StringBuilder sb = new StringBuilder();
		boolean needComma = false;
		for (CDOMReference<?> ref : references)
		{
			if (needComma)
			{
				sb.append(',');
			}
			needComma = true;
			sb.append(ref.getPrimitiveFormat());
		}
		return sb.toString();
	}

	@Override
	public String getLSTformat()
	{
		return ReferenceUtilities.joinLstFormat(references, Constants.COMMA);
	}

	@Override
	public void addResolution(T obj)
	{
		throw new IllegalStateException(
			"CompoundReference cannot be given a resolution");
	}

	@Override
	public int getObjectCount()
	{
		return getContainedObjects().size();
	}

	@Override
	public Collection<T> getContainedObjects()
	{
		boolean first = true;
		Set<T> set = new HashSet<T>();
		for (CDOMReference<T> ref : references)
		{
			if (first)
			{
				set.addAll(ref.getContainedObjects());
			}
			else
			{
				set.retainAll(ref.getContainedObjects());
			}
		}
		return set;
	}
}
