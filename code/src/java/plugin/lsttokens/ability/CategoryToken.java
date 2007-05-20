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

import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.util.Logging;

/**
 * Deal with CATEGORY token
 */
public class CategoryToken implements AbilityLstToken
{

	public String getTokenName()
	{
		return "CATEGORY";
	}

	public boolean parse(Ability ability, String value)
	{
		ability.setCategory(value);
		return true;
	}

	public boolean parse(LoadContext context, Ability ability, String value)
	{
		try
		{
			AbilityCategory ac = AbilityCategory.valueOf(value);
			context.obj.put(ability, ObjectKey.CATEGORY, ac);
			return true;
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint(getTokenName()
				+ " had illegal AbilityCategory: " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, Ability ability)
	{
		AbilityCategory ac = context.obj.getObject(ability, ObjectKey.CATEGORY);
		if (ac == null)
		{
			context.addWriteMessage("Abilities must have an AbilityCategory");
			return null;
		}
		return new String[]{ac.toString()};
	}
}
