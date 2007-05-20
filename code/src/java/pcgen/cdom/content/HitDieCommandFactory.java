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

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.modifier.AbstractHitDieModifier;
import pcgen.core.PCClass;
import pcgen.persistence.lst.utils.TokenUtilities;

public class HitDieCommandFactory extends ConcretePrereqObject implements
		Comparable<HitDieCommandFactory>, LSTWriteable
{

	private final CDOMReference<PCClass> pcClass;

	private final AbstractHitDieModifier modifier;

	public HitDieCommandFactory(CDOMReference<PCClass> cl,
		AbstractHitDieModifier mod)
	{
		pcClass = cl;
		modifier = mod;
	}

	public AbstractHitDieModifier getModifier()
	{
		return modifier;
	}

	public String getLSTformat()
	{
		return pcClass.getLSTformat();
	}

	@Override
	public int hashCode()
	{
		return pcClass.hashCode() * 29 + modifier.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof HitDieCommandFactory))
		{
			return false;
		}
		HitDieCommandFactory lcf = (HitDieCommandFactory) o;
		return modifier.equals(lcf.modifier) && pcClass.equals(lcf.pcClass);
	}

	public int compareTo(HitDieCommandFactory arg0)
	{
		int i = TokenUtilities.REFERENCE_SORTER.compare(pcClass, arg0.pcClass);
		if (i == 0)
		{
			// TODO Need to fix this - AbstractHitDieModifier should be
			// comparable?
			throw new UnsupportedOperationException();
		}
		return i;
	}

}
