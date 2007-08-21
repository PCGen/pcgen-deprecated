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

import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SpellDescriptor;
import pcgen.core.Globals;
import pcgen.core.spell.Spell;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with DESCRIPTOR Token
 */
public class DescriptorToken extends AbstractToken implements SpellLstToken
{

	@Override
	public String getTokenName()
	{
		return "DESCRIPTOR";
	}

	public boolean parse(Spell spell, String value)
	{
		final StringTokenizer tok = new StringTokenizer(value, "|", false);

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			spell.addDescriptor(token);
			Globals.addSpellDescriptorSet(token);
		}
		return true;
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		while (aTok.hasMoreTokens())
		{
			context.getObjectContext().addToList(spell,
				ListKey.SPELL_DESCRIPTOR,
				SpellDescriptor.getConstant(aTok.nextToken()));
		}
		return true;
	}

	public String[] unparse(LoadContext context, Spell spell)
	{
		Changes<SpellDescriptor> changes =
				context.getObjectContext().getListChanges(spell,
					ListKey.SPELL_DESCRIPTOR);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[]{StringUtil.join(changes.getAdded(), Constants.PIPE)};
	}
}
