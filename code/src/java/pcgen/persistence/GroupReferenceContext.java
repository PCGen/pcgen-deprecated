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
package pcgen.persistence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.CDOMAllRef;
import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.CDOMTypeRef;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.core.PObject;

public class GroupReferenceContext
{

	private Map<Class, CDOMGroupRef> allReferences =
			new HashMap<Class, CDOMGroupRef>();

	public <T extends PObject> CDOMGroupRef<T> getCDOMAllReference(Class<T> c)
	{
		CDOMGroupRef<T> obj = allReferences.get(c);
		if (obj == null)
		{
			obj = new CDOMAllRef<T>(c);
			allReferences.put(c, obj);
		}
		return obj;
	}

	private Map<Class, Map<String[], CDOMGroupRef>> typeReferences =
			new HashMap<Class, Map<String[], CDOMGroupRef>>();

	public <T extends PObject> CDOMGroupRef<T> getCDOMTypeReference(Class<T> c,
		String... val)
	{
		if (val.length == 0)
		{
			throw new IllegalArgumentException();
		}
		for (String s : val)
		{
			if (s.length() == 0)
			{
				throw new IllegalArgumentException(
					"Cannot build Reference with empty type");
			}
			else if (s.indexOf('.') != -1)
			{
				throw new IllegalArgumentException(
					"Cannot build Reference with type conaining a period");
			}
			else if (s.indexOf('=') != -1)
			{
				throw new IllegalArgumentException(
					"Cannot build Reference with type conaining an equals");
			}
		}
		Arrays.sort(val);
		Map<String[], CDOMGroupRef> trm = typeReferences.get(c);
		if (trm == null)
		{
			trm = new HashMap<String[], CDOMGroupRef>();
			typeReferences.put(c, trm);
		}
		/*
		 * TODO FIXME This is the SLOW method - better to actually use Jakarta
		 * Commons Collections and create a map that does the lookup based on
		 * deepEquals of an Array...
		 */
		for (Entry<String[], CDOMGroupRef> me : trm.entrySet())
		{
			if (Arrays.deepEquals(me.getKey(), val))
			{
				return me.getValue();
			}
		}
		// Didn't find the appropriate key, create new
		CDOMGroupRef<T> cgr = new CDOMTypeRef<T>(c, val);
		trm.put(val, cgr);
		return cgr;
	}

	private DoubleKeyMap<Class, Category, Map<String[], CDOMGroupRef>> categoryTypeRef =
			new DoubleKeyMap<Class, Category, Map<String[], CDOMGroupRef>>();

	public <T extends PObject & CategorizedCDOMObject<T>> CDOMGroupRef<T> getCategorizedCDOMTypeReference(
		Class<T> c, Category<T> cat, String... val)
	{
		for (String s : val)
		{
			if (s.length() == 0)
			{
				throw new IllegalArgumentException(
					"Cannot build Reference with empty type");
			}
			else if (s.indexOf('.') != -1)
			{
				throw new IllegalArgumentException(
					"Cannot build Reference with type conaining a period");
			}
			else if (s.indexOf('=') != -1)
			{
				throw new IllegalArgumentException(
					"Cannot build Reference with type conaining an equals");
			}
		}
		Arrays.sort(val);
		Map<String[], CDOMGroupRef> trm = categoryTypeRef.get(c, cat);
		if (trm == null)
		{
			trm = new HashMap<String[], CDOMGroupRef>();
			categoryTypeRef.put(c, cat, trm);
		}
		/*
		 * TODO FIXME This is the SLOW method - better to actually use Jakarta
		 * Commons Collections and create a map that does the lookup based on
		 * deepEquals of an Array...
		 */
		for (Entry<String[], CDOMGroupRef> me : trm.entrySet())
		{
			if (Arrays.deepEquals(me.getKey(), val))
			{
				return me.getValue();
			}
		}
		// Didn't find the appropriate key, create new
		CDOMGroupRef cgr = new CDOMTypeRef(c, val);
		trm.put(val, cgr);
		return cgr;
	}

	private DoubleKeyMap<Class, Category, CDOMGroupRef> categoryAllRef =
			new DoubleKeyMap<Class, Category, CDOMGroupRef>();

	public <T extends PObject & CategorizedCDOMObject<T>> CDOMGroupRef<T> getCategorizedCDOMAllReference(
		Class<T> c, Category<T> cat)
	{
		CDOMGroupRef obj = categoryAllRef.get(c, cat);
		if (obj == null)
		{
			obj = new CDOMAllRef<T>(c);
			categoryAllRef.put(c, cat, obj);
		}
		return obj;
	}

	public void clear()
	{
		categoryAllRef.clear();
		categoryTypeRef.clear();
		allReferences.clear();
		typeReferences.clear();
	}
}
