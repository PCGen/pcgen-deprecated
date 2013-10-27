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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.cdom.inst.CDOMStat;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with STAT Token
 */
public class StatToken implements SpellLstToken, CDOMPrimaryToken<CDOMSpell>
{

	private static final Class<CDOMStat> PCSTAT_CLASS = CDOMStat.class;

	public String getTokenName()
	{
		return "STAT";
	}

	public boolean parse(Spell spell, String value)
	{
		spell.setStat(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMSpell spell, String value)
	{
		CDOMStat pcs = context.ref.getAbbreviatedObject(PCSTAT_CLASS, value);
		if (pcs == null)
		{
			Logging.errorPrint("Invalid Stat Abbreviation in Token + "
					+ getTokenName() + ": " + value);
			return false;
		}
		context.getObjectContext().put(spell, ObjectKey.SPELL_STAT, pcs);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMSpell spell)
	{
		CDOMStat pcs = context.getObjectContext().getObject(spell,
				ObjectKey.SPELL_STAT);
		if (pcs == null)
		{
			return null;
		}
		return new String[] { pcs.getLSTformat() };
	}

	public Class<CDOMSpell> getTokenClass()
	{
		return CDOMSpell.class;
	}
}
