/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Current Ver: $Revision: 2959 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-05-20 00:02:34 -0400 (Sun, 20 May 2007) $
 */
package plugin.lstcompatibility.equipment;

import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.CDOMArmorProf;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMShieldProf;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Constants;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

public class Proficiency512Token implements
		CDOMCompatibilityToken<CDOMEquipment>, DeferredToken<CDOMEquipment>
{

	public String getTokenName()
	{
		return "PROFICIENCY";
	}

	public boolean parse(LoadContext context, CDOMEquipment eq, String value)
	{
		if (value.indexOf(Constants.PIPE) == -1)
		{
			eq.put(StringKey.COMPAT_PROFICIENCY, value);
			eq.put(StringKey.COMPAT_PROFICIENCY_SOURCE, eq.getKeyName()
					+ " in file " + context.getGraphContext().getSourceURI());
			return true;
		}
		return false;
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}

	public Class<CDOMEquipment> getObjectClass()
	{
		return CDOMEquipment.class;
	}

	public boolean process(LoadContext context, CDOMEquipment eq)
	{
		String value = eq.get(StringKey.COMPAT_PROFICIENCY);
		if (value == null)
		{
			return true;
		}
		List<Type> list = eq.getListFor(ListKey.TYPE);
		boolean isWeapon = list.contains(Type.getConstant("WEAPON"));
		boolean isArmor = list.contains(Type.getConstant("ARMOR"));
		boolean isShield = list.contains(Type.getConstant("SHIELD"));
		if (isWeapon)
		{
			if (isArmor)
			{
				Logging
						.errorPrint("PROFICIENCY is ambiguous: item is both Weapon and Armor: "
								+ eq.get(StringKey.COMPAT_PROFICIENCY_SOURCE));
				return false;
			}
			if (isShield)
			{
				Logging
						.errorPrint("PROFICIENCY is ambiguous: item is both Weapon and Shield: "
								+ eq.get(StringKey.COMPAT_PROFICIENCY_SOURCE));
				return false;
			}
			CDOMSingleRef<CDOMWeaponProf> wp = context.ref.getCDOMReference(
					CDOMWeaponProf.class, value);
			context.getObjectContext().put(eq, ObjectKey.WEAPON_PROF, wp);
		}
		else if (isArmor)
		{
			if (isShield)
			{
				Logging
						.errorPrint("PROFICIENCY is ambiguous: item is both Armor and Shield: "
								+ eq.get(StringKey.COMPAT_PROFICIENCY_SOURCE));
				return false;
			}
			CDOMSingleRef<CDOMArmorProf> wp = context.ref.getCDOMReference(
					CDOMArmorProf.class, value);
			context.getObjectContext().put(eq, ObjectKey.ARMOR_PROF, wp);
		}
		else if (isShield)
		{
			CDOMSingleRef<CDOMShieldProf> wp = context.ref.getCDOMReference(
					CDOMShieldProf.class, value);
			context.getObjectContext().put(eq, ObjectKey.SHIELD_PROF, wp);
		}
		else
		{
			Logging
					.errorPrint("PROFICIENCY is ambiguous: item is not Weapon, Armor or Shield: "
							+ eq.get(StringKey.COMPAT_PROFICIENCY_SOURCE));
			return false;
		}

		return false;
	}

	public Class<CDOMEquipment> getTokenClass()
	{
		return CDOMEquipment.class;
	}
}
