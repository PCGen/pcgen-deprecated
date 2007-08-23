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

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.spell.Spell;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.util.Logging;

/**
 * Class deals with ITEM Token
 */
public class ItemToken extends AbstractToken implements SpellLstToken
{

	@Override
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
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(value, Constants.COMMA);

		while (aTok.hasMoreTokens())
		{
			String tokString = aTok.nextToken();
			int bracketLoc = tokString.indexOf('[');
			if (bracketLoc == 0)
			{
				// Check ends with bracket
				if (tokString.lastIndexOf(']') != tokString.length() - 1)
				{
					Logging.errorPrint("Invalid " + getTokenName()
						+ ": mismatched open Bracket: " + tokString + " in "
						+ value);
					return false;
				}
				String substring =
						tokString.substring(1, tokString.length() - 1);
				if (substring.length() == 0)
				{
					Logging.errorPrint("Invalid " + getTokenName()
						+ ": cannot be empty item in brackets []");
					return false;
				}
				context.getObjectContext().addToList(spell,
					ListKey.PROHIBITED_ITEM, Type.getConstant(substring));
			}
			else
			{
				if (tokString.lastIndexOf(']') != -1)
				{
					Logging.errorPrint("Invalid " + getTokenName()
						+ ": mismatched close Bracket: " + tokString + " in "
						+ value);
					return false;
				}
				context.getObjectContext().addToList(spell, ListKey.ITEM,
					Type.getConstant(tokString));
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, Spell spell)
	{
		Changes<Type> changes =
				context.getObjectContext().getListChanges(spell, ListKey.ITEM);
		Changes<Type> proChanges =
				context.getObjectContext().getListChanges(spell,
					ListKey.PROHIBITED_ITEM);
		Collection<Type> changeAdded = changes.getAdded();
		Collection<Type> proAdded = proChanges.getAdded();
		StringBuilder sb = new StringBuilder();
		boolean needComma = false;
		if (changeAdded != null)
		{
			for (Type t : changeAdded)
			{
				if (needComma)
				{
					sb.append(Constants.COMMA);
				}
				sb.append(t.toString());
				needComma = true;
			}
		}
		if (proAdded != null)
		{
			for (Type t : proAdded)
			{
				if (needComma)
				{
					sb.append(Constants.COMMA);
				}
				sb.append('[').append(t.toString()).append(']');
				needComma = true;
			}
		}
		if (sb.length() == 0)
		{
			return null;
		}
		return new String[]{sb.toString()};
	}
}
