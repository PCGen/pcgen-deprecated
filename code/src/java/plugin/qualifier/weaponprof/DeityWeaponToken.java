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

import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.character.CharacterDataStore;
import plugin.qualifier.AbstractQualifierToken;

public class DeityWeaponToken extends AbstractQualifierToken<CDOMWeaponProf>
{

	public String getTokenName()
	{
		return "DEITYWEAPON";
	}

	public Class<CDOMWeaponProf> getChoiceClass()
	{
		return CDOMWeaponProf.class;
	}

	public Set<CDOMWeaponProf> getSet(CharacterDataStore pc)
	{
		Set<CDOMWeaponProf> set = new HashSet<CDOMWeaponProf>();
		List<CDOMDeity> list = pc.getActiveGraph().getGrantedNodeList(
				CDOMDeity.class);
		if (list != null)
		{
			for (CDOMDeity deity : list)
			{
				List<CDOMSingleRef<CDOMWeaponProf>> weapons = deity
						.getListFor(ListKey.DEITYWEAPON);
				for (CDOMSingleRef<CDOMWeaponProf> weapon : weapons)
				{
					CDOMWeaponProf wp = weapon.resolvesTo();
					if (pcs == null || pcs.allow(pc, wp))
					{
						set.add(wp);
					}
				}
			}
		}
		return set;
	}
}
