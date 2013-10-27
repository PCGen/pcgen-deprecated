/*
 * KitSpellBook.java
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on September 26, 2005
 *
 * $Id$
 */
package pcgen.core.kit;

import java.util.*;

/**
 * Spell books for kits
 */
public class KitSpellBook// extends BaseKit
{
	private String className;
	private String theName;
	private HashMap<String, List<KitSpellBookEntry>> theSpells = new HashMap<String, List<KitSpellBookEntry>>();

	/**
	 * Constructor
	 * @param aClassName
	 * @param aName
	 */
	public KitSpellBook(final String aClassName, final String aName)
	{
		className = aClassName;
		theName = aName;
	}

	/**
	 * Get the name of the spell book
	 * @return the name of the spell book
	 */
	public final String getName()
	{
		return theName;
	}

	/**
	 * Get the spells in the spell book
	 * @return the spells in the spell book
	 */
	public Collection<List<KitSpellBookEntry>> getSpells()
	{
		return theSpells.values();
	}

	/**
	 * Add a spell to the spell book
	 * @param aSpell
	 * @param metamagicList
	 * @param countStr
	 */
	public void addSpell(final String aSpell, final List<String> metamagicList,
						 final String countStr)
	{
		int numCopies = 1;
		try
		{
			numCopies = Integer.parseInt(countStr);
		}
		catch (NumberFormatException e)
		{
			// Assume one copy.
		}
		List<KitSpellBookEntry> entries = theSpells.get(aSpell);
		if (entries == null)
		{
			// This spellbook doesn't contain this spell.
			entries = new ArrayList<KitSpellBookEntry>();
			KitSpellBookEntry sbe = new KitSpellBookEntry(className, theName, aSpell, metamagicList);
			sbe.addCopies(numCopies-1);
			entries.add(sbe);
			theSpells.put(aSpell, entries);
		}
		else
		{
			// We have a copy of this spell already.
			// Check to see if the modifiers are the same
			boolean found = false;
			KitSpellBookEntry sbe = null;
			for (Iterator<KitSpellBookEntry> i = entries.iterator(); i.hasNext(); )
			{
				sbe = i.next();
				List<String> modifiers = sbe.getModifiers();
				if (modifiers == null)
				{
					if (metamagicList != null && metamagicList.size() > 0)
					{
						// This spell is modified and we are adding one that isn't
						continue;
					}
					found = true;
					break;
				}
				else if (modifiers.size() != metamagicList.size())
				{
					continue;
				}
				int count = metamagicList.size() - 1;
				for ( String mod : modifiers )
				{
					if (!metamagicList.contains(mod))
					{
						continue;
					}
					count--;
				}
				if (count == 0)
				{
					found = true;
					break;
				}
			}
			if (found)
			{
				sbe.addCopies(numCopies);
			}
			else
			{
				entries.add(new KitSpellBookEntry(className, theName, aSpell, metamagicList));
			}
		}
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(theName + ": ");

		boolean first = true;
		for ( List<KitSpellBookEntry> entries : theSpells.values() )
		{
			if (!first)
			{
				buf.append(",");
			}

			for ( KitSpellBookEntry sbe : entries )
			{
				buf.append(sbe.getName());
				if (sbe.getModifiers() != null)
				{
					for ( String mod : sbe.getModifiers() )
					{
						buf.append(" [").append(mod).append("]");
					}
				}
				if (sbe.getCopies() > 1)
				{
					buf.append(" (").append(sbe.getCopies()).append(")");
				}
				first = false;
			}
		}
		return buf.toString();
	}
}

