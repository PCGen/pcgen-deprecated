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
package pcgen.cdom.choice;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.helper.ChoiceFilter;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PlayerCharacter;

public class RemovingChooser<T extends PrereqObject> extends AbstractChooser<T>
{

	private final Set<ChoiceFilter<? super T>> set =
			new HashSet<ChoiceFilter<? super T>>();

	private final ChoiceSet<T> baseSet;

	public RemovingChooser(ChoiceSet<T> base)
	{
		super();
		if (base == null)
		{
			throw new IllegalArgumentException();
		}
		baseSet = base;
	}

	public void addRemovingChoiceFilter(ChoiceFilter<? super T> cs)
	{
		if (cs == null)
		{
			throw new IllegalArgumentException();
		}
		set.add(cs);
	}

	public void addAllRemovingChoiceFilters(Collection<ChoiceFilter<T>> coll)
	{
		if (coll == null)
		{
			throw new IllegalArgumentException();
		}
		set.addAll(coll);
	}

	public Set<T> getSet(PlayerCharacter pc)
	{
		Set<T> choices = new HashSet<T>(baseSet.getSet(pc));
		for (ChoiceFilter<? super T> cf : set)
		{
			for (Iterator<T> it = choices.iterator(); it.hasNext();)
			{
				T next = it.next();
				if (cf.remove(pc, next))
				{
					it.remove();
				}
			}
		}
		return choices;
	}

	@Override
	public String toString()
	{
		return getCount().toString() + '<' + getMaxSelections().toString()
			+ Constants.PIPE + StringUtil.join(set, Constants.PIPE);
	}

	@Override
	public int hashCode()
	{
		return chooserHashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof RemovingChooser))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		RemovingChooser<?> cs = (RemovingChooser) o;
		return equalsAbstractChooser(cs) && set.equals(cs.set);
	}
}
