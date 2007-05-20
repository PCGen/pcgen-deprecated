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

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with MONSKILL Token
 */
public class MonskillToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "MONSKILL";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass
			.addBonusList("0|MONSKILLPTS|NUMBER|" + value + "|PRELEVELMAX:1");
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		try
		{
			Integer in = Integer.valueOf(value);
			if (in.intValue() <= 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer > 0");
				return false;
			}
			context.obj.put(pcc, IntegerKey.MONSTER_SKILL_POINTS, in);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " expected an integer.  Tag must be of the form: "
				+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Integer msp = context.obj.getInteger(pcc, IntegerKey.MONSTER_SKILL_POINTS);
		if (msp == null)
		{
			return null;
		}
		if (msp.intValue() <= 0)
		{
			context
				.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[]{msp.toString()};
	}
}
