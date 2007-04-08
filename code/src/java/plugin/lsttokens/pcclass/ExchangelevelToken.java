/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.content.LevelExchange;
import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with EXCHANGELEVEL Token
 */
public class ExchangelevelToken implements PCClassLstToken,
		PCClassClassLstToken
{

	public String getTokenName()
	{
		return "EXCHANGELEVEL";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setLevelExchange(value);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}
		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		if (tok.countTokens() != 4)
		{
			Logging.errorPrint(getTokenName()
				+ " must have 4 | delimited arguments : " + value);
			return false;
		}

		String classString = tok.nextToken();
		CDOMSimpleSingleRef<PCClass> cl =
				context.ref.getCDOMReference(PCClass.class, classString);
		String mindlString = tok.nextToken();
		int mindl;
		try
		{
			mindl = Integer.parseInt(mindlString);
			if (mindl <= 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer > 0");
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName() + " expected an integer: "
				+ mindlString);
			return false;
		}
		String maxdlString = tok.nextToken();
		int maxdl;
		try
		{
			maxdl = Integer.parseInt(maxdlString);
			if (maxdl <= 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer > 0");
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName() + " expected an integer: "
				+ maxdlString);
			return false;
		}
		String minremString = tok.nextToken();
		int minrem;
		try
		{
			minrem = Integer.parseInt(minremString);
			if (minrem <= 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer > 0");
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName() + " expected an integer: "
				+ minremString);
			return false;
		}
		LevelExchange le = new LevelExchange(cl, mindl, maxdl, minrem);
		context.graph.linkObjectIntoGraph(getTokenName(), pcc, le);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
