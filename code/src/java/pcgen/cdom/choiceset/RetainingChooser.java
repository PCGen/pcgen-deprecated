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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.character.CharacterDataStore;
import pcgen.rules.persistence.TokenUtilities;

public class RetainingChooser<T extends CDOMObject> implements
		PrimitiveChoiceSet<T>
{

	private final Set<PrimitiveChoiceFilter<? super T>> retainingSet = new HashSet<PrimitiveChoiceFilter<? super T>>();

	private final PrimitiveChoiceSet<T> baseSet;

	public RetainingChooser(Class<T> cl)
	{
		super();
		if (cl == null)
		{
			throw new IllegalArgumentException();
		}
		baseSet = new AnyChoiceSet<T>(cl);
	}

	public void addRetainingChoiceFilter(PrimitiveChoiceFilter<? super T> cs)
	{
		if (cs == null)
		{
			throw new IllegalArgumentException();
		}
		retainingSet.add(cs);
	}

	public void addAllRetainingChoiceFilters(
			Collection<PrimitiveChoiceFilter<T>> coll)
	{
		if (coll == null)
		{
			throw new IllegalArgumentException();
		}
		retainingSet.addAll(coll);
	}

	public Set<T> getSet(CharacterDataStore pc)
	{
		Set<T> choices = new HashSet<T>();
		if (retainingSet != null)
		{
			choices.addAll(baseSet.getSet(pc));
			RETAIN: for (Iterator<T> it = choices.iterator(); it.hasNext();)
			{
				for (PrimitiveChoiceFilter<? super T> cf : retainingSet)
				{
					if (cf.allow(pc, it.next()))
					{
						continue RETAIN;
					}
				}
				it.remove();
			}
		}
		return choices;
	}

	public String getLSTformat()
	{
		Set<LSTWriteable> sortSet = new TreeSet<LSTWriteable>(TokenUtilities.WRITEABLE_SORTER);
		for (PrimitiveChoiceFilter<? super T> cf : retainingSet)
		{
			sortSet.add(cf);
		}
		return ReferenceUtilities.joinLstFormat(sortSet, Constants.PIPE);
	}

	public Class<? super T> getChoiceClass()
	{
		return baseSet.getChoiceClass();
	}

	@Override
	public int hashCode()
	{
		return baseSet.hashCode() + retainingSet.size();
	}

	public boolean equals(Object o)
	{
		if (o instanceof RetainingChooser)
		{
			RetainingChooser other = (RetainingChooser) o;
			return baseSet.equals(other.baseSet)
					&& retainingSet.equals(other.retainingSet);
		}
		return false;
	}
}
