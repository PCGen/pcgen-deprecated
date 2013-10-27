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
import pcgen.cdom.inst.CDOMEqMod;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Deals with REPLACES token
 */
public class ReplacesToken extends AbstractToken implements
		EquipmentModifierLstToken, CDOMPrimaryToken<CDOMEqMod>
{

	@Override
	public String getTokenName()
	{
		return "REPLACES";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setReplacement(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMEqMod mod, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);
		while (tok.hasMoreTokens())
		{
			CDOMSingleRef<CDOMEqMod> ref = context.ref.getCDOMReference(
					CDOMEqMod.class, tok.nextToken());
			context.getObjectContext().addToList(mod, ListKey.REPLACED_KEYS,
					ref);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMEqMod mod)
	{
		Changes<CDOMSingleRef<CDOMEqMod>> changes = context.getObjectContext()
				.getListChanges(mod, ListKey.REPLACED_KEYS);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[] { ReferenceUtilities.joinLstFormat(changes
				.getAdded(), Constants.COMMA) };
	}

	public Class<CDOMEqMod> getTokenClass()
	{
		return CDOMEqMod.class;
	}
}
