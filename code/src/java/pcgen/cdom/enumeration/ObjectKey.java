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
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import pcgen.base.formula.Resolver;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.DefaultMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.content.CDOMSpellProhibitor;
import pcgen.cdom.content.HitDieCommandFactory;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.inst.CDOMAlignment;
import pcgen.cdom.inst.CDOMArmorProf;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMShieldProf;
import pcgen.cdom.inst.CDOMSizeAdjustment;
import pcgen.cdom.inst.CDOMStat;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.util.enumeration.DefaultTriState;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Visibility;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 * 
 * This is a Typesafe enumeration of legal String Characteristics of an object.
 */
public final class ObjectKey<T>
{

	/*
	 * TODO Should ObjectKey take in the Class in order to be able to cast to
	 * the given class?
	 * 
	 * have a .cast(Object o) method on ObjectKey???
	 */
	public static final ObjectKey<Resolver<CDOMSizeAdjustment>> SIZE = new ObjectKey<Resolver<CDOMSizeAdjustment>>();

	public static final ObjectKey<Region> REGION = new ObjectKey<Region>();

	public static final ObjectKey<SubRegion> SUBREGION = new ObjectKey<SubRegion>();

	public static final ObjectKey<Visibility> VISIBILITY = new ObjectKey<Visibility>();

	public static final ObjectKey<SubRace> SUBRACE = new ObjectKey<SubRace>();

	public static final ObjectKey<RaceType> RACETYPE = new ObjectKey<RaceType>();

	public static final ObjectKey<Boolean> REMOVABLE = new ObjectKey<Boolean>();

	public static final ObjectKey<Load> UNENCUMBERED_LOAD = new ObjectKey<Load>();

	public static final ObjectKey<Load> UNENCUMBERED_ARMOR = new ObjectKey<Load>();

	public static final ObjectKey<Boolean> SPELLBOOK = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> MOD_TO_SKILLS = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> MEMORIZE_SPELLS = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> IS_MONSTER = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> HAS_SPELL_FORMULA = new ObjectKey<Boolean>();

	public static final ObjectKey<CDOMReference<CDOMPCClass>> EX_CLASS = new ObjectKey<CDOMReference<CDOMPCClass>>();

	public static final ObjectKey<Gender> GENDER_LOCK = new ObjectKey<Gender>();

	public static final ObjectKey<BigDecimal> COST = new ObjectKey<BigDecimal>();

	public static final ObjectKey<CDOMStat> KEY_STAT = new ObjectKey<CDOMStat>();

	public static final ObjectKey<CDOMStat> SPELL_STAT = new ObjectKey<CDOMStat>();

	public static final ObjectKey<CDOMStat> BONUS_SPELL_STAT = new ObjectKey<CDOMStat>();

	public static final ObjectKey<Boolean> READ_ONLY = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> COST_DOUBLE = new ObjectKey<Boolean>();

	public static final ObjectKey<EqModFormatCat> FORMAT = new ObjectKey<EqModFormatCat>();

	public static final ObjectKey<Boolean> ASSIGN_TO_ALL = new ObjectKey<Boolean>();

	public static final ObjectKey<EqModNameOpt> NAME_OPT = new ObjectKey<EqModNameOpt>();

	public static final ObjectKey<BigDecimal> WEIGHT = new ObjectKey<BigDecimal>();

	public static final ObjectKey<EqWield> WIELD = new ObjectKey<EqWield>();

	public static final ObjectKey<CDOMSingleRef<CDOMWeaponProf>> WEAPON_PROF = new ObjectKey<CDOMSingleRef<CDOMWeaponProf>>();

	public static final ObjectKey<EqModControl> MOD_CONTROL = new ObjectKey<EqModControl>();

	public static final ObjectKey<CDOMSingleRef<CDOMEquipment>> BASE_ITEM = new ObjectKey<CDOMSingleRef<CDOMEquipment>>();

	public static final ObjectKey<Boolean> USE_MASTER_SKILL = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> STACKS = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> MULTIPLE_ALLOWED = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> USE_UNTRAINED = new ObjectKey<Boolean>();

	public static final ObjectKey<SkillArmorCheck> ARMOR_CHECK = new ObjectKey<SkillArmorCheck>();

	public static final ObjectKey<CDOMAlignment> ALIGNMENT = new ObjectKey<CDOMAlignment>();

	public static final ObjectKey<DefaultMap<CDOMSingleRef<CDOMPCClass>, Integer>> COMPONENT_COST = new ObjectKey<DefaultMap<CDOMSingleRef<CDOMPCClass>, Integer>>();

	public static final ObjectKey<Boolean> EXCLUSIVE = new ObjectKey<Boolean>();

	public static final ObjectKey<BigDecimal> CONTAINER_WEIGHT_CAPACITY = new ObjectKey<BigDecimal>();

