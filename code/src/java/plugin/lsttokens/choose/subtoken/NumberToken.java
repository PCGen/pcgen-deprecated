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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.helper.NumberChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.ChooseCDOMLstToken;
import pcgen.util.Logging;

public class NumberToken extends AbstractToken implements ChooseCDOMLstToken
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
				+ "Must have three arguments, MIN=, MAX= and TITLE=: " + value);
			return null;
		}

		String minString = value.substring(0, pipeLoc);
		if (!minString.startsWith("MIN="))
		{
			Logging.errorPrint(getTokenName()
				+ " first argument must start with MIN=: " + minString);
			return null;
		}
		int min;
		try
		{
			min = Integer.parseInt(minString.substring(4));
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " MIN must be an integer >= 0: " + minString);
			return null;
		}

		int nextPipeLoc = value.indexOf(Constants.PIPE, pipeLoc + 1);
		if (nextPipeLoc == -1)
		{
			Logging.errorPrint(getTokenName() + " had only two arguments. "
				+ "Must have three arguments, MIN=, MAX= and TITLE=: " + value);
			return null;
		}
		if (nextPipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.errorPrint(getTokenName() + " had too many arguments. "
				+ "Must have three arguments, MIN=, MAX= and TITLE=: " + value);
			return null;
		}
		String maxString = value.substring(pipeLoc + 1, nextPipeLoc);
		if (!maxString.startsWith("MAX="))
		{
			Logging.errorPrint(getTokenName()
				+ " second argument must start with MAX=: " + maxString);
			return null;
		}
		int max;
		try
		{
			max = Integer.parseInt(maxString.substring(4));
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " MAX must be an integer >= 0: " + maxString);
			return null;
		}

		if (max <= min)
		{
			Logging.errorPrint(getTokenName() + " MAX must be > MIN");
			return null;
		}

		String titleString = value.substring(nextPipeLoc + 1);
		if (!titleString.startsWith("TITLE="))
		{
			Logging.errorPrint(getTokenName()
				+ " third argument must start with TITLE=");
			return null;
		}
		String title = titleString.substring(6);
		if (title == null)
		{
			Logging.errorPrint(getTokenName() + " TITLE cannot be empty");
			return null;
		}
		return new NumberChoiceSet(min, max, title);
	}
}
