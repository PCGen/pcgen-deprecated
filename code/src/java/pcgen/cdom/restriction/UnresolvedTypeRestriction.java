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

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Restriction;
import pcgen.cdom.enumeration.Type;
import pcgen.core.PObject;

public class UnresolvedTypeRestriction<T extends PObject> implements
		Restriction<T>
{

	private final Type targetType;

	private final Class<T> targetClass;

	private final boolean negated;

	public UnresolvedTypeRestriction(Class<T> cl, Type type)
	{
		this(cl, type, false);
	}

	public UnresolvedTypeRestriction(Class<T> cl, Type type, boolean negate)
	{
		targetType = type;
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
		StringTokenizer st = new StringTokenizer(pro.getType(), Constants.DOT);
		while (st.hasMoreTokens())
		{
			if (st.nextToken().equals(targetType))
			{
				return !negated;
			}
		}
		return negated;
	}

	public Class<T> getRestrictedType()
	{
		return targetClass;
	}

	public String toLSTform()
	{
		String targetTypeString = targetType.toString();
		StringBuilder sb = new StringBuilder(targetTypeString.length() + 2);
		if (negated)
		{
			sb.append('[');
		}
		sb.append(targetType);
		if (negated)
		{
			sb.append(']');
		}
		return sb.toString();
	}
}
