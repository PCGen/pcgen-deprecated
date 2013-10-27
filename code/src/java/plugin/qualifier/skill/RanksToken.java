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
package plugin.qualifier.skill;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.inst.CDOMSkill;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.ChooseLstQualifierToken;
import pcgen.util.Logging;

public class RanksToken implements ChooseLstQualifierToken<CDOMSkill>
{

	private PrimitiveChoiceFilter<CDOMSkill> pcs = null;
	
	private int ranks;

	public String getTokenName()
	{
		return "RANKS";
	}

	public Class<CDOMSkill> getChoiceClass()
	{
		return CDOMSkill.class;
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

	public boolean initialize(LoadContext context, Class<CDOMSkill> cl, String condition, String value)
	{
		if (condition == null)
		{
			Logging.addParseMessage(Level.SEVERE, getTokenName()
					+ " Must be a conditional Qualifier, e.g. "
					+ getTokenName() + "=10");
			return false;
		}
		try
		{
			ranks = Integer.parseInt(condition);
		}
		catch (NumberFormatException e)
		{
			Logging.addParseMessage(Level.SEVERE, getTokenName()
					+ " Must be a numerical conditional Qualifier, e.g. "
					+ getTokenName() + "=10 ... Offending value: " + condition);
			return false;
		}
		if (value != null)
		{
			pcs = context.getPrimitiveChoiceFilter(cl, value);
			return pcs != null;
		}
		return true;
	}

	public Set<CDOMSkill> getSet(CharacterDataStore pc)
	{
		Set<CDOMSkill> skillSet = new HashSet<CDOMSkill>(pc.getRulesData().getAll(
				CDOMSkill.class));
		List<CDOMSkill> skillList =
				pc.getActiveGraph().getGrantedNodeList(CDOMSkill.class);
		if (skillList != null)
		{
			for (CDOMSkill sk : skillList)
			{
				if (pc.getTotalWeight(sk) >= ranks)
				{
					skillSet.add(sk);
				}
			}
		}
		return skillSet;
	}
}