	public static final ObjectKey<Boolean> CONTAINER_CONSTANT_WEIGHT = new ObjectKey<Boolean>();

	public static final ObjectKey<CDOMAbilityCategory> CATEGORY = new ObjectKey<CDOMAbilityCategory>();

	public static final ObjectKey<Boolean> ATTACKS_PROGRESS = new ObjectKey<Boolean>();

	public static final ObjectKey<DefaultTriState> XP_PENALTY = new ObjectKey<DefaultTriState>();

	public static final ObjectKey<Type> SPELL_TYPE = new ObjectKey<Type>();

	public static final ObjectKey<Boolean> RETIRED = new ObjectKey<Boolean>();

	public static final ObjectKey<URI> SOURCE_URI = new ObjectKey<URI>();

	public static final ObjectKey<BigDecimal> FACE_WIDTH = new ObjectKey<BigDecimal>();

	public static final ObjectKey<BigDecimal> FACE_HEIGHT = new ObjectKey<BigDecimal>();

	public static final ObjectKey<ChoiceSet<?>> CHOICE = new ObjectKey<ChoiceSet<?>>();

	public static final ObjectKey<ChoiceSet<?>> KIT_CHOICE = new ObjectKey<ChoiceSet<?>>();

	public static final ObjectKey<CDOMObject> PSEUDO_PARENT = new ObjectKey<CDOMObject>();

	public static final ObjectKey<HitDieCommandFactory> HITDIE = new ObjectKey<HitDieCommandFactory>();

	public static final ObjectKey<Boolean> HAS_BONUS_SPELL_STAT = new ObjectKey<Boolean>();

	public static final ObjectKey<CDOMSingleRef<CDOMShieldProf>> SHIELD_PROF = new ObjectKey<CDOMSingleRef<CDOMShieldProf>>();

	public static final ObjectKey<CDOMSingleRef<CDOMArmorProf>> ARMOR_PROF = new ObjectKey<CDOMSingleRef<CDOMArmorProf>>();

	public static final ObjectKey<BigDecimal> PROHIBITED_COST = new ObjectKey<BigDecimal>();

	public static final ObjectKey<Boolean> USE_SPELL_SPELL_STAT = new ObjectKey<Boolean>();

	public static final ObjectKey<CDOMSpellProhibitor<?>> SELETED_SPELLS = new ObjectKey<CDOMSpellProhibitor<?>>();

	public static final ObjectKey<Boolean> CASTER_WITHOUT_SPELL_STAT = new ObjectKey<Boolean>();

	public static final ObjectKey<CDOMDeity> DEITY = new ObjectKey<CDOMDeity>();

	public static final ObjectKey<Boolean> IS_DEFAULT = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> FRACTIONAL_POOL = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> EDITPOOL = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> EDITABLE = new ObjectKey<Boolean>();

	public static final ObjectKey<CDOMSingleRef<CDOMAbilityCategory>> PARENT_CATEGORY = new ObjectKey<CDOMSingleRef<CDOMAbilityCategory>>();

	public static final ObjectKey<Boolean> USEMASTERSKILL = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> DESC_PI = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> NAME_PI = new ObjectKey<Boolean>();

	public static final ObjectKey<Object> PARENT = new ObjectKey<Object>();

	public static final ObjectKey<Boolean> ALLOWBASECLASS = new ObjectKey<Boolean>();

	public static final ObjectKey<URI> SOURCE_WEB = new ObjectKey<URI>();

	public static final ObjectKey<Date> SOURCE_DATE = new ObjectKey<Date>();

	private static CaseInsensitiveMap<ObjectKey<?>> map = null;

	private ObjectKey()
	{
		// Only allow instantation here
	}

	public T cast(Object o)
	{
		return (T) o;
	}

	public static <OT> ObjectKey<OT> getKeyFor(Class<OT> c, String s)
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
		ObjectKey<OT> o = (ObjectKey<OT>) map.get(s);
		if (o == null)
		{
			o = new ObjectKey<OT>();
			map.put(s, o);
		}
		return o;
	}

	private static void buildMap()
	{
		map = new CaseInsensitiveMap<ObjectKey<?>>();
		Field[] fields = ObjectKey.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			int mod = fields[i].getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
					&& Modifier.isPublic(mod))
			{
				try
				{
					Object o = fields[i].get(null);
					if (o instanceof ObjectKey)
					{
						map.put(fields[i].getName(), (ObjectKey<?>) o);
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
		for (Map.Entry<?, ObjectKey<?>> me : map.entrySet())
		{
			if (me.getValue() == this)
			{
				return me.getKey().toString();
			}
		}
		// Error
		return "";
	}

	public static Collection<ObjectKey<?>> getAllConstants()
	{
		if (map == null)
		{
			buildMap();
		}
		return new HashSet<ObjectKey<?>>(map.values());
	}
}
