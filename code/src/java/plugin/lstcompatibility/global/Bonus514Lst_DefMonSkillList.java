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

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.CDOMKit;
import pcgen.cdom.inst.CDOMRace;
import pcgen.core.Constants;
import pcgen.core.kit.KitSkill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;

public class Bonus514Lst_DefMonSkillList extends AbstractToken implements
		CDOMCompatibilityToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "BONUS";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (!(obj instanceof CDOMRace))
		{
			return false;
		}
		value = StringUtil.replaceAll(value, "<this>", obj.getKeyName());
		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
		if (!aTok.nextToken().equalsIgnoreCase("SKILLRANK"))
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
		CDOMKit kit = ((CDOMRace) obj).getCompatMonsterKit();
		StringTokenizer st = new StringTokenizer(bonusInfo, Constants.COMMA);
		while (st.hasMoreTokens())
		{
			String skill = st.nextToken();
			KitSkill ks = new KitSkill(skill);
			ks.setRank(bValue);
			kit.addObject(ks);
		}
		return true;
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
		return 14;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
