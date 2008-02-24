/*
 * SpellProhibitor.java
 * Copyright 2005 (c) Stefan Raderamcher <radermacher@netcologne.de>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on March 3, 2005, 16:30 AM
 *
 * Current Ver: $Revision: 3813 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-08-19 19:19:05 -0400 (Sun, 19 Aug 2007) $
 *
 */
package pcgen.cdom.content;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.ProhibitedSpellType;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.character.CharacterDataStore;
import pcgen.core.Globals;
import pcgen.core.RuleConstants;

public class CDOMSpellProhibitor<T> extends ConcretePrereqObject implements
		LSTWriteable
{

	private ProhibitedSpellType<T> type = null;
	private Set<T> valueSet = null;

	public CDOMSpellProhibitor()
	{
		// Empty Construtor
	}

	public ProhibitedSpellType<T> getType()
	{
		return type;
	}

	public Set<T> getValueSet()
	{
		return valueSet;
	}

	public void setType(ProhibitedSpellType<T> prohibitedType)
	{
		type = prohibitedType;
	}

	public void addValue(T value)
	{
		if (valueSet == null)
		{
			valueSet = new HashSet<T>();
		}
		valueSet.add(value);
	}

	public boolean isProhibited(CDOMSpell s, CharacterDataStore aPC)
	{
		/*
		 * Note the rule is only "Prohibit Cleric/Druid spells based on
		 * Alignment" - thus this Globals check is only relevant to the
		 * Alignment type
		 */
		if (type.equals(ProhibitedSpellType.ALIGNMENT)
				&& !Globals.checkRule(RuleConstants.PROHIBITSPELLS))
		{
			return false;
		}

		if (!qualifies(aPC))
		{
			return false;
		}

		int hits = 0;

		for (T typeDesc : type.getCheckList(s))
		{
			for (T prohib : valueSet)
			{
				if (prohib.equals(typeDesc))
				{
					hits++;
				}
			}
		}

		return hits == type.getRequiredCount(valueSet);
	}

	@Override
	public int hashCode()
	{
		return type.hashCode() ^ valueSet.size();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof CDOMSpellProhibitor))
		{
			return false;
		}
		CDOMSpellProhibitor<?> other = (CDOMSpellProhibitor<?>) o;
		if ((type == null && other.type == null)
				|| (type != null && type.equals(other.type)))
		{
			return (other.valueSet == null && valueSet == null)
					|| valueSet != null && valueSet.equals(other.valueSet);
		}
		return false;
	}

	public String getLSTformat()
	{
		return type.toString();
	}
}
