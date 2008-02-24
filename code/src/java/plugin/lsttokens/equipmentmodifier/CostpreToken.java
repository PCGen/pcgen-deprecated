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

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.inst.CDOMEqMod;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Deals with COSTPRE token
 */
public class CostpreToken implements EquipmentModifierLstToken, CDOMPrimaryToken<CDOMEqMod>
{

	public String getTokenName()
	{
		return "COSTPRE";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setPreCost(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMEqMod mod,
		String value)
	{
		context.getObjectContext().put(mod, FormulaKey.BASECOST,
			FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMEqMod mod)
	{
		Formula f =
				context.getObjectContext().getFormula(mod, FormulaKey.BASECOST);
		if (f == null)
		{
			return null;
		}
		return new String[]{f.toString()};
	}

	public Class<CDOMEqMod> getTokenClass()
	{
		return CDOMEqMod.class;
	}
}
