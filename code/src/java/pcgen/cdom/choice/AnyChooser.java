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

import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public class AnyChooser<T extends PObject> implements ChoiceSet<T>
{

	private Formula count;

	private Formula max;

	private Class<T> choiceClass;

	public static <T extends PObject> AnyChooser<T> getAnyChooser(Class<T> cl)
	{
		return new AnyChooser<T>(cl);
	}

	public AnyChooser(Class<T> cl)
	{
		super();
		if (cl == null)
		{
			throw new IllegalArgumentException("Choice Class cannot be null");
		}
		choiceClass = cl;
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
		return pc.getContext().ref.getConstructedCDOMObjects(choiceClass);
	}

	@Override
	public String toString()
	{
		return count.toString() + '<' + max.toString() + Constants.PIPE
			+ "Any: " + choiceClass;
	}

	@Override
	public int hashCode()
	{
		return count.hashCode() + max.hashCode() * 23;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof AnyChooser))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		AnyChooser<?> cs = (AnyChooser) o;
		return max == cs.max && count == cs.count
			&& choiceClass.equals(cs.choiceClass);

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
