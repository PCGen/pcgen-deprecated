/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.spell;

import java.util.List;

import pcgen.cdom.base.Restriction;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.restriction.UnresolvedTypeRestriction;
import pcgen.core.Equipment;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with ITEM Token
 */
public class ItemToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "ITEM";
	}

	public boolean parse(Spell spell, String value)
	{
		spell.setCreatableItem(value);
		return true;
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		int bracketLoc = value.indexOf('[');
		boolean negate = false;
		String itemString;
		if (bracketLoc == 0)
		{
			// Check ends with bracket
			if (value.lastIndexOf(']') != value.length() - 1)
			{
				return false;
			}
			negate = true;
			itemString = value.substring(1, value.length() - 1);
		}
		else
		{
			itemString = value;
		}
		Type t = Type.getConstant(itemString);
		UnresolvedTypeRestriction<Equipment> tr =
				new UnresolvedTypeRestriction<Equipment>(Equipment.class, t,
					negate);
		/*
		 * CONSIDER FIXME Need to think about how this should work... this is
		 * really an ANCESTOR restriction here, since it is based off of the
		 * parent item (e.g. if it was [Weapon], and this was attached to an
		 * EqMod??? What happens?)
		 */
		spell.addSourceRestriction(tr);
		return true;
	}

	public String[] unparse(LoadContext context, Spell spell)
	{
		List<Restriction<?>> resList = spell.getSourceRestrictions();
		StringBuilder sb = new StringBuilder();
		for (Restriction<?> r : resList)
		{
			if (r instanceof UnresolvedTypeRestriction)
			{
				UnresolvedTypeRestriction<?> tr = (UnresolvedTypeRestriction) r;
				if (tr.getRestrictedType().equals(Equipment.class))
				{
					sb.append(getTokenName()).append(':')
						.append(tr.toLSTform());
				}
			}
		}
		return sb.length() == 0 ? null : new String[]{sb.toString()};
	}
}
