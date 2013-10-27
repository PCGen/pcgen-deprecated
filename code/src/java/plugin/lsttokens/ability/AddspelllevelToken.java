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
package plugin.lsttokens.ability;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * Class deals with ADDSPELLLEVEL Token
 */
public class AddspelllevelToken implements AbilityLstToken, CDOMPrimaryToken<CDOMAbility>
{

	public String getTokenName()
	{
		return "ADDSPELLLEVEL";
	}

	public boolean parse(Ability ability, String value)
	{
		try
		{
			ability.setAddSpellLevel(Delta.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Bad addSpellLevel " + value);
		}
		return false;
	}

	public boolean parse(LoadContext context, CDOMAbility ability, String value)
	{
		try
		{
			Integer i = Integer.valueOf(value);
			if (i.intValue() < 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(ability, IntegerKey.ADD_SPELL_LEVEL,
				i);
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

	public String[] unparse(LoadContext context, CDOMAbility ability)
	{
		Integer lvl =
				context.getObjectContext().getInteger(ability,
					IntegerKey.ADD_SPELL_LEVEL);
		if (lvl == null)
		{
			return null;
		}
		if (lvl.intValue() < 0)
		{
			context
				.addWriteMessage(getTokenName() + " must be an integer >= 0");
			return null;
		}
		return new String[]{lvl.toString()};
	}

	public Class<CDOMAbility> getTokenClass()
	{
		return CDOMAbility.class;
	}
}
