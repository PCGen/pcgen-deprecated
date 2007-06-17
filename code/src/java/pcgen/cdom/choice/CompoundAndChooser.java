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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PlayerCharacter;

public class CompoundAndChooser<T extends PrereqObject> extends
		AbstractChooser<T>
{

	private final Map<ChoiceSet<T>, Boolean> set =
			new HashMap<ChoiceSet<T>, Boolean>();

	public CompoundAndChooser()
	{
		super();
	}

	public void addChoiceSet(ChoiceSet<T> cs, boolean significant)
	{
		if (cs == null)
		{
			throw new IllegalArgumentException();
		}
		set.put(cs, Boolean.valueOf(significant));
	}

	public void addAllChoiceSets(Collection<ChoiceSet<T>> coll,
		boolean significant)
	{
		if (coll == null)
		{
			throw new IllegalArgumentException();
		}
		for (ChoiceSet<T> cs : coll)
		{
			addChoiceSet(cs, significant);
		}
	}

	public Set<T> getSet(PlayerCharacter pc)
	{
		Set<T> returnSet = null;
		for (ChoiceSet<T> cs : set.keySet())
		{
			if (returnSet == null)
			{
				returnSet = new HashSet<T>(cs.getSet(pc));
			}
			else
			{
				returnSet.retainAll(cs.getSet(pc));
			}
		}
		return returnSet;
	}

	@Override
	public String toString()
	{
		return getCount().toString() + '<' + getMaxSelections().toString()
			+ Constants.PIPE + StringUtil.join(set.keySet(), Constants.PIPE);
	}

	@Override
	public int hashCode()
	{
		return chooserHashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof CompoundAndChooser))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		CompoundAndChooser<?> cs = (CompoundAndChooser) o;
		return equalsAbstractChooser(cs) && set.equals(cs.set);
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder();
		boolean needComma = false;
		for (Entry<ChoiceSet<T>, Boolean> me : set.entrySet())
		{
			if (me.getValue().booleanValue())
			{
				if (needComma)
				{
					sb.append(',');
				}
				sb.append(me.getKey().getLSTformat());
				needComma = true;
			}
		}
		return sb.toString();
	}

	public Class<T> getChoiceClass()
	{
		return set == null ? null : set.keySet().iterator().next()
			.getChoiceClass();
	}
}
