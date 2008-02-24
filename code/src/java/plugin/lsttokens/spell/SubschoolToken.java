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
import pcgen.cdom.enumeration.SpellSubSchool;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with SUBSCHOOL Token
 */
public class SubschoolToken extends AbstractToken implements SpellLstToken, CDOMPrimaryToken<CDOMSpell>
{

	@Override
	public String getTokenName()
	{
		return "SUBSCHOOL";
	}

	public boolean parse(Spell spell, String value)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		while (aTok.hasMoreTokens())
		{
			String token = aTok.nextToken();
			spell.addSubschool(token);
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

		while (aTok.hasMoreTokens())
		{
			context.getObjectContext().addToList(spell,
				ListKey.SPELL_SUBSCHOOL,
				SpellSubSchool.getConstant(aTok.nextToken()));
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMSpell spell)
	{
		Changes<SpellSubSchool> changes =
				context.getObjectContext().getListChanges(spell,
					ListKey.SPELL_SUBSCHOOL);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[]{StringUtil.join(changes.getAdded(), Constants.PIPE)};
	}

	public Class<CDOMSpell> getTokenClass()
	{
		return CDOMSpell.class;
	}
}
