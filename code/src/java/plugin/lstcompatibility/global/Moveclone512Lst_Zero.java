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
 * Current Ver: $Revision: 3939 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-08-29 19:03:17 -0400 (Wed, 29 Aug 2007) $
 */
package plugin.lstcompatibility.global;

import java.util.StringTokenizer;

import pcgen.base.formula.AddingFormula;
import pcgen.base.formula.DividingFormula;
import pcgen.base.formula.MultiplyingFormula;
import pcgen.base.formula.ReferenceFormula;
import pcgen.base.formula.SubtractingFormula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.constructor.MovementFormulaConstructor;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class Moveclone512Lst_Zero extends AbstractToken implements
		CDOMCompatibilityToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "MOVECLONE";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}
		StringTokenizer moves = new StringTokenizer(value, Constants.COMMA);

		if (moves.countTokens() != 4)
		{
			return false;
		}

		String oldType = moves.nextToken();
		String oldMod = moves.nextToken();
		if (!"0".equals(oldMod))
		{
			return false;
		}
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
			// Zero is legal here, it just copies
			if (sub < 0)
			{
				form = new SubtractingFormula(-sub);
			}
			else
			{
				form = new AddingFormula(sub);
			}
		}

		MovementFormulaConstructor fc = new MovementFormulaConstructor(oldType,
				newType, form);

		context.getObjectContext().give(getTokenName(), obj, fc);
		return true;
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
