/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lstcompatibility.race;

import pcgen.core.Kit;
import pcgen.core.Race;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.RaceLstCompatibilityToken;

public class HitDice512Lst_DefMon extends AbstractToken implements
		RaceLstCompatibilityToken
{

	@Override
	public String getTokenName()
	{
		return "HITDICE";
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}

	public boolean parse(LoadContext context, Race race, String value)
		throws PersistenceLayerException
	{
		int commaLoc = value.indexOf(',');
		if (commaLoc == -1)
		{
			return false;
		}
		if (commaLoc != value.lastIndexOf(','))
		{
			return false;
		}
		Kit kit = race.getCompatMonsterKit();
		kit.setCompatHitDice(Integer.parseInt(value.substring(0, commaLoc)));
		kit.setCompatHitDiceSize(Integer
			.parseInt(value.substring(commaLoc + 1)));
		return true;
	}
}
