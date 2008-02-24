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
package plugin.qualifier.shieldprof;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMShieldProf;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.ChooseLstQualifierToken;
import pcgen.util.Logging;

public class EquipmentToken implements ChooseLstQualifierToken<CDOMShieldProf>
{
	private static Type SHIELD_TYPE = Type.getConstant("SHIELD");

	private PrimitiveChoiceFilter<CDOMEquipment> pcs = null;

	public String getTokenName()
	{
		return "EQUIPMENT";
	}

	public Class<CDOMShieldProf> getChoiceClass()
	{
		return CDOMShieldProf.class;
	}

	public Set<CDOMShieldProf> getSet(CharacterDataStore pc)
	{
		Set<CDOMShieldProf> profs = new HashSet<CDOMShieldProf>();
		Set<CDOMEquipment> equipment = pc.getRulesData().getAll(
				CDOMEquipment.class);
		if (equipment != null)
		{
			for (CDOMEquipment e : equipment)
			{
				if (e.getListFor(ListKey.TYPE).contains(SHIELD_TYPE))
				{
					if (pcs == null || pcs.allow(pc, e))
					{
						CDOMSingleRef<CDOMShieldProf> prof = e
								.get(ObjectKey.SHIELD_PROF);
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

	public boolean initialize(LoadContext context, Class<CDOMShieldProf> cl,
			String condition, String value)
	{
		if (condition != null)
		{
			Logging.addParseMessage(Level.SEVERE, "Cannot make "
					+ getTokenName()
					+ " into a conditional Qualifier, remove =");
			return false;
		}
		if (value != null)
		{
			pcs = context.getPrimitiveChoiceFilter(CDOMEquipment.class, value);
			return pcs != null;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		return pcs == null ? 0 : pcs.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof EquipmentToken)
		{
			EquipmentToken other = (EquipmentToken) o;
			if (pcs == null)
			{
				return other.pcs == null;
			}
			return pcs.equals(other.pcs);
		}
		return false;
	}
}
