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

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with RACESUBTYPE Token
 */
public class RacesubtypeToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "RACESUBTYPE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.addSubTypeString(value);
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint("Invalid " + getTokenName());
			Logging.errorPrint("  Requires at least one argument");
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			String aType = tok.nextToken();

			if (aType.startsWith(".REMOVE."))
			{
				String substring = aType.substring(8);
				if (substring.length() == 0)
				{
					Logging.errorPrint("Invalid .REMOVE. in " + getTokenName());
					Logging.errorPrint("  Requires an argument");
					return false;
				}
				template.addToListFor(ListKey.REMOVED_RACESUBTYPE, RaceSubType
					.getConstant(substring));
			}
			else
			{
				template.addToListFor(ListKey.RACESUBTYPE, RaceSubType
					.getConstant(aType));
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		List<RaceSubType> raceSubTypes = pct.getListFor(ListKey.RACESUBTYPE);
		List<RaceSubType> removedTypes =
				pct.getListFor(ListKey.REMOVED_RACESUBTYPE);
		if ((raceSubTypes == null || raceSubTypes.isEmpty())
			&& (removedTypes == null || removedTypes.isEmpty()))
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean needPipe = false;
		if (raceSubTypes != null)
		{
			for (RaceSubType rst : raceSubTypes)
			{
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				sb.append(rst);
				needPipe = true;
			}
		}
		if (removedTypes != null)
		{
			for (RaceSubType rst : removedTypes)
			{
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				sb.append(".REMOVE.").append(rst);
				needPipe = true;
			}
		}
		return new String[]{sb.toString()};
	}
}
