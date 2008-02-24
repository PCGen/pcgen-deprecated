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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deal with STACK token
 */
public class StackToken implements AbilityLstToken, CDOMPrimaryToken<CDOMAbility>
{

	public String getTokenName()
	{
		return "STACK";
	}

	public boolean parse(Ability ability, String value)
	{
		ability.setStacks(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMAbility ability, String value)
	{
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' as the "
					+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
				{
					Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
					return false;
				}
			}
			set = Boolean.FALSE;
		}
		context.getObjectContext().put(ability, ObjectKey.STACKS, set);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMAbility ability)
	{
		Boolean stacks =
				context.getObjectContext().getObject(ability, ObjectKey.STACKS);
		if (stacks == null)
		{
			return null;
		}
		return new String[]{stacks.booleanValue() ? "YES" : "NO"};
	}

	public Class<CDOMAbility> getTokenClass()
	{
		return CDOMAbility.class;
	}
}
