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

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.PCTemplate;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with RACESUBTYPE Token
 */
public class RacesubtypeToken extends AbstractToken implements
		PCTemplateLstToken
{

	@Override
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
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
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
				context.getObjectContext().addToList(template,
					ListKey.REMOVED_RACESUBTYPE,
					RaceSubType.getConstant(substring));
			}
			else
			{
				context.getObjectContext().addToList(template,
					ListKey.RACESUBTYPE, RaceSubType.getConstant(aType));
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Changes<RaceSubType> addedChanges =
				context.getObjectContext().getListChanges(pct,
					ListKey.RACESUBTYPE);
		Changes<RaceSubType> removedChanges =
				context.getObjectContext().getListChanges(pct,
					ListKey.REMOVED_RACESUBTYPE);
		if (addedChanges == null && removedChanges == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean needPipe = false;
		if (addedChanges != null)
		{
			for (RaceSubType rst : addedChanges.getAdded())
			{
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				sb.append(rst);
				needPipe = true;
			}
		}
		if (removedChanges != null)
		{
			for (RaceSubType rst : removedChanges.getAdded())
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
