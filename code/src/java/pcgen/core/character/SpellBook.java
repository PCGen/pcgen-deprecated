/*
 * SpellBook.java
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Jan 6, 2006
 *
 * $Id$
 *
 */
package pcgen.core.character;

import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;

/**
 * <code>SpellBook</code> contains details of a prepared spell list or 
 * a spell book. The term spell book was used as that is the term used
 * throughout the rest of the code.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class SpellBook implements Cloneable
{

	/** Spell book type indicating a list of known spells. */
	public static final int TYPE_KNOWN_SPELLS = 1;
	/** Spell book type indicating a list of prepared spells. */
	public static final int TYPE_PREPARED_LIST = 2;
	/** Spell book type indicating a written spell book. */
	public static final int TYPE_SPELL_BOOK = 3;

	private String name;
	private int type;
	private int numPages;
	private int numPagesUsed;
	private int numSpells;
	private String pageFormula;
	private String description;
	private Equipment equip;

	/**
	 * Create a new spell book with the basic details.
	 * 
	 * @param name The name of the spellbook
	 * @param type The type of spell book, be it prepared spells or written book.
	 */
	public SpellBook(String name, int type)
	{
		super();

		this.name = name;
		this.type = type;
	}

	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuffer result = new StringBuffer(name);
		if (type == TYPE_SPELL_BOOK)
		{
			result.append(" [");
			result.append(numPagesUsed);
			result.append('/');
			result.append(numPages);
			result.append(']');
		}
		return result.toString();
	}
	
	/**
	 * @return Returns the description.
	 */
	public final String getDescription()
	{
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public final void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return Returns the equip.
	 */
	public final Equipment getEquip()
	{
		return equip;
	}

	/**
	 * @param equip The equip to set.
	 */
	public final void setEquip(Equipment equip)
	{
		this.equip = equip;
		this.setNumPages(equip.getNumPages());
		this.setPageFormula(equip.getPageUsage());
	}

	/**
	 * @return Returns the name.
	 */
	public final String getName()
	{
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public final void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Get the number of pages in the book.
	 * @return Returns the numPages.
	 */
	public final int getNumPages()
	{
		return numPages;
	}

	/**
	 * Set the number of pages in the book.
	 * @param numPages The numPages to set.
	 */
	public final void setNumPages(int numPages)
	{
		this.numPages = numPages;
	}

	/**
	 * Get the pageFormula
	 * @return Returns the pageFormula.
	 */
	public final String getPageFormula()
	{
		return pageFormula;
	}

	/**
	 * Set the page formula
	 * @param pageFormula The pageFormula to set.
	 */
	public final void setPageFormula(String pageFormula)
	{
		this.pageFormula = pageFormula;
	}

	/**
	 * Get the spell book type.
	 * @return Returns the type.
	 */
	public final int getType()
	{
		return type;
	}

	/**
	 * Set the spell book type.
	 * @param type The type to set.
	 */
	public final void setType(int type)
	{
		this.type = type;
	}

	/**
	 * Retrieve the name of the spell book type.
	 * @return The name of the type of spell book.
	 */
	public final String getTypeName()
	{
		String retValue = "";
		switch (getType())
		{
			case SpellBook.TYPE_KNOWN_SPELLS:
				retValue = "Known Spell List";
				break;

			case SpellBook.TYPE_PREPARED_LIST:
				retValue = "Prepared Spell List";
				break;

			case SpellBook.TYPE_SPELL_BOOK:
				retValue = "Spell Book";
				break;

			default:
				retValue = "Unknown spell list type: " + getType();
				break;
		}
		return retValue;
	}

	/**
	 * Get the number of pages used.
	 * @return The number of pages used.
	 */
	public final int getNumPagesUsed()
	{
		return numPagesUsed;
	}


	/**
	 * Set the number of pages used.
	 * @param numPagesUsed The number of pages used.
	 */
	public final void setNumPagesUsed(int numPagesUsed)
	{
		this.numPagesUsed = numPagesUsed;
	}


	/**
	 * @return Returns the numSpells.
	 */
	public final int getNumSpells()
	{
		return numSpells;
	}


	/**
	 * @param numSpells The numSpells to set.
	 */
	public final void setNumSpells(int numSpells)
	{
		this.numSpells = numSpells;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone()
	{
		SpellBook aClone = null;
		try
		{
			aClone = (SpellBook) super.clone();
			if (equip != null)
			{
				aClone.equip = equip.clone();
			}
		}
		catch (CloneNotSupportedException e)
		{
			ShowMessageDelegate.showMessageDialog(
				"Clone of SpellBook failed due to " + e.getMessage(),
				Constants.s_APPNAME, MessageType.ERROR);
		}
		return aClone;
	}
}