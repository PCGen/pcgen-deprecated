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

import pcgen.base.util.Logging;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.enumeration.DefaultTriState;

/**
 * Class deals with XPPENALTY Token
 */
public class XppenaltyToken implements PCClassLstToken, PCClassClassLstToken
{

	/**
	 * Get token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "XPPENALTY";
	}

	/**
	 * Parse XPPENALTY token
	 * 
	 * @param pcclass
	 * @param value
	 * @param level
	 * @return true
	 */
	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setXPPenalty(DefaultTriState.valueOf(value));
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		try
		{
			context.obj.put(pcc, ObjectKey.XP_PENALTY, DefaultTriState
				.valueOf(value));
			return true;
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint("Illegal Value encountered in " + getTokenName()
				+ ": " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		DefaultTriState xpp = context.obj.getObject(pcc, ObjectKey.XP_PENALTY);
		if (xpp == null)
		{
			return null;
		}
		return new String[]{xpp.toString()};
	}
}
