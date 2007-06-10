/*
 * Copyright 2007 (C) Tom Parker <thpr@sourceforge.net>
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
package pcgen.cdom.filter;

import pcgen.cdom.helper.ChoiceFilter;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public class NegatingFilter<T extends PObject> implements ChoiceFilter<T>
{

	private ChoiceFilter<T> choiceFilter;

	public static <T extends PObject> NegatingFilter<T> getNegatingFilter(
		ChoiceFilter<T> cf)
	{
		return new NegatingFilter<T>(cf);
	}

	public NegatingFilter(ChoiceFilter<T> cf)
	{
		super();
		if (cf == null)
		{
			throw new IllegalArgumentException("Choice Filter cannot be null");
		}
		choiceFilter = cf;
	}

	public boolean remove(PlayerCharacter pc, T obj)
	{
		return !choiceFilter.remove(pc, obj);
	}

}
