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
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.core.Deity;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.ChooseLstQualifierToken;

public class DeityWeaponToken implements ChooseLstQualifierToken<WeaponProf>
{

	private PrimitiveChoiceFilter<WeaponProf> pcs = null;

	public String getTokenName()
	{
		return "DEITYWEAPON";
	}

	public Class<WeaponProf> getChoiceClass()
	{
		return WeaponProf.class;
	}

	public Set<WeaponProf> getSet(PlayerCharacter pc)
	{
		// TODO Where does the filter hit??
		Set<WeaponProf> set = new HashSet<WeaponProf>();
		List<Deity> list = pc.getActiveGraph().getGrantedNodeList(Deity.class);
		if (list != null)
		{
			for (Deity deity : list)
			{
				List<CDOMSimpleSingleRef<WeaponProf>> weapons =
						deity.getListFor(ListKey.DEITYWEAPON);
				for (CDOMSimpleSingleRef<WeaponProf> weapon : weapons)
				{
					set.add(weapon.resolvesTo());
				}
			}
		}
		return set;
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
			pcs = ChooseLoader.getPrimitiveChoiceFilter(context, cl, value);
			return pcs != null;
		}
		return true;
	}
}
