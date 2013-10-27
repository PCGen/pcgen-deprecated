/*
 * Skill.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on December 13, 2002, 9:19 AM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.bonustokens;

import pcgen.core.bonus.BonusObj;

/**
 * <code>Skill</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public final class Skill extends BonusObj
{
	private static final String[] bonusHandled = {"SKILL"};

	private static final String[] bonusTags = {"LIST", "ALL"};

	@Override
	public boolean parseToken(final String token)
	{
		for (int i = 0; i < bonusTags.length; ++i)
		{
			if (bonusTags[i].equals(token))
			{
				addBonusInfo(Integer.valueOf(i));

				return true;
			}
		}

		if (token.startsWith("STAT=") || token.startsWith("TYPE="))
		{
			addBonusInfo(token.replace('=', '.'));
		}
		else
		{
			addBonusInfo(token);
		}

		return true;
	}

	protected String unparseToken(final Object obj)
	{
		if (obj instanceof Integer)
		{
			return bonusTags[((Integer) obj).intValue()];
		}

		return (String) obj;
	}

	protected String[] getBonusesHandled()
	{
		return bonusHandled;
	}
}
