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
 * Current Ver: $Revision: 1447 $
 * Last Editor: $Author: boomer70 $
 * Last Edited: $Date: 2006-10-03 21:56:03 -0400 (Tue, 03 Oct 2006) $
 */
package pcgen.cdom.enumeration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.helper.Capacity;
import pcgen.cdom.helper.Quality;
import pcgen.core.Alignment;
import pcgen.core.Description;
import pcgen.core.EquipmentModifier;
import pcgen.core.PCClass;
import pcgen.core.WeaponProf;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 * 
 * This is a Typesafe enumeration of legal List Characteristics of an object.
 */
public final class ListKey<T>
{

	public static final ListKey<String> UDAM = new ListKey<String>();

	public static final ListKey<Type> TYPE = new ListKey<Type>();

	public static final ListKey<RaceSubType> RACESUBTYPE =
			new ListKey<RaceSubType>();

	public static final ListKey<Pantheon> PANTHEON = new ListKey<Pantheon>();

	public static final ListKey<CDOMSimpleSingleRef<WeaponProf>> DEITYWEAPON =
			new ListKey<CDOMSimpleSingleRef<WeaponProf>>();

	public static final ListKey<RacePantheon> RACE_PANTHEON =
			new ListKey<RacePantheon>();

	public static final ListKey<SpellSubSchool> SPELL_SUBSCHOOL =
			new ListKey<SpellSubSchool>();

	public static final ListKey<SpellSchool> SPELL_SCHOOL =
			new ListKey<SpellSchool>();

	public static final ListKey<SpellDescriptor> SPELL_DESCRIPTOR =
			new ListKey<SpellDescriptor>();

	public static final ListKey<String> VARIANTS = new ListKey<String>();

	public static final ListKey<CDOMSimpleSingleRef<EquipmentModifier>> REPLACED_KEYS =
			new ListKey<CDOMSimpleSingleRef<EquipmentModifier>>();

	public static final ListKey<Type> ITEM_TYPES = new ListKey<Type>();

	public static final ListKey<Type> ALT_TYPE = new ListKey<Type>();

	public static final ListKey<Type> PROFICIENCY_TYPES = new ListKey<Type>();

	public static final ListKey<Alignment> FOLLOWER_ALIGN =
			new ListKey<Alignment>();

	public static final ListKey<Capacity> CAPACITY = new ListKey<Capacity>();

	public static final ListKey<Description> DESC = new ListKey<Description>();

	public static final ListKey<RaceSubType> REMOVED_RACESUBTYPE =
			new ListKey<RaceSubType>();

	public static final ListKey<Integer> HITDICE_ADVANCEMENT =
			new ListKey<Integer>();

	public static final ListKey<CDOMReference<PCClass>> FAVORED_CLASS =
			new ListKey<CDOMReference<PCClass>>();

	public static final ListKey<Quality> QUALITY = new ListKey<Quality>();

	public static final ListKey<Type> ITEM = new ListKey<Type>();

	public static final ListKey<Type> PROHIBITED_ITEM = new ListKey<Type>();

	/** Private constructor to prevent instantiation of this class */
	private ListKey()
	{
		// Only allow instantation here
	}

	private static CaseInsensitiveMap<ListKey<?>> map = null;

	private static void buildMap()
	{
		map = new CaseInsensitiveMap<ListKey<?>>();
		Field[] fields = ListKey.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			int mod = fields[i].getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
				&& Modifier.isPublic(mod))
			{
				try
				{
					Object o = fields[i].get(null);
					if (o instanceof ListKey)
					{
						map.put(fields[i].getName(), (ListKey) o);
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
		if (map == null)
		{
			buildMap();
		}
		for (Map.Entry<?, ListKey<?>> me : map.entrySet())
		{
			if (me.getValue() == this)
			{
				return me.getKey().toString();
			}
		}
		// Error
		return "";
	}

	public static <OT> ListKey<OT> getKeyFor(Class<OT> c, String s)
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
		ListKey<OT> o = (ListKey<OT>) map.get(s);
		if (o == null)
		{
			o = new ListKey<OT>();
			map.put(s, o);
		}
		return o;
	}

	public static Collection<ListKey<?>> getAllConstants()
	{
		if (map == null)
		{
			buildMap();
		}
		return new HashSet<ListKey<?>>(map.values());
	}
}
