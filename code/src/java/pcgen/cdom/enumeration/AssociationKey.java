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
import java.net.URI;
import java.util.List;
import java.util.Map;

import pcgen.base.formula.Formula;
import pcgen.base.io.FileLocation;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChoiceSet;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 * 
 * This is a Typesafe enumeration of legal String Characteristics of an object.
 */
public final class AssociationKey<T>
{

	/*
	 * TODO Should ObjectKey take in the Class in order to be able to cast to
	 * the given class?
	 * 
	 * have a .cast(Object o) method on ObjectKey???
	 */
	public static final AssociationKey<Integer> SPELL_LEVEL = new AssociationKey<Integer>();

	public static final AssociationKey<SkillCost> SKILL_COST = new AssociationKey<SkillCost>();

	public static final AssociationKey<AbilityNature> ABILITY_NATURE = new AssociationKey<AbilityNature>();

	public static final AssociationKey<String> CASTER_LEVEL = new AssociationKey<String>();

	public static final AssociationKey<String> TIMES_PER_UNIT = new AssociationKey<String>();

	public static final AssociationKey<String> SPELLBOOK = new AssociationKey<String>();

	public static final AssociationKey<String> DC_FORMULA = new AssociationKey<String>();

	public static final AssociationKey<String> ONLY = new AssociationKey<String>();

	public static final AssociationKey<CDOMAbilityCategory> ABILITY_CATEGORY = new AssociationKey<CDOMAbilityCategory>();

	public static final AssociationKey<EquipmentNature> EQUIPMENT_NATURE = new AssociationKey<EquipmentNature>();

	public static final AssociationKey<Integer> QUANTITY = new AssociationKey<Integer>();

	public static final AssociationKey<URI> RETIRED_BY = new AssociationKey<URI>();

	public static final AssociationKey<Integer> SEQUENCE_NUMBER = new AssociationKey<Integer>();

	public static final AssociationKey<URI> SOURCE_URI = new AssociationKey<URI>();

	public static final AssociationKey<Integer> FOLLOWER_ADJUSTMENT = new AssociationKey<Integer>();

	public static final AssociationKey<CDOMObject> OWNER = new AssociationKey<CDOMObject>();

	public static final AssociationKey<String> TOKEN = new AssociationKey<String>();

	public static final AssociationKey<Integer> WEIGHT = new AssociationKey<Integer>();

	public static final AssociationKey<Integer> EQUIPMENT_LOCATION = new AssociationKey<Integer>();

	public static final AssociationKey<CDOMObject> ABILITY_ASSOCIATION = new AssociationKey<CDOMObject>();

	public static final AssociationKey<AssociationListKey<?>> CHOICE_KEY = new AssociationKey<AssociationListKey<?>>();

	public static final AssociationKey<Formula> CHOICE_COUNT = new AssociationKey<Formula>();

	public static final AssociationKey<Formula> CHOICE_MAXCOUNT = new AssociationKey<Formula>();

	public static final AssociationKey<URI> GLOBAL_REMOVE = new AssociationKey<URI>();

	public static final AssociationKey<Boolean> IRRELEVANT = new AssociationKey<Boolean>();

	public static final AssociationKey<FileLocation> FILE_LOCATION = new AssociationKey<FileLocation>();

	public static final AssociationKey<ChoiceSet<?>> CHOICE = new AssociationKey<ChoiceSet<?>>();

	public static final AssociationKey<String> CHOICE_TITLE = new AssociationKey<String>();

	public static final AssociationKey<Integer> NUMBER_CARRIED = new AssociationKey<Integer>();

	public static final AssociationKey<List<AssociationKey<?>>> LOCK = new AssociationKey<List<AssociationKey<?>>>();

	public static final AssociationKey<Boolean> REQUIRED = new AssociationKey<Boolean>();

	public static final AssociationKey<String> TIME_UNIT = new AssociationKey<String>();

	private static CaseInsensitiveMap<AssociationKey<?>> map = null;

	private AssociationKey()
	{
		// Only allow instantation here
	}

	public T cast(Object o)
	{
		return (T) o;
	}

	public static <OT> AssociationKey<OT> getKeyFor(Class<OT> c, String s)
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
		AssociationKey<OT> o = (AssociationKey<OT>) map.get(s);
		if (o == null)
		{
			o = new AssociationKey<OT>();
			map.put(s, o);
		}
		return o;
	}

	private static void buildMap()
	{
		map = new CaseInsensitiveMap<AssociationKey<?>>();
		Field[] fields = AssociationKey.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			int mod = fields[i].getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
					&& Modifier.isPublic(mod))
			{
				try
				{
					Object o = fields[i].get(null);
					if (o instanceof AssociationKey)
					{
						map.put(fields[i].getName(), (AssociationKey<?>) o);
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
		for (Map.Entry<?, AssociationKey<?>> me : map.entrySet())
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
