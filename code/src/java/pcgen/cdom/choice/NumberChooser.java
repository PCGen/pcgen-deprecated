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
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PlayerCharacter;

public class NumberChooser implements ChoiceSet<Integer>
{

	private Formula count;

	private Formula max;

	private int lowerBound;

	private int upperBound;

	private Set<Integer> set = new TreeSet<Integer>();

	public NumberChooser(int minChoice, int maxChoice)
	{
		super();
		if (maxChoice < minChoice)
		{
			throw new IllegalArgumentException(
				"Min Choice must be less than or equal to Max Choice");
		}
		lowerBound = minChoice;
		upperBound = maxChoice;
		for (int i = lowerBound; i <= upperBound; i++)
		{
			set.add(Integer.valueOf(i));
		}
	}

	public Formula getMaxSelections()
	{
		return max;
	}

	public Formula getCount()
	{
		return count;
	}

	public Set<Integer> getSet(PlayerCharacter pc)
	{
		return new TreeSet<Integer>(set);
	}

	@Override
	public String toString()
	{
		return count.toString() + '<' + max.toString() + Constants.PIPE
			+ StringUtil.join(set, Constants.PIPE);
	}

	@Override
	public int hashCode()
	{
		return count.hashCode() + max.hashCode() * 23;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof NumberChooser))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		NumberChooser cs = (NumberChooser) o;
		return max == cs.max && count == cs.count && set.equals(cs.set);
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
