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

import java.math.BigDecimal;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.SimpleMovement;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Equipment;
import pcgen.core.Movement;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class MoveLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "MOVE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (obj instanceof Equipment)
		{
			return false;
		}
		Movement cm = Movement.getOldMovementFrom(value);
		cm.setMoveRatesFlag(0);
		obj.setMovement(cm, anInt);
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (value == null)
		{
			Logging.errorPrint("Error encountered while parsing "
				+ getTokenName() + ": " + value);
			Logging.errorPrint("  Null initialization String illegal");
			return false;
		}
		if (value.length() == 0)
		{
			Logging.errorPrint("Error encountered while parsing "
				+ getTokenName() + ": " + value);
			Logging.errorPrint("  Empty initialization String illegal");
			return false;
		}
		if (value.charAt(0) == ',')
		{
			Logging.errorPrint("Error encountered while parsing "
				+ getTokenName() + ": " + value);
			Logging.errorPrint("  Movement arguments may not start with ,| : "
				+ value);
			return false;
		}
		if (value.charAt(value.length() - 1) == ',')
		{
			Logging.errorPrint("Error encountered while parsing "
				+ getTokenName() + ": " + value);
			Logging.errorPrint("  Movement arguments may not end with , : "
				+ value);
			return false;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint("Error encountered while parsing "
				+ getTokenName() + ": " + value);
			Logging
				.errorPrint("  Movement arguments uses double separator ,, : "
					+ value);
			return false;
		}
		final StringTokenizer moves = new StringTokenizer(value, ",");

		int tokenCount = moves.countTokens();
		if (tokenCount % 2 != 0)
		{
			Logging.errorPrint("Error encountered while parsing "
				+ getTokenName() + ": " + value);
			Logging
				.errorPrint("  String must value count that is a multiple of 2: "
					+ value);
			return false;
		}
		while (moves.hasMoreTokens())
		{
			String moveType = moves.nextToken(); // e.g. "Walk"
			String moveDistance = moves.nextToken();

			try
			{
				BigDecimal distance = new BigDecimal(moveDistance);
				if (distance.compareTo(BigDecimal.ZERO) < 0)
				{
					Logging.errorPrint("Error encountered while parsing "
						+ getTokenName() + ": " + value);
					Logging.errorPrint("  Distance: " + moveDistance
						+ " was negative");
					return false;
				}
				context.graph.linkObjectIntoGraph(getTokenName(), obj,
					new SimpleMovement(moveType, distance));
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Error encountered while parsing "
					+ getTokenName() + ": " + value);
				Logging.errorPrint("  Badly formed MOVE token: " + moveDistance
					+ " was not a double");
				return false;
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					SimpleMovement.class);
		if (edgeList == null || edgeList.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (PCGraphEdge edge : edgeList)
		{
			SimpleMovement m = (SimpleMovement) edge.getSinkNodes().get(0);
			set.add(m.toLSTString());
		}
		return new String[]{StringUtil.join(set, ",")};
	}
}
