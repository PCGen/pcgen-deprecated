/*
 * SpellProhibitor.java
 * Copyright 2005 (c) Tom Parker <thpr@sourceforge.net>
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
 * Created on November 3, 2006
 *
 * Current Ver: $Revision: 1522 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2006-10-24 18:40:09 -0400 (Tue, 24 Oct 2006) $
 *
 */
package pcgen.cdom.content;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.core.spell.Spell;

public class KnownSpellIdentifier extends ConcretePrereqObject
{

	private final CDOMReference<Spell> ref;

	private final Integer spellLevel;

	public KnownSpellIdentifier(CDOMReference<Spell> sr, Integer levelLimit)
	{
		if (sr == null)
		{
			throw new IllegalArgumentException("Spell Reference cannot be null");
		}
		ref = sr;
		spellLevel = levelLimit;
	}

	public boolean matchesFilter(Spell s, int testSpellLevel)
	{
		// TODO Need to implement this method
		return false;
	}

	public CDOMReference<Spell> getLimit()
	{
		return ref;
	}

	public Integer getSpellLevel()
	{
		return spellLevel;
	}

	@Override
	public int hashCode()
	{
		return spellLevel == null ? ref.hashCode() : spellLevel.intValue()
			* ref.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (!(o instanceof KnownSpellIdentifier))
		{
			return false;
		}
		KnownSpellIdentifier other = (KnownSpellIdentifier) o;
		if (spellLevel == null)
		{
			return other.spellLevel == null && ref.equals(other.ref);
		}
		return ((spellLevel == null && other.spellLevel == null) || spellLevel
			.equals(other.spellLevel))
			&& ref.equals(other.ref);
	}
}
