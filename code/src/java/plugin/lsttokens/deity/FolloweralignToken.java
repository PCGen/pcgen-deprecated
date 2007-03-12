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
package plugin.lsttokens.deity;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AlignmentType;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Deity;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.DeityLstToken;
import pcgen.util.Logging;

/**
 * Class deals with FOLLOWERALIGN Token
 */
public class FolloweralignToken implements DeityLstToken
{

	public String getTokenName()
	{
		return "FOLLOWERALIGN";
	}

	public boolean parse(Deity deity, String value)
	{
		deity.setFollowerAlignments(value);
		return true;
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		if (value.charAt(0) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with , : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with , : " + value);
			return false;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}

		StringTokenizer commaTok = new StringTokenizer(value, Constants.COMMA);
		while (commaTok.hasMoreTokens())
		{
			try
			{
				deity.addToListFor(ListKey.FOLLOWER_ALIGN, AlignmentType
					.valueOf(commaTok.nextToken()));
			}
			catch (IllegalArgumentException e)
			{
				Logging.errorPrint("Invalid Alignment found in "
					+ getTokenName() + ": " + value);
				return false;
			}
		}
		return true;
	}

	public String unparse(LoadContext context, Deity deity)
	{
		List<AlignmentType> atypes = deity.getListFor(ListKey.FOLLOWER_ALIGN);
		if (atypes == null || atypes.isEmpty())
		{
			return null;
		}
		StringBuilder sb =
				new StringBuilder().append(getTokenName()).append(':');
		boolean needPipe = false;
		for (AlignmentType at : atypes)
		{
			if (needPipe)
			{
				sb.append(Constants.COMMA);
			}
			needPipe = true;
			sb.append(at);
		}
		return sb.toString();
	}
}
