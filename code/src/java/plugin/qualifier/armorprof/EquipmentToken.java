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
package plugin.qualifier.armorprof;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.core.ArmorProf;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.ChooseLstQualifierToken;

public class EquipmentToken implements ChooseLstQualifierToken<ArmorProf>
{
	private static Type ARMOR_TYPE = Type.getConstant("ARMOR");

	private PrimitiveChoiceFilter<Equipment> pcs = null;

	public String getTokenName()
	{
		return "EQUIPMENT";
	}

	public Class<ArmorProf> getChoiceClass()
	{
		return ArmorProf.class;
	}

	public Set<ArmorProf> getSet(PlayerCharacter pc)
	{
		Set<ArmorProf> profs = new HashSet<ArmorProf>();
		Set<Equipment> equipment =
				pc.getContext().ref.getConstructedCDOMObjects(Equipment.class);
		if (equipment != null)
		{
			for (Equipment e : equipment)
			{
				if (e.getListFor(ListKey.TYPE).contains(ARMOR_TYPE))
				{
					if (pcs == null || pcs.allow(pc, e))
					{
						CDOMSimpleSingleRef<ArmorProf> prof =
								e.get(ObjectKey.ARMOR_PROF);
						if (prof != null)
						{
							profs.add(prof.resolvesTo());
						}
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

	public boolean initialize(LoadContext context, Class<ArmorProf> cl,
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
