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

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with RACESUBTYPE Token
 */
public class RacesubtypeToken extends AbstractToken implements
		PCTemplateLstToken, CDOMPrimaryToken<CDOMTemplate>
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

	public boolean parse(LoadContext context, CDOMTemplate template, String value)
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

	public String[] unparse(LoadContext context, CDOMTemplate pct)
	{
		Changes<RaceSubType> addedChanges =
				context.getObjectContext().getListChanges(pct,
					ListKey.RACESUBTYPE);
		Changes<RaceSubType> removedChanges =
				context.getObjectContext().getListChanges(pct,
					ListKey.REMOVED_RACESUBTYPE);
		Collection<RaceSubType> added = addedChanges.getAdded();
		Collection<RaceSubType> removed = removedChanges.getAdded();
		if (added == null && removed == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean needPipe = false;
		if (removed != null)
		{
			for (RaceSubType rst : removed)
			{
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				sb.append(".REMOVE.").append(rst);
				needPipe = true;
			}
		}
		if (added != null)
		{
			for (RaceSubType rst : added)
			{
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				sb.append(rst);
				needPipe = true;
			}
		}
		return new String[]{sb.toString()};
	}

	public Class<CDOMTemplate> getTokenClass()
	{
		return CDOMTemplate.class;
	}
}
