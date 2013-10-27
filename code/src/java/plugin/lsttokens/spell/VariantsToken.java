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

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with VARIANTS Token
 */
public class VariantsToken extends AbstractToken implements SpellLstToken, CDOMPrimaryToken<CDOMSpell>
{

	@Override
	public String getTokenName()
	{
		return "VARIANTS";
	}

	public boolean parse(Spell spell, String value)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String variant = aTok.nextToken();

			if (variant.equals(".CLEAR"))
			{
				spell.clearVariants();
			}
			else
			{
				spell.addVariant(variant);
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMSpell spell, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		boolean first = true;
		while (aTok.hasMoreTokens())
		{
			String tok = aTok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tok))
			{
				if (!first)
				{
					Logging.errorPrint("Non-sensical use of .CLEAR in "
						+ getTokenName() + ": " + value);
					return false;
				}
				context.getObjectContext().removeList(spell, ListKey.VARIANTS);
			}
			else
			{
				context.getObjectContext().addToList(spell, ListKey.VARIANTS,
					tok);
			}
			first = false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMSpell spell)
	{
		Changes<String> changes =
				context.getObjectContext().getListChanges(spell,
					ListKey.VARIANTS);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Collection<?> added = changes.getAdded();
		boolean globalClear = changes.includesGlobalClear();
		if (globalClear)
		{
			sb.append(Constants.LST_DOT_CLEAR);
		}
		if (added != null && !added.isEmpty())
		{
			if (globalClear)
			{
				sb.append(Constants.PIPE);
			}
			sb.append(StringUtil.join(added, Constants.PIPE));
		}
		if (sb.length() == 0)
		{
			context.addWriteMessage(getTokenName()
				+ " was expecting non-empty changes to include "
				+ "added items or global clear");
			return null;
		}
		return new String[]{sb.toString()};
	}

	public Class<CDOMSpell> getTokenClass()
	{
		return CDOMSpell.class;
	}
}
