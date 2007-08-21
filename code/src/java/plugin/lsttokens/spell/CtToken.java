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

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.util.Logging;

/**
 * Class deals with CT Token
 */
public class CtToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "CT";
	}

	public boolean parse(Spell spell, String value)
	{
		try
		{
			int ct = Integer.parseInt(value);
			if (ct < 0)
			{
				Logging.errorPrint(getTokenName()
					+ " can not have a negative value");
				return false;
			}
			spell.setCastingThreshold(ct);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " must be an integer (greater than or equal to zero)");
			return false;
		}
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		try
		{
			Integer ct = Integer.valueOf(value);
			if (ct.intValue() < 0)
			{
				Logging.errorPrint(getTokenName()
					+ " requires a positive Integer");
				return false;
			}
			context.getObjectContext().put(spell, IntegerKey.CASTING_THRESHOLD,
				ct);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " expected an integer.  Tag must be of the form: "
				+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, Spell spell)
	{
		Integer i =
				context.getObjectContext().getInteger(spell,
					IntegerKey.CASTING_THRESHOLD);
		if (i == null)
		{
			return null;
		}
		if (i.intValue() < 0)
		{
			context.addWriteMessage(getTokenName()
				+ " requires a positive Integer");
			return null;
		}
		return new String[]{i.toString()};
	}
}
