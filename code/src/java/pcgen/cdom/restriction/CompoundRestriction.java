/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.restriction;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.Restriction;

public class CompoundRestriction<T extends PrereqObject> implements
		Restriction<T>
{

	private final Class<T> restrictedClass;

	private final int count;

	private final List<Restriction<T>> resList =
			new ArrayList<Restriction<T>>();

	public CompoundRestriction(Class<T> name, int i)
	{
		if (i < 1)
		{
			throw new IllegalArgumentException(
				"CompoundRestriction must require at least one sub-restriction to pass");
		}
		count = i;
		if (name == null)
		{
			throw new IllegalArgumentException(
				"CompoundRestriction requires a restricted class");
		}
		restrictedClass = name;
	}

	public void addRestriction(Restriction<T> t)
	{
		if (t == null)
		{
			throw new IllegalArgumentException(
				"Cannot add a null restriction to CompoundRestriction");
		}
		// CONSIDER Should this be some form of assignable, to allow
		// a general restriction? Could also be confusing, tho'
		// if (!slotClass.isAssignableFrom(o.getClass())) {
		if (!t.getRestrictedType().equals(restrictedClass))
		{
			throw new IllegalArgumentException(
				"Cannot add a restriction for Class "
					+ t.getRestrictedType().getSimpleName()
					+ " to a CompoundRestriction for Class "
					+ restrictedClass.getSimpleName());
		}
		resList.add(t);
	}

	public boolean qualifies(T o)
	{
		if (!o.getClass().equals(restrictedClass))
		{
			// CONSIDER This is an Error, or is false sufficient?
			return false;
		}
		int passCount = 0;
		for (Restriction<T> r : resList)
		{
			if (r.qualifies(o))
			{
				passCount++;
				if (passCount >= count)
				{
					return true;
				}
			}
		}
		return false;
	}

	public Class<T> getRestrictedType()
	{
		return restrictedClass;
	}

}
