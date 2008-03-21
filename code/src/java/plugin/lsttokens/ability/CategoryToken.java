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

import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deal with CATEGORY token
 */
public class CategoryToken implements AbilityLstToken, CDOMPrimaryToken<CDOMAbility>
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

	public boolean parse(LoadContext context, CDOMAbility ability, String value)
	{
		try
		{
			CDOMAbilityCategory ac = CDOMAbilityCategory.valueOf(value);
			context.ref.reassociateCategory(ac, ability);
			//TODO may be temporary...
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

	public String[] unparse(LoadContext context, CDOMAbility ability)
	{
		/*
		 * TODO How does this work with editor vs. real object and using
		 * reassociateReference??
		 */
		CDOMAbilityCategory ac =
				context.getObjectContext().getObject(ability,
					ObjectKey.CATEGORY);
		if (ac == null)
		{
			// TODO How to handle this for .MODs... this isn't ALWAYS required
			// context.addWriteMessage("Abilities must have an
			// AbilityCategory");
			return null;
		}
		return new String[]{ac.toString()};
	}

	public Class<CDOMAbility> getTokenClass()
	{
		return CDOMAbility.class;
	}
}
