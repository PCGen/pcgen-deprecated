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

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deal with COST Token
 */
public class CostToken implements AbilityLstToken, CDOMPrimaryToken<CDOMAbility>
{

	public String getTokenName()
	{
		return "COST";
	}

	public boolean parse(Ability ability, String value)
	{
		ability.setCost(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMAbility ability, String value)
	{
		try
		{
			BigDecimal cost = new BigDecimal(value);
			if (cost.compareTo(BigDecimal.ZERO) < 0)
			{
				Logging.errorPrint(getTokenName()
					+ " must be a positive number: " + value);
				return false;
			}
			context.getObjectContext().put(ability, ObjectKey.COST, cost);
			return true;
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint(getTokenName() + " expected a number: " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, CDOMAbility ability)
	{
		BigDecimal bd =
				context.getObjectContext().getObject(ability, ObjectKey.COST);
		if (bd == null)
		{
			return null;
		}
		return new String[]{bd.toString()};
	}

	public Class<CDOMAbility> getTokenClass()
	{
		return CDOMAbility.class;
	}
}
