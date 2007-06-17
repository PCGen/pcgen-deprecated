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

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.core.PlayerCharacter;

public class NumberChooser extends AbstractChooser<Integer>
{

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

	public Set<Integer> getSet(PlayerCharacter pc)
	{
		return new TreeSet<Integer>(set);
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
		if (!(o instanceof NumberChooser))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		NumberChooser cs = (NumberChooser) o;
		return equalsAbstractChooser(cs) && set.equals(cs.set);
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("MIN=").append(lowerBound).append("|MAX=").append(upperBound);
		return sb.toString();
	}

	public Class<Integer> getChoiceClass()
	{
		return Integer.class;
	}

}
