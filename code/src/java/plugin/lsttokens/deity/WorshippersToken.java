/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.deity;

import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.core.Deity;
import pcgen.persistence.lst.DeityLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with WORSHIPPERS Token
 */
public class WorshippersToken implements DeityLstToken, CDOMPrimaryToken<CDOMDeity>
{

	/**
	 * Get token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "WORSHIPPERS";
	}

	/**
	 * Parse WORSHIPPERS token
	 * 
	 * @param deity
	 * @param value
	 * @return true
	 */
	public boolean parse(Deity deity, String value)
	{
		deity.setWorshippers(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMDeity deity, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(deity, StringKey.WORSHIPPERS, value);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMDeity deity)
	{
		String worshippers =
				context.getObjectContext().getString(deity,
					StringKey.WORSHIPPERS);
		if (worshippers == null)
		{
			return null;
		}
		return new String[]{worshippers};
	}

	public Class<CDOMDeity> getTokenClass()
	{
		return CDOMDeity.class;
	}
}
