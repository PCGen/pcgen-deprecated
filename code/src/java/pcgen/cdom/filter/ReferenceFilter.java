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
package pcgen.cdom.filter;

import java.util.Collection;

import pcgen.cdom.helper.ChoiceFilter;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public class ReferenceFilter<T extends PObject> implements ChoiceFilter<T>
{

	public static <TT extends PObject> ReferenceFilter<TT> getReferenceFilter(
		Collection<TT> coll)
	{
		return new ReferenceFilter<TT>(coll);
	}

	private Collection<T> collection;

	public ReferenceFilter(Collection<T> coll)
	{
		super();
		if (coll == null)
		{
			throw new IllegalArgumentException(
				"Filter collection cannot be null");
		}
		if (coll.isEmpty())
		{
			throw new IllegalArgumentException(
				"Filter collection cannot be empty");
		}
		collection = coll;
	}

	public boolean remove(PlayerCharacter pc, T obj)
	{
		return collection.contains(obj);
	}

}
