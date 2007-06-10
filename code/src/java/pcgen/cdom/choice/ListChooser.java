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
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.CDOMListObject;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public class ListChooser<T extends PObject> implements ChoiceSet<T>
{

	private HashMapToList<AssociationKey<?>, Object> assoc;

	private Formula count;

	private Formula max;

	private CDOMSimpleSingleRef<? extends CDOMListObject<T>> listRef;

	public ListChooser(CDOMSimpleSingleRef<? extends CDOMListObject<T>> cl)
	{
		super();
		if (cl == null)
		{
			throw new IllegalArgumentException("Choice Class cannot be null");
		}
		listRef = cl;
	}

	public Formula getMaxSelections()
	{
		return max;
	}

	public Formula getCount()
	{
		return count;
	}

	public Set<T> getSet(PlayerCharacter pc)
	{
		CDOMListObject<T> listObj = listRef.resolvesTo();
		Collection<T> listContents = pc.getCDOMListContents(listObj);
		return new HashSet<T>(listContents);
	}

	@Override
	public String toString()
	{
		return count.toString() + '<' + max.toString() + Constants.PIPE
			+ "List: " + listRef;
	}

	@Override
	public int hashCode()
	{
		return count.hashCode() + max.hashCode() * 23;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof ListChooser))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		ListChooser<?> cs = (ListChooser) o;
		return max == cs.max && count == cs.count && listRef.equals(cs.listRef);
	}

	public void setCount(Formula choiceCount)
	{
		// if (choiceCount <= 0)
		// {
		// throw new IllegalArgumentException(
		// "Count for ChoiceSet must be >= 1");
		// }
		count = choiceCount;
	}

	public void setMaxSelections(Formula maxSelected)
	{
		// if (maxSelected <= 0)
		// {
		// throw new IllegalArgumentException(
		// "Max Selected for ChoiceSet must be >= 1");
		// }
		max = maxSelected;
	}

	public <A> void setAssociation(AssociationKey<A> ak, A val)
	{
		if (assoc == null)
		{
			assoc = new HashMapToList<AssociationKey<?>, Object>();
		}
		assoc.addToListFor(ak, val);
	}

	// public boolean validate()
	// {
	// if (max < count)
	// {
	// Logging
	// .errorPrint("Nonsensical ChoiceSet Max Selected must be >= Count");
	// return false;
	// }
	// return true;
	// }
}
