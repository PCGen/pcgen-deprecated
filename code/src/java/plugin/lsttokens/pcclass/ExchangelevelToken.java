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

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.content.LevelExchange;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with EXCHANGELEVEL Token
 */
public class ExchangelevelToken extends AbstractToken implements
		PCClassLstToken, CDOMPrimaryToken<CDOMPCClass>
{

	@Override
	public String getTokenName()
	{
		return "EXCHANGELEVEL";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setLevelExchange(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMPCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
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
		CDOMSingleRef<CDOMPCClass> cl = context.ref.getCDOMReference(
				CDOMPCClass.class, classString);
		String mindlString = tok.nextToken();
		int mindl;
		try
		{
			mindl = Integer.parseInt(mindlString);
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
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName() + " expected an integer: "
					+ minremString);
			return false;
		}
		try
		{
			LevelExchange le = new LevelExchange(cl, mindl, maxdl, minrem);
			context.getObjectContext().give(getTokenName(), pcc, le);
			return true;
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint("Error in " + getTokenName() + " "
					+ e.getMessage());
			Logging.errorPrint("  Token contents: " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, CDOMPCClass pcc)
	{
		Changes<LevelExchange> changes = context.getObjectContext()
				.getGivenChanges(getTokenName(), pcc, LevelExchange.class);
		Collection<LevelExchange> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token present
			return null;
		}
		if (added.size() > 1)
		{
			context
					.addWriteMessage("Only 1 LevelExchange is allowed per Class");
			return null;
		}
		LevelExchange le = added.iterator().next();
		StringBuilder sb = new StringBuilder();
		sb.append(le.getLSTformat()).append(Constants.PIPE);
		sb.append(le.getMinDonatingLevel()).append(Constants.PIPE);
		sb.append(le.getMaxDonatedLevels()).append(Constants.PIPE);
		sb.append(le.getDonatingLowerLevelBound());
		return new String[] { sb.toString() };
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
