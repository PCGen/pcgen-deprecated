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

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PlayerCharacter;

public class CompoundOrChooser<T extends PrereqObject> extends
		AbstractChooser<T>
{

	private final Set<ChoiceSet<T>> set = new HashSet<ChoiceSet<T>>();

	public CompoundOrChooser()
	{
		super();
	}

	public void addChoiceSet(ChoiceSet<T> cs)
	{
		if (cs == null)
		{
			throw new IllegalArgumentException();
		}
		set.add(cs);
	}

	public void addAllChoiceSets(Collection<ChoiceSet<T>> coll)
	{
		if (coll == null)
		{
			throw new IllegalArgumentException();
		}
		set.addAll(coll);
	}

	public Set<T> getSet(PlayerCharacter pc)
	{
		Set<T> returnSet = new HashSet<T>();
		for (ChoiceSet<T> cs : set)
		{
			returnSet.addAll(cs.getSet(pc));
		}
		return returnSet;
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
		if (!(o instanceof CompoundOrChooser))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		CompoundOrChooser<?> cs = (CompoundOrChooser) o;
		return equalsAbstractChooser(cs) && set.equals(cs.set);
	}
}
