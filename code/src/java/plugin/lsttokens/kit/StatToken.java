/*
 * StatToken.java
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
 * Created on March 3, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit;

import java.net.URI;
import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.inst.CDOMStat;
import pcgen.cdom.kit.CDOMKitStat;
import pcgen.core.Kit;
import pcgen.core.kit.KitStat;
import pcgen.persistence.lst.KitLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * This class handles the STAT tag for Kits.<br>
 * The tag format is:<br>
 * <code>STAT:STR=15|DEX=14|WIS=10|CON=10|INT=10|CHA=18</code>
 */
public class StatToken extends KitLstToken implements
		CDOMSecondaryToken<CDOMKitStat>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "STAT";
	}

	/**
	 * Parses the STAT tag for a Kit. This tag is a pipe (|) separated list of
	 * stats to set.
	 * 
	 * @param aKit
	 *            the Kit object to add this information to
	 * @param value
	 *            the token string
	 * @return true if parse OK
	 */
	@Override
	public boolean parse(Kit aKit, String value, URI source)
	{
		KitStat stats = null;
		// Remove the STAT:
		final StringTokenizer aTok = new StringTokenizer(value, "|");

		while (aTok.hasMoreTokens())
		{
			final String statStr = aTok.nextToken();
			// STAT:value
			final int equalInd = statStr.indexOf("=");
			if (equalInd < 0)
			{
				Logging.errorPrint("Invalid STAT tag \"" + statStr + "\"");
				continue;
			}
			final String statType = statStr.substring(0, equalInd);
			final String statVal = statStr.substring(equalInd + 1);
			stats = new KitStat(statType, statVal);
			aKit.addStat(stats);
		}
		return (stats != null);
	}

	public Class<CDOMKitStat> getTokenClass()
	{
		return CDOMKitStat.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitStat kitStat, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('.', value))
		{
			return false;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			int equalLoc = token.indexOf('=');
			if (equalLoc == -1)
			{
				Logging.errorPrint("Illegal " + getTokenName()
						+ " did not have Stat=X format: " + value);
				return false;
			}
			if (equalLoc != token.lastIndexOf('='))
			{
				Logging.errorPrint("Illegal " + getTokenName()
						+ " had two equal signs, is not Stat=X format: "
						+ value);
				return false;
			}
			String statName = token.substring(0, equalLoc);
			CDOMStat stat = context.ref.getAbbreviatedObject(CDOMStat.class,
					statName);
			if (stat == null)
			{
				Logging.errorPrint("Unable to find STAT: " + statName);
				return false;
			}
			Formula statValue = FormulaFactory.getFormulaFor(token
					.substring(equalLoc + 1));
			kitStat.addStat(stat, statValue);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitStat kitStat)
	{
		Collection<CDOMStat> stats = kitStat.getStats();
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (CDOMStat stat : stats)
		{
			if (!first)
			{
				sb.append('|');
			}
			sb.append(stat.getLSTformat());
			sb.append('=');
			sb.append(kitStat.getFormulaFor(stat).toString());
			first = false;
		}
		return new String[] { sb.toString() };
	}

}
