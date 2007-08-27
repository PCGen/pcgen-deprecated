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
import pcgen.cdom.base.Constants;
import pcgen.core.Movement;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class MovecloneLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "MOVECLONE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		StringTokenizer moves = new StringTokenizer(value, Constants.COMMA);
		Movement cm;

		if (moves.countTokens() == 3)
		{
			cm = new Movement(2);
			cm.assignMovement(0, moves.nextToken(), "0");
			cm.assignMovement(1, moves.nextToken(), moves.nextToken());
		}
		else
		{
			Logging.errorPrint("Invalid Version of MOVECLONE detected: "
				+ value + "\n  MOVECLONE now has 3 arguments: "
				+ "SourceMove,DestinationMove,Modifier");
			return false;
		}
		cm.setMoveRatesFlag(2);
		obj.setMovement(cm, anInt);
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		StringTokenizer moves = new StringTokenizer(value, Constants.COMMA);
		Movement cm;

		if (moves.countTokens() == 3)
		{
			cm = new Movement(2);
			cm.assignMovement(0, moves.nextToken(), "0");
			cm.assignMovement(1, moves.nextToken(), moves.nextToken());
			cm.setMoveRatesFlag(2);
		}
		else
		{
			Logging.errorPrint("Invalid Version of MOVECLONE detected: "
				+ value + "\n  MOVECLONE now has 3 arguments: "
				+ "SourceMove,DestinationMove,Modifier");
			return false;
		}
		
		context.getGraphContext().grant(getTokenName(), obj, cm);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		return null;
		// GraphChanges<Movement> changes =
		// context.graph.getChangesFromToken(getTokenName(), obj,
		// Movement.class);
		// if (changes == null)
		// {
		// return null;
		// }
		// Collection<LSTWriteable> added = changes.getAdded();
		// if (added == null || added.isEmpty())
		// {
		// // Zero indicates no Token
		// return null;
		// }
		// return added.toArray(new String[added.size()]);
	}
}
