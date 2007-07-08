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
package plugin.lstcompatibility.global;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Constants;
import pcgen.core.Kit;
import pcgen.core.Race;
import pcgen.core.kit.KitStat;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstCompatibilityToken;

public class Bonus514Lst_DefMonStat extends AbstractToken implements
		GlobalLstCompatibilityToken
{

	@Override
	public String getTokenName()
	{
		return "BONUS";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (!(obj instanceof Race))
		{
			return false;
		}
		value = CoreUtility.replaceAll(value, "<this>", obj.getKeyName());
		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
		if (!aTok.nextToken().equalsIgnoreCase("STAT"))
		{
			return false;
		}
		String bonusInfo = aTok.nextToken();
		String bValue = aTok.hasMoreTokens() ? aTok.nextToken() : "0";
		if (!aTok.hasMoreTokens())
		{
			// Only looking for Default Monster items
			return false;
		}
		String aString = aTok.nextToken().toUpperCase();
		if (!aString.equals("PREDEFAULTMONSTER:Y"))
		{
			// Only looking for Default Monster items
			return false;
		}
		if (aTok.hasMoreTokens())
		{
			// Only looking for Default Monster items, no types expected...
			return false;
		}
		int statMod = Integer.parseInt(bValue);
		String newStat = Integer.toString(statMod + 10);
		Kit kit = ((Race) obj).getCompatMonsterKit();
		StringTokenizer st = new StringTokenizer(bonusInfo, Constants.COMMA);
		while (st.hasMoreTokens())
		{
			String stat = st.nextToken();
			kit.addStat(new KitStat(stat, newStat));
		}
		return true;
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 1;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}
}
