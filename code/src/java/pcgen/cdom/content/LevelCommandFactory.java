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
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.rules.persistence.TokenUtilities;

public class LevelCommandFactory extends ConcretePrereqObject implements
		Comparable<LevelCommandFactory>, LSTWriteable
{

	private final CDOMSingleRef<CDOMPCClass> pcClass;

	private final int levels;

	public LevelCommandFactory(CDOMSingleRef<CDOMPCClass> cl, int lvls)
	{
		pcClass = cl;
		levels = lvls;
	}

	public int getLevelCount()
	{
		return levels;
	}

	public CDOMPCClass getPCClass()
	{
		return pcClass.resolvesTo();
	}

	public String getLSTformat()
	{
		return pcClass.getLSTformat();
	}

	@Override
	public int hashCode()
	{
		return pcClass.hashCode() * 29 + levels;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof LevelCommandFactory))
		{
			return false;
		}
		LevelCommandFactory lcf = (LevelCommandFactory) o;
		return levels == lcf.levels && pcClass.equals(lcf.pcClass);
	}

	public int compareTo(LevelCommandFactory arg0)
	{
		int i = TokenUtilities.REFERENCE_SORTER.compare(pcClass, arg0.pcClass);
		if (i == 0)
		{
			if (levels == arg0.levels)
			{
				return 0;
			}
			return levels < arg0.levels ? -1 : 1;
		}
		return i;
	}

}
