/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.choose.subtoken;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.helper.FormulaChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.ChoiceSetToken;
import pcgen.util.Logging;

public class NumberToken extends AbstractToken implements
		ChoiceSetToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "NUMBER";
	}

	public PrimitiveChoiceSet<?> parse(LoadContext context, CDOMObject obj,
			String value) throws PersistenceLayerException
	{
		int pipeLoc = value.indexOf(Constants.PIPE);

		if (pipeLoc == -1)
		{
			Logging.errorPrint(getTokenName() + " had only one argument. "
					+ "Must have three arguments, MIN=, MAX= and TITLE=: "
					+ value);
			return null;
		}

		String minString = value.substring(0, pipeLoc);
		if (!minString.startsWith("MIN="))
		{
			Logging.errorPrint(getTokenName()
					+ " first argument must start with MIN=: " + minString);
			return null;
		}
		Formula min;
		Integer minInt = null;
		try
		{
			minInt = Integer.valueOf(minString.substring(4));
			min = FormulaFactory.getFormulaFor(minInt);
		}
		catch (NumberFormatException nfe)
		{
			min = FormulaFactory.getFormulaFor(minString.substring(4));
		}

		int nextPipeLoc = value.indexOf(Constants.PIPE, pipeLoc + 1);
		if (nextPipeLoc != -1)
		{
			Logging.errorPrint(getTokenName() + " had too many arguments. "
					+ "Must have two arguments, MIN=, MAX= : " + value);
			return null;
		}
		String maxString = value.substring(pipeLoc + 1);
		if (!maxString.startsWith("MAX="))
		{
			Logging.errorPrint(getTokenName()
					+ " second argument must start with MAX=: " + maxString);
			return null;
		}

		Formula max;
		Integer maxInt = null;
		try
		{
			maxInt = Integer.valueOf(maxString.substring(4));
			max = FormulaFactory.getFormulaFor(maxInt);
		}
		catch (NumberFormatException nfe)
		{
			max = FormulaFactory.getFormulaFor(maxString.substring(4));
		}

		if (minInt != null && maxInt != null)
		{
			if (minInt.intValue() >= maxInt.intValue())
			{
				Logging.errorPrint(getTokenName() + " MAX must be > MIN");
				return null;
			}
		}
		return new FormulaChoiceSet(min, max);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
