package pcgen.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMDomain;
import pcgen.cdom.inst.CDOMEqMod;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMRace;
import pcgen.cdom.inst.CDOMSkill;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.cdom.inst.CDOMWeaponProf;
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

public final class StringPClassUtil
{

	private StringPClassUtil()
	{
		// Can't instantiate Utility Class
	}

	private static Map<String, Class<? extends PObject>> classMap;
	private static Map<Class<? extends PObject>, String> stringMap;
	private static Map<String, Class<? extends CDOMObject>> cdomClassMap;
	private static Map<Class<? extends CDOMObject>, String> cdomStringMap;

	static
	{
		classMap = new HashMap<String, Class<? extends PObject>>();
		stringMap = new HashMap<Class<? extends PObject>, String>();
		cdomClassMap = new HashMap<String, Class<? extends CDOMObject>>();
		cdomStringMap = new HashMap<Class<? extends CDOMObject>, String>();

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

		cdomClassMap.put("DEITY", CDOMDeity.class);
		cdomClassMap.put("DOMAIN", CDOMDomain.class);
		cdomClassMap.put("EQUIPMENT", CDOMEquipment.class);
		cdomClassMap.put("EQMOD", CDOMEqMod.class);
		cdomClassMap.put("ABILITY", CDOMAbility.class);
		cdomClassMap.put("FEAT", CDOMAbility.class);
		cdomClassMap.put("CLASS", CDOMPCClass.class);
		cdomClassMap.put("RACE", CDOMRace.class);
		cdomClassMap.put("SPELL", CDOMSpell.class);
		cdomClassMap.put("SKILL", CDOMSkill.class);
		cdomClassMap.put("TEMPLATE", CDOMTemplate.class);
		cdomClassMap.put("WEAPONPROF", CDOMWeaponProf.class);

		cdomStringMap.put(CDOMDeity.class, "DEITY");
		cdomStringMap.put(CDOMDomain.class, "DOMAIN");
		cdomStringMap.put(CDOMEquipment.class, "EQUIPMENT");
		cdomStringMap.put(CDOMEqMod.class, "EQMOD");
		cdomStringMap.put(CDOMAbility.class, "ABILITY");
		cdomStringMap.put(CDOMPCClass.class, "CLASS");
		cdomStringMap.put(CDOMRace.class, "RACE");
		cdomStringMap.put(CDOMSpell.class, "SPELL");
		cdomStringMap.put(CDOMSkill.class, "SKILL");
		cdomStringMap.put(CDOMTemplate.class, "TEMPLATE");
		cdomStringMap.put(CDOMWeaponProf.class, "WEAPONPROF");
	}

	public static Class<? extends PObject> getClassFor(String key)
	{
		return classMap.get(key);
	}

	public static Class<? extends CDOMObject> getCDOMClassFor(String key)
	{
		return cdomClassMap.get(key);
	}

	public static Set<String> getValidStrings()
	{
		return classMap.keySet();
	}

	public static String getStringFor(Class<? extends PObject> cl)
	{
		return stringMap.get(cl);
	}

	public static String getCDOMStringFor(Class<? extends CDOMObject> cl)
	{
		return cdomStringMap.get(cl);
	}

	public static <T extends CategorizedCDOMObject<T>>  Category<T> getCDOMCategoryFor(Class<T> c, String categoryName)
	{
		if (CDOMAbility.class.equals(c))
		{
			return (Category) CDOMAbilityCategory.valueOf(categoryName);
		}
		return null;
	}

}
