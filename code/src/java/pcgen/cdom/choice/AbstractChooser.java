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

import pcgen.base.formula.Formula;
import pcgen.cdom.helper.ChoiceSet;

public abstract class AbstractChooser<T> implements ChoiceSet<T>
{

	private Formula count;

	private Formula max;

	private String type;

	public AbstractChooser()
	{
		super();
	}

	public Formula getMaxSelections()
	{
		return max;
	}

	public Formula getCount()
	{
		return count;
	}

	public int chooserHashCode()
	{
		return count == null ? 0 : count.hashCode()
			+ (max == null ? 0 : max.hashCode() * 23);
	}

	public boolean equalsAbstractChooser(AbstractChooser<?> ac)
	{
		if (ac == this)
		{
			return true;
		}
		return max == ac.max && count == ac.count;
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

	public void setChooseType(String key)
	{
		type = key;
	}

	public String getChooseType()
	{
		return type;
	}
}
