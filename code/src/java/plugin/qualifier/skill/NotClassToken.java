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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import pcgen.cdom.base.AssociatedObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.cdom.lists.PCGenLists;
import pcgen.core.ClassSkillList;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.ChooseLstQualifierToken;
import pcgen.util.Logging;

public class NotClassToken implements ChooseLstQualifierToken<Skill>
{

	private PrimitiveChoiceFilter<Skill> pcs = null;

	public String getTokenName()
	{
		return "!CLASS";
	}

	public Class<Skill> getChoiceClass()
	{
		return Skill.class;
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

	public boolean initialize(LoadContext context, Class<Skill> cl, String condition, String value)
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
			pcs = ChooseLoader.getPrimitiveChoiceFilter(context, cl, value);
			return pcs != null;
		}
		return true;
	}

	public Set<Skill> getSet(PlayerCharacter pc)
	{
		Set<Skill> skillSet = new HashSet<Skill>();
		PCGenLists activeLists = pc.getActiveLists();
		Set<ClassSkillList> lists = activeLists.getLists(ClassSkillList.class);
		SkillCost classCost = SkillCost.CLASS;
		if (lists != null)
		{
			for (ClassSkillList csl : lists)
			{
				Collection<Skill> contents = activeLists.getListContents(csl);
				for (Skill sk : contents)
				{
					AssociatedObject assoc =
							activeLists.getListAssociation(csl, sk);
					if (!classCost.equals(assoc
						.getAssociation(AssociationKey.SKILL_COST)))
					{
						skillSet.add(sk);
					}
				}
			}
		}
		return skillSet;
	}
}
