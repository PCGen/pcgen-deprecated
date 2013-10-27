/*
 * ValuesToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.table;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.kit.CDOMKitGear;
import pcgen.cdom.kit.CDOMKitTable;
import pcgen.cdom.kit.CDOMKitTable.RangeLimited;
import pcgen.core.Kit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.KitTableLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * VALUES token for KitTable
 */
public class ValuesToken extends AbstractToken implements KitTableLstToken,
		CDOMSecondaryToken<CDOMKitTable>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "VALUES";
	}

	/**
	 * parse
	 * 
	 * @param kit
	 *            Kit
	 * @param value
	 *            String
	 * @return boolean
	 */
	public boolean parse(Kit kit, final String tableName, String value)
	{
		final StringTokenizer fieldToken = new StringTokenizer(value, "|");
		while (fieldToken.hasMoreTokens())
		{
			String val = fieldToken.nextToken();
			String range = fieldToken.nextToken();
			int ind = -1;
			String lowVal;
			String highVal;
			if ((ind = range.indexOf(",")) != -1)
			{
				lowVal = range.substring(0, ind);
				highVal = range.substring(ind + 1);
			}
			else
			{
				lowVal = highVal = range;
			}
			kit.addLookupValue(tableName, val, lowVal, highVal);
		}
		return true;
	}

	public Class<CDOMKitTable> getTokenClass()
	{
		return CDOMKitTable.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitTable kitTable,
			String value) throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		while (st.hasMoreTokens())
		{
			String thing = st.nextToken();
			CDOMKitGear optionInfo = new CDOMKitGear();
			for (String s : thing.split("[\\[\\]]"))
			{
				if (s.length() == 0)
				{
					continue;
				}
				int colonLoc = s.indexOf(':');
				if (colonLoc == -1)
				{
					Logging.errorPrint("Expected colon in Value item: " + s
							+ " within: " + value);
					return false;
				}
				String key = s.substring(0, colonLoc);
				String thingValue = s.substring(colonLoc + 1);
				context.processSubToken(optionInfo, getParentToken(), key,
						thingValue);
			}
			if (!st.hasMoreTokens())
			{
				Logging.errorPrint("Odd token count in Value: " + value);
				return false;
			}
			String range = st.nextToken();
			if (!processRange(kitTable, optionInfo, range))
			{
				Logging.errorPrint("Invalid Range in Value: " + range
						+ " within " + value);
				return false;
			}
		}

		return true;
	}

	private boolean processRange(CDOMKitTable kitTable, CDOMKitGear optionInfo,
			String range)
	{
		int commaLoc = range.indexOf(',');
		String minString;
		String maxString;
		if (commaLoc == -1)
		{
			minString = range;
			maxString = range;
		}
		else if (commaLoc != range.lastIndexOf(','))
		{
			return false;
		}
		else
		{
			minString = range.substring(0, commaLoc);
			maxString = range.substring(commaLoc + 1);
		}
		Formula min = FormulaFactory.getFormulaFor(minString);
		Formula max = FormulaFactory.getFormulaFor(maxString);
		kitTable.addGear(optionInfo, min, max);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitTable kitTable)
	{
		StringBuilder sb = new StringBuilder();
		List<RangeLimited> list = kitTable.getList();
		boolean first = false;
		for (RangeLimited rl : list)
		{
			if (!first)
			{
				sb.append(Constants.PIPE);
			}
			String[] unparse = context.unparse(rl.gear, getParentToken());
			if (unparse.length == 1)
			{
				sb.append(unparse[0]);
			}
			else
			{
				for (String s : unparse)
				{
					sb.append('[');
					sb.append(s);
					sb.append(']');
				}
			}
			sb.append(Constants.PIPE);
			sb.append(rl.lowRange.toString());
			sb.append(',');
			sb.append(rl.highRange.toString());
			first = false;
		}
		return new String[] { sb.toString() };
	}
}
