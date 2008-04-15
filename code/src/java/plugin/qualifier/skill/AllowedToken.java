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
import java.util.Set;
import java.util.logging.Level;

import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.inst.CDOMSkill;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.lists.PCGenLists;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.ChooseLstQualifierToken;
import pcgen.util.Logging;

public class AllowedToken implements ChooseLstQualifierToken<CDOMSkill>
{

	private PrimitiveChoiceFilter<CDOMSkill> pcs = null;

	public String getTokenName()
	{
		return "ALLOWED";
	}

	public Class<CDOMSkill> getChoiceClass()
	{
		return CDOMSkill.class;
	}

	public boolean initialize(LoadContext context, Class<CDOMSkill> cl, String condition, String value)
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
			pcs = context.getPrimitiveChoiceFilter(cl, value);
			return pcs != null;
		}
		return true;
	}

	public Set<CDOMSkill> getSet(CharacterDataStore pc)
	{
		Set<CDOMSkill> skillSet = new HashSet<CDOMSkill>();
		PCGenLists activeLists = pc.getActiveLists();
		Set<ClassSkillList> lists = activeLists.getLists(ClassSkillList.class);
		if (lists != null)
		{
			for (ClassSkillList csl : lists)
			{
				skillSet.addAll(activeLists.getListContents(csl));
			}
		}
		return skillSet;
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
}