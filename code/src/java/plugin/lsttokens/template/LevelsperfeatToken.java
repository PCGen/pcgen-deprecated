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
package plugin.lsttokens.template;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with LEVELSPERFEAT Token
 */
public class LevelsperfeatToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "LEVELSPERFEAT";
	}

	// how many levels per feat.
	public boolean parse(PCTemplate template, String value)
	{
		try
		{
			final int newLevels = Integer.parseInt(value);

			if (newLevels >= 0)
			{
				template.setLevelsPerFeat(newLevels);
			}
			else
			{
				Logging.errorPrint("Levels Per Feat must be "
					+ "greater than or equal to zero: " + value);
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		try
		{
			Integer lpf = Integer.valueOf(value);
			if (lpf.intValue() < 0)
			{
				Logging.errorPrint("Levels Per Feat must be "
					+ "greater than or equal to zero: " + value);
				return false;
			}
			context.obj.put(template, IntegerKey.LEVELS_PER_FEAT, lpf);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Levels Per Feat must be a number "
				+ "greater than or equal to zero: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Integer lpf = context.obj.getInteger(pct, IntegerKey.LEVELS_PER_FEAT);
		if (lpf == null)
		{
			return null;
		}
		if (lpf.intValue() < 0)
		{
			context
				.addWriteMessage(getTokenName() + " must be an integer >= 0");
			return null;
		}
		return new String[]{lpf.toString()};
	}
}
