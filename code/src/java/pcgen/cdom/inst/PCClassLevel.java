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
package pcgen.cdom.inst;

import pcgen.core.PObject;

public final class PCClassLevel extends PObject
{

	/*
	 * THIS MUST STAY OBJECT AND NOT PCCLASS!!!
	 * 
	 * Otherwise there is a circular dependence of this object and PCClass.
	 */
	private final Object classSource;

	private final int classLevel;

	public PCClassLevel(Object source, int lvl)
	{
		classSource = source;
		classLevel = lvl;
	}

	public Object getSource()
	{
		return classSource;
	}
	
	public int getClassLevel()
	{
		return classLevel;
	}

	// No additional Functionality :)

	@Override
	public int hashCode()
	{
		return classLevel ^ classSource.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (!(o instanceof PCClassLevel))
		{
			return false;
		}
		PCClassLevel other = (PCClassLevel) o;
		return other.classLevel == classLevel
			&& other.classSource.equals(classSource);
	}
}
