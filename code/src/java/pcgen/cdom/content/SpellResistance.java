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

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;

public class SpellResistance extends ConcretePrereqObject implements
		LSTWriteable
{

	private final Formula reduction;

	public SpellResistance(Formula aReduction)
	{
		super();
		reduction = aReduction;
	}

	public Formula getReduction()
	{
		return reduction;
	}

	@Override
	public String toString()
	{
		return reduction.toString();
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof SpellResistance)
		{
			SpellResistance othSR = (SpellResistance) other;
			return reduction.equals(othSR.reduction);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return reduction.hashCode();
	}

	public String getLSTformat()
	{
		return reduction.toString();
	}
}
