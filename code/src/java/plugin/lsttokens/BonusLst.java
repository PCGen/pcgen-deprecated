/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
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
package plugin.lsttokens;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusObj.StackType;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.BonusLoader;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * @author djones4
 */
public class BonusLst extends AbstractToken implements GlobalLstToken
{

	/**
	 * Returns token name
	 * 
	 * @return token name
	 */
	@Override
	public String getTokenName()
	{
		return "BONUS";
	}

	/**
	 * Parse BONUS token
	 * 
	 * @param obj
	 * @param value
	 * @param anInt
	 * @return true or false
	 */
	public boolean parse(PObject obj, String value, int anInt)
	{
		boolean result = false;
		value = CoreUtility.replaceAll(value, "<this>", obj.getKeyName());
		if (anInt > -9)
		{
			result = obj.addBonusList(anInt + "|" + value);
		}
		else
		{
			result = obj.addBonusList(value);
		}
		return result;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		value = CoreUtility.replaceAll(value, "<this>", obj.getKeyName());
		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
		String bonusName = aTok.nextToken().toUpperCase();
		String bonusInfo = aTok.nextToken().toUpperCase();
		String bValue =
				aTok.hasMoreTokens() ? aTok.nextToken().toUpperCase() : "0";

		BonusObj bonus =
				BonusLoader
					.getBonus(context, obj, bonusName, bonusInfo, bValue);

		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken().toUpperCase();

			if (PreParserFactory.isPreReqString(aString))
			{
				Prerequisite prereq = getPrerequisite(aString);
				if (prereq == null)
				{
					return false;
				}
				bonus.addPreReq(prereq);
			}
			else if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
			{
				String bonusType = aString.substring(5);
				int dotLoc = bonusType.indexOf('.');
				if (dotLoc != -1)
				{
					final String stackingFlag = bonusType.substring(dotLoc + 1);
					// TODO - Need to reset bonusType to exclude this but
					// there is too much dependancy on it being there
					// built into the code.
					if (stackingFlag.startsWith("REPLACE")) //$NON-NLS-1$
					{
						bonus.setStackingFlag(StackType.REPLACE);
					}
					else if (stackingFlag.startsWith("STACK")) //$NON-NLS-1$
					{
						bonus.setStackingFlag(StackType.STACK);
					}
				}
				boolean result = bonus.addType(bonusType);

				if (!result)
				{
					Logging.errorPrint(new StringBuffer().append(
						"Could not add type ").append(aString.substring(5))
						.append(" to bonusType ").append(bonusName).toString());
				}
			}
			else
			{
				Logging.errorPrint(new StringBuffer().append(getTokenName())
					.append(" error: Unexpected argument: ").append(aString)
					.append(" to bonus ").append(bonusName).append(
						"; value was: ").append(value).toString());
			}
		}
		context.getObjectContext().addToList(obj, ListKey.BONUSES, bonus);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
