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

import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.base.Restriction;
import pcgen.core.PObject;

public class SimpleRestriction<T extends PObject> implements Restriction<T>
{
	private final CDOMSingleRef<T> target;

	private final Class<T> targetClass;

	private final boolean negated;

	public SimpleRestriction(Class<T> cl, CDOMSingleRef<T> ref)
	{
		this(cl, ref, false);
	}

	public SimpleRestriction(Class<T> cl, CDOMSingleRef<T> ref, boolean negate)
	{
		target = ref;
		targetClass = cl;
		negated = negate;
	}

	public boolean qualifies(T pro)
	{
		if (!pro.getClass().equals(targetClass))
		{
			// CONSIDER This is an Error, or is false sufficient?
			return false;
		}
		return target.resolvesTo().equals(pro) ^ negated;
	}

	public Class<T> getRestrictedType()
	{
		return targetClass;
	}

	public String toLSTform()
	{
		String targetString = target.getLSTformat();
		StringBuilder sb = new StringBuilder(targetString.length() + 2);
		if (negated)
		{
			sb.append('[');
		}
		sb.append(targetString);
		if (negated)
		{
			sb.append(']');
		}
		return sb.toString();
	}
}
