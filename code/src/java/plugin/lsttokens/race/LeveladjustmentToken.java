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
package plugin.lsttokens.race;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.inst.CDOMRace;
import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with LEVELADJUSTMENT Token
 */
public class LeveladjustmentToken implements RaceLstToken, CDOMPrimaryToken<CDOMRace>
{

	public String getTokenName()
	{
		return "LEVELADJUSTMENT";
	}

	public boolean parse(Race race, String value)
	{
		race.setLevelAdjustment(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMRace race, String value)
	{
		context.getObjectContext().put(race, FormulaKey.LEVEL_ADJUSTMENT,
			FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMRace race)
	{
		Formula f =
				context.getObjectContext().getFormula(race,
					FormulaKey.LEVEL_ADJUSTMENT);
		if (f == null)
		{
			return null;
		}
		return new String[]{f.toString()};
	}

	public Class<CDOMRace> getTokenClass()
	{
		return CDOMRace.class;
	}
}
