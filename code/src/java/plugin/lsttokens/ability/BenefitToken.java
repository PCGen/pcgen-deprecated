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

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Ability;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.util.Logging;

/**
 * This class deals with the BENEFIT Token
 */
public class BenefitToken implements AbilityLstToken
{

	public String getTokenName()
	{
		return "BENEFIT";
	}

	public boolean parse(Ability ability, String value)
	{
		ability.setBenefit(value);
		return true;
	}

	public boolean parse(LoadContext context, Ability ability, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		ability.put(StringKey.BENEFIT, value);
		return true;
	}

	public String[] unparse(LoadContext context, Ability ability)
	{
		String benefit = ability.get(StringKey.BENEFIT);
		if (benefit == null)
		{
			return null;
		}
		return new String[]{benefit};
	}
}
