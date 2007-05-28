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
package pcgen.cdom.choice;

import java.util.Collection;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PObject;

public class ObjectFilter<T> implements ChoiceFilter<T>
{

	private HashMapToList<ObjectKey<?>, Object> assoc;

	public static <T extends PObject> ObjectFilter<T> getObjectFilter(
		Class<T> cl)
	{
		return new ObjectFilter<T>(cl);
	}

	public ObjectFilter(Class<T> cl)
	{
		super();
		if (cl == null)
		{
			throw new IllegalArgumentException("Choice Class cannot be null");
		}
	}

	public <A> void setObjectFilter(ObjectKey<A> ak, A val)
	{
		if (assoc == null)
		{
			assoc = new HashMapToList<ObjectKey<?>, Object>();
		}
		assoc.addToListFor(ak, val);
	}

	public <A> void setObjectFilter(ObjectKey<A> ak, Collection val)
	{
		if (assoc == null)
		{
			assoc = new HashMapToList<ObjectKey<?>, Object>();
		}
		assoc.addAllToListFor(ak, val);
	}

}
