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
package plugin.lsttokens.equipmentmodifier;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;

/**
 * Deals with ADDPROF token
 */
public class AddprofToken extends AbstractToken implements
		EquipmentModifierLstToken
{

	@Override
	public String getTokenName()
	{
		return "ADDPROF";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setProficiency(value);
		return true;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('.', value))
		{
			return false;
		}

		/*
		 * FIXME TODO ACTUALLY, this is NOT arbitrary length - it is very
		 * specifically Exotic.Heavy or Martial.Foo, etc. See Equipment.setBase
		 */
		StringTokenizer tok = new StringTokenizer(value, Constants.DOT);
		while (tok.hasMoreTokens())
		{
			String wtString = tok.nextToken();
			try
			{
				Type wt = Type.valueOf(wtString);
				context.obj.addToList(mod, ListKey.PROFICIENCY_TYPES, wt);
			}
			catch (IllegalArgumentException iae)
			{
				Logging.errorPrint(getTokenName()
					+ " had invalid Weapon Type: " + wtString);
				return false;
			}
		}
		// TODO This Token doesn't function in 5.11 - remove??
		return false;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		return null;
		// List<Type> profTypes = mod.getListFor(ListKey.PROFICIENCY_TYPES);
		// if (profTypes == null || profTypes.isEmpty())
		// {
		// return null;
		// }
		// return new String[]{StringUtil.join(profTypes, Constants.DOT)};
	}
}
