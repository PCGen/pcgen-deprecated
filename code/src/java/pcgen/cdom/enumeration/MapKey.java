/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Created on June 18, 2005.
 * 
 * Current Ver: $Revision: 1447 $ Last Editor: $Author: boomer70 $ Last Edited:
 * $Date: 2006-10-03 21:56:03 -0400 (Tue, 03 Oct 2006) $
 */
package pcgen.cdom.enumeration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.CDOMReference;
import pcgen.core.PObject;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 * 
 * This is a Typesafe enumeration of legal Map Characteristics of an object.
 */
public final class MapKey<K, V>
{

	public static final MapKey<Class<? extends PObject>, CDOMReference<?>> QUALIFY =
			new MapKey<Class<? extends PObject>, CDOMReference<?>>();

	/** Private constructor to prevent instantiation of this class */
	private MapKey()
	{
		// Only allow instantation here
	}

	private static CaseInsensitiveMap<MapKey<?, ?>> map = null;

	private static void buildMap()
	{
		map = new CaseInsensitiveMap<MapKey<?, ?>>();
		Field[] fields = MapKey.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			int mod = fields[i].getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
				&& Modifier.isPublic(mod))
			{
				try
				{
					Object o = fields[i].get(null);
					if (o instanceof MapKey)
					{
						map.put(fields[i].getName(), (MapKey) o);
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
		/*
		 * CONSIDER Should this find a way to do a Two-Way Map or something to
		 * that effect?
		 */
		for (Map.Entry<?, MapKey<?, ?>> me : map.entrySet())
		{
			if (me.getValue() == this)
			{
				return me.getKey().toString();
			}
		}
		// Error
		return "";
	}

	public static Collection<MapKey<?, ?>> getAllConstants()
	{
		if (map == null)
		{
			buildMap();
		}
		return new HashSet<MapKey<?, ?>>(map.values());
	}
}
