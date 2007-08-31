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

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.formula.AddingFormula;
import pcgen.base.formula.DividingFormula;
import pcgen.base.formula.MultiplyingFormula;
import pcgen.base.formula.ReferenceFormula;
import pcgen.base.formula.SubtractingFormula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.constructor.MovementFormulaConstructor;
import pcgen.core.Movement;
import pcgen.core.PObject;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class MovecloneLst extends AbstractToken implements GlobalLstToken
{

	@Override
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
			Logging.deprecationPrint("Invalid Version of MOVECLONE detected: "
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
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}
		StringTokenizer moves = new StringTokenizer(value, Constants.COMMA);

		if (moves.countTokens() != 3)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
				"Invalid Version of MOVECLONE detected: " + value
					+ "\n  MOVECLONE has 3 arguments: "
					+ "SourceMove,DestinationMove,Modifier");
			return false;
		}

		String oldType = moves.nextToken();
		String newType = moves.nextToken();
		String formulaString = moves.nextToken();
		ReferenceFormula<Integer> form;

		if (formulaString.startsWith("/"))
		{
			int denom = Integer.parseInt(formulaString.substring(1));
			if (denom <= 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " was expecting a Positive Integer "
					+ "for dividing Movement, was : "
					+ formulaString.substring(1));
				return false;
			}
			form = new DividingFormula(denom);
		}
		else if (formulaString.startsWith("*"))
		{
			int mult = Integer.parseInt(formulaString.substring(1));
			if (mult <= 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " was expecting a Positive "
					+ "Integer for multiplying Movement, was : "
					+ formulaString.substring(1));
				return false;
			}
			form = new MultiplyingFormula(mult);
		}
		else if (formulaString.startsWith("+"))
		{
			int add = Integer.parseInt(formulaString.substring(1));
			if (add <= 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " was expecting a Positive "
					+ "Integer for adding Movement, was : "
					+ formulaString.substring(1));
				return false;
			}
			form = new AddingFormula(add);
		}
		else
		{
			int sub = Integer.parseInt(formulaString);
			if (sub == 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " was expecting a non-zero "
					+ "Integer for Movement modifier, was : " + formulaString);
				return false;
			}
			if (sub < 0)
			{
				form = new SubtractingFormula(-sub);
			}
			else
			{
				form = new AddingFormula(sub);
			}
		}

		MovementFormulaConstructor fc =
				new MovementFormulaConstructor(oldType, newType, form);

		context.getGraphContext().grant(getTokenName(), obj, fc);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		AssociatedChanges<MovementFormulaConstructor> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					obj, MovementFormulaConstructor.class);
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
		Set<String> set = new TreeSet<String>();
		for (LSTWriteable lstw : added)
		{
			MovementFormulaConstructor fc =
					MovementFormulaConstructor.class.cast(lstw);
			StringBuilder sb = new StringBuilder();
			sb.append(fc.getBaseType());
			sb.append(Constants.COMMA);
			sb.append(fc.getNewType());
			sb.append(Constants.COMMA);
			sb.append(fc.getLSTformat());
			set.add(sb.toString());
		}
		return set.toArray(new String[set.size()]);
	}
}
