package pcgen.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.core.Ability;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.ReferenceManufacturer;

public final class StringPClassUtil
{

	private StringPClassUtil()
	{
		// Can't instantiate Utility Class
	}

	private static Map<String, Class<? extends PObject>> classMap;
	private static Map<Class<? extends PObject>, String> stringMap;

	static
	{
		classMap = new HashMap<String, Class<? extends PObject>>();
		stringMap = new HashMap<Class<? extends PObject>, String>();

		classMap.put("DEITY", Deity.class);
		classMap.put("DOMAIN", Domain.class);
		classMap.put("EQUIPMENT", Equipment.class);
		classMap.put("EQMOD", EquipmentModifier.class);
		classMap.put("ABILITY", Ability.class);
		classMap.put("FEAT", Ability.class);
		classMap.put("CLASS", PCClass.class);
		classMap.put("RACE", Race.class);
		classMap.put("SPELL", Spell.class);
		classMap.put("SKILL", Skill.class);
		classMap.put("TEMPLATE", PCTemplate.class);
		classMap.put("WEAPONPROF", WeaponProf.class);

		stringMap.put(Deity.class, "DEITY");
		stringMap.put(Domain.class, "DOMAIN");
		stringMap.put(Equipment.class, "EQUIPMENT");
		stringMap.put(EquipmentModifier.class, "EQMOD");
		stringMap.put(Ability.class, "ABILITY");
		stringMap.put(PCClass.class, "CLASS");
		stringMap.put(Race.class, "RACE");
		stringMap.put(Spell.class, "SPELL");
		stringMap.put(Skill.class, "SKILL");
		stringMap.put(PCTemplate.class, "TEMPLATE");
		stringMap.put(WeaponProf.class, "WEAPONPROF");
	}

	public static Class<? extends PObject> getClassFor(String key)
	{
		return classMap.get(key);
	}

	public static Set<String> getValidStrings()
	{
		return classMap.keySet();
	}

	public static String getStringFor(Class<? extends PObject> cl)
	{
		return stringMap.get(cl);
	}

	public static ReferenceManufacturer<? extends PObject> getReferenceManufacturer(
		LoadContext context, String type)
	{
		ReferenceManufacturer<? extends PObject> rm;
		int equalLoc = type.indexOf('=');
		if (equalLoc == -1)
		{
			if ("ABILITY".equals(type))
			{
				Logging.errorPrint("Invalid use of ABILITY in QUALIFY "
					+ "(requires ABILITY=<category>): " + type);
				return null;
			}
			Class<? extends PObject> cl;
			cl = StringPClassUtil.getClassFor(type);
			if (cl == null)
			{
				Logging.errorPrint(" Expecting a POBJECT Type, found: " + type);
				return null;
			}
			rm = context.ref.getReferenceManufacturer(cl);
		}
		else
		{
			if (!"ABILITY".equals(type.substring(0, equalLoc)))
			{
				Logging.errorPrint("Invalid use of = in QUALIFY "
					+ "(only valid for ABILITY): " + type);
				return null;
			}
			AbilityCategory cat =
					AbilityCategory.valueOf(type.substring(equalLoc + 1));
			rm = context.ref.getReferenceManufacturer(Ability.class, cat);
		}
		return rm;
	}

}
