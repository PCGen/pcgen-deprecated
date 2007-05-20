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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.core.spell.Spell;
import pcgen.util.enumeration.ProhibitedSpellType;

/**
 * @author stefan
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SpellProhibitor extends ConcretePrereqObject implements
		LSTWriteable
{

	private ProhibitedSpellType type = null;
	private Set<String> valueSet = null;

	public SpellProhibitor()
	{
		// Empty Construtor
	}

	public ProhibitedSpellType getType()
	{
		return type;
	}

	public Set<String> getValueSet()
	{
		return valueSet;
	}

	public void setType(ProhibitedSpellType prohibitedType)
	{
		type = prohibitedType;
	}

	public void addValue(String value)
	{
		if (valueSet == null)
		{
			valueSet = new TreeSet<String>();
		}
		valueSet.add(value);
	}

	public boolean isProhibited(Spell s, PlayerCharacter aPC)
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

		for (String typeDesc : type.getCheckList(s))
		{
			for (String prohib : valueSet)
			{
				if (prohib.equalsIgnoreCase(typeDesc))
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
		if (!(o instanceof SpellProhibitor))
		{
			return false;
		}
		SpellProhibitor other = (SpellProhibitor) o;
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
