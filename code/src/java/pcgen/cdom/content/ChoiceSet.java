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
package pcgen.cdom.content;

import java.util.HashSet;
import java.util.Set;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;

public class ChoiceSet<T> extends ConcretePrereqObject implements LSTWriteable
{

	private final Set<T> set;

	private final int count;

	public ChoiceSet(int choiceCount, int listCount)
	{
		super();
		count = choiceCount;
		set = new HashSet<T>(listCount);
	}

	public int getCount()
	{
		return count;
	}

	public Set<T> getSet()
	{
		return set;
	}

	public boolean addChoice(T choice)
	{
		/*
		 * Note: You MUST NOT check the list length here for validity. This can
		 * easily be 'broken' by TEMPLATE:ADDCHOICE; thus, no test can be done
		 * against the list length. - Tom Parker 1/26/07
		 */
		return set.add(choice);
	}

	@Override
	public String toString()
	{
		return count + Constants.PIPE + StringUtil.join(set, Constants.PIPE);
	}

	public void clear()
	{
		set.clear();
	}

	@Override
	public int hashCode()
	{
		return set.hashCode() ^ count;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof ChoiceSet))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		ChoiceSet<?> cs = (ChoiceSet) o;
		return count == cs.count && set.equals(cs.set);
	}

	public String getLSTformat()
	{
		// TODO Don't like this, but required for unparse clarity (need to clean
		// up generics/GraphChanges)
		return Integer.toString(count);
	}
}
