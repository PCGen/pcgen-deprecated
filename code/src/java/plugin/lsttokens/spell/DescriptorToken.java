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
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SpellDescriptor;
import pcgen.core.Globals;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.util.Logging;

/**
 * Class deals with DESCRIPTOR Token
 */
public class DescriptorToken implements SpellLstToken
{

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
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		while (aTok.hasMoreTokens())
		{
			spell.addToListFor(ListKey.SPELL_DESCRIPTOR, SpellDescriptor
				.getConstant(aTok.nextToken()));
		}
		return true;
	}

	public String unparse(LoadContext context, Spell spell)
	{
		List<SpellDescriptor> descs =
				spell.getListFor(ListKey.SPELL_DESCRIPTOR);
		if (descs == null || descs.size() == 0)
		{
			return null;
		}
		return new StringBuilder().append(getTokenName()).append(':').append(
			StringUtil.join(descs, Constants.PIPE)).toString();
	}
}
