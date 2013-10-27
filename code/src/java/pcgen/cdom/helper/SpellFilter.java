/*
 * SpellFilter.java Copyright 2006-2007 (c) Tom Parker
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
 * Created on November 3, 2006
 * 
 * Current Ver: $Revision: 1522 $ Last Editor: $Author: thpr $ Last Edited:
 * $Date: 2006-10-24 18:40:09 -0400 (Tue, 24 Oct 2006) $
 * 
 */
package pcgen.cdom.helper;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.CDOMSpell;

public class SpellFilter
{

	private CDOMSpell spell;

	private Type spellType;

	private int spellLevel = -1;

	public void setSpellLevel(int lvl)
	{
		this.spellLevel = lvl;
	}

	public void setSpell(CDOMSpell sp)
	{
		this.spell = sp;
	}

	public void setSpellType(Type type)
	{
		this.spellType = type;
	}

	public boolean isEmpty()
	{
		return spellLevel < 0 && spellType == null && spell == null;
	}

	public boolean matchesFilter(CDOMSpell sp, int testSpellLevel)
	{
		if (spellLevel >= 0 && testSpellLevel != spellLevel)
		{
			return false;
		}
		if (spellType != null && !sp.containsInList(ListKey.TYPE, spellType))
		{
			return false;
		}
		if (spell != null && !spell.equals(sp))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (spellLevel >= 0)
		{
			sb.append("LEVEL=").append(spellLevel);
		}
		if (spellType != null)
		{
			if (sb.length() != 0)
			{
				sb.append(",");
			}
			sb.append("TYPE=").append(spellType);
		}
		if (spell != null)
		{
			if (sb.length() != 0)
			{
				sb.append(",");
			}
			sb.append(spell.get(StringKey.NAME));
		}
		return sb.toString();
	}

}
