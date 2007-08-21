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
import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.content.SimpleMovement;
import pcgen.core.Equipment;
import pcgen.core.Movement;
import pcgen.core.PObject;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class MoveLst extends AbstractToken implements GlobalLstToken
{

	@Override
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
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
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
				context.getGraphContext().grant(getTokenName(), obj,
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
		GraphChanges<SimpleMovement> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					obj, SimpleMovement.class);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		return new String[]{ReferenceUtilities.joinLstFormat(added,
			Constants.COMMA)};
	}
}
