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

import java.math.BigDecimal;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.util.BigDecimalHelper;

public class Weight extends ConcretePrereqObject implements Comparable<Weight>
{

	private BigDecimal weight;

	public Weight(BigDecimal d)
	{
		weight = BigDecimalHelper.trimBigDecimal(d);
	}

	public BigDecimal getWeight()
	{
		return weight;
	}

	public int compareTo(Weight arg0)
	{
		return weight.compareTo(arg0.weight);
	}

	@Override
	public int hashCode()
	{
		return weight.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof Weight && ((Weight) o).weight.equals(weight);
	}

}
