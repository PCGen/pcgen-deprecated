/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on June 18, 2005.
 *
 * Current Ver: $Revision: 513 $
 * Last Editor: $Author: soulcatcher $
 * Last Edited: $Date: 2006-03-29 12:17:43 -0500 (Wed, 29 Mar 2006) $
 */
package pcgen.cdom.enumeration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.core.WeaponProf;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 * 
 * This is a Typesafe enumeration of legal List Characteristics of an object.
 */
public final class AssociationListKey<T>
{

	public static final AssociationListKey<WeaponProf> WEAPON_PROF =
			new AssociationListKey<WeaponProf>();

	private static CaseInsensitiveMap<AssociationListKey<?>> map = null;

	private AssociationListKey()
	{
		// Only allow instantation here
	}

	public T cast(Object o)
	{
		return (T) o;
	}

	public static <OT> AssociationListKey<OT> getKeyFor(Class<OT> c, String s)
	{
		if (map == null)
		{
			buildMap();
		}
		/*
		 * CONSIDER This is actually not type safe, there is a case of asking
		 * for a String a second time with a different Class that ObjectKey
		 * currently does not handle. Two solutions: One, store this in a
		 * Two-Key map and allow a String to map to more than one ObjectKey
		 * given different output types (considered confusing) or Two, store the
		 * Class and validate that with a an error message if a different class
		 * is requested.
		 */
		AssociationListKey<OT> o = (AssociationListKey<OT>) map.get(s);
		if (o == null)
		{
			o = new AssociationListKey<OT>();
			map.put(s, o);
		}
		return o;
	}

	private static void buildMap()
	{
		map = new CaseInsensitiveMap<AssociationListKey<?>>();
		Field[] fields = AssociationListKey.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			int mod = fields[i].getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
				&& Modifier.isPublic(mod))
			{
				try
				{
					Object o = fields[i].get(null);
					if (o instanceof AssociationListKey)
					{
						map.put(fields[i].getName(), (AssociationListKey<?>) o);
					}
				}
				catch (IllegalArgumentException e)
				{
					throw new InternalError();
				}
				catch (IllegalAccessException e)
				{
					throw new InternalError();
				}
			}
		}
	}

	@Override
	public String toString()
	{
		if (map == null)
		{
			buildMap();
		}
		/*
		 * CONSIDER Should this find a way to do a Two-Way Map or something to
		 * that effect?
		 */
		for (Map.Entry<?, AssociationListKey<?>> me : map.entrySet())
		{
			if (me.getValue() == this)
			{
				return me.getKey().toString();
			}
		}
		// Error
		return "";
	}
}
