/*
 * Copyright 2006 (C) Tom Parker <thpr@sourceforge.net>
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
 * 
 * Created on October 29, 2006.
 * 
 * Current Ver: $Revision: 1111 $ Last Editor: $Author: boomer70 $ Last Edited:
 * $Date: 2006-06-22 21:22:44 -0400 (Thu, 22 Jun 2006) $
 */
package pcgen.cdom.choiceset;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.character.CharacterDataStore;

public class FilteringChooser<T extends PrereqObject> implements
		PrimitiveChoiceSet<T>
{

	private final PrimitiveChoiceFilter<? super T> removingFilter;

	private final PrimitiveChoiceSet<T> baseSet;

	public FilteringChooser(PrimitiveChoiceSet<T> base,
		PrimitiveChoiceFilter<? super T> cf)
	{
		super();
		if (base == null)
		{
			throw new IllegalArgumentException();
		}
		if (cf == null)
		{
			throw new IllegalArgumentException();
		}
		baseSet = base;
		removingFilter = cf;
	}

	public Set<T> getSet(CharacterDataStore pc)
	{
		Set<T> choices = new HashSet<T>(baseSet.getSet(pc));
		for (Iterator<T> it = choices.iterator(); it.hasNext();)
		{
			if (!removingFilter.allow(pc, it.next()))
			{
				it.remove();
			}
		}
		return choices;
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(baseSet.getLSTformat()).append('[');
		sb.append(removingFilter.getLSTformat()).append(']');
		return sb.toString();
	}

	public Class<? super T> getChoiceClass()
	{
		return baseSet.getChoiceClass();
	}
}
