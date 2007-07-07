/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.qualifier.weaponprof;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.ChooseLstQualifierToken;

public class EquipmentToken implements ChooseLstQualifierToken<WeaponProf>
{

	private static Type WEAPON_TYPE = Type.getConstant("WEAPON");

	private PrimitiveChoiceFilter<Equipment> pcs = null;

	public String getTokenName()
	{
		return "EQUIPMENT";
	}

	public Class<WeaponProf> getChoiceClass()
	{
		return WeaponProf.class;
	}

	public Set<WeaponProf> getSet(PlayerCharacter pc)
	{
		Set<WeaponProf> profs = new HashSet<WeaponProf>();
		Set<Equipment> equipment =
				pc.getContext().ref.getConstructedCDOMObjects(Equipment.class);
		if (equipment != null)
		{
			for (Equipment e : equipment)
			{
				if (e.getListFor(ListKey.TYPE).contains(WEAPON_TYPE))
				{
					if (pcs == null || pcs.allow(pc, e))
					{
						CDOMSimpleSingleRef<WeaponProf> prof =
								e.get(ObjectKey.WEAPON_PROF);
						if (prof != null)
						{
							profs.add(prof.resolvesTo());
						}
						// TODO else use equipment name?
					}
				}
			}
		}
		return profs;
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName());
		if (pcs != null)
		{
			sb.append('[').append(pcs.getLSTformat()).append(']');
		}
		return sb.toString();
	}

	public boolean initialize(LoadContext context, Class<WeaponProf> cl,
		String value)
	{
		if (value != null)
		{
			pcs =
					ChooseLoader.getPrimitiveChoiceFilter(context,
						Equipment.class, value);
			return pcs != null;
		}
		return true;
	}
}
