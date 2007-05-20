/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.content;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;

public class ClassSkillPointFactory extends ConcretePrereqObject implements
		Comparable<ClassSkillPointFactory>, LSTWriteable
{

	private final int skillPoints;

	public ClassSkillPointFactory(int lvls)
	{
		skillPoints = lvls;
	}

	public int getSkillPointCount()
	{
		return skillPoints;
	}

	@Override
	public int hashCode()
	{
		return skillPoints;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof ClassSkillPointFactory))
		{
			return false;
		}
		ClassSkillPointFactory lcf = (ClassSkillPointFactory) o;
		return skillPoints == lcf.skillPoints;
	}

	public int compareTo(ClassSkillPointFactory arg0)
	{
		if (skillPoints == arg0.skillPoints)
		{
			return 0;
		}
		return skillPoints < arg0.skillPoints ? -1 : 1;
	}

	public String getLSTformat()
	{
		return Integer.toString(skillPoints);
	}

}
