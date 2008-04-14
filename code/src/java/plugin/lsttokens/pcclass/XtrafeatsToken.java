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
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with XTRAFEATS Token
 */
public class XtrafeatsToken implements PCClassLstToken, CDOMPrimaryToken<CDOMPCClass>
{

	/**
	 * Get Token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "XTRAFEATS";
	}

	/**
	 * Parse the XTRAFEATS token
	 * 
	 * @param pcclass
	 * @param value
	 * @param level
	 * @return true if successful else false
	 */
	public boolean parse(PCClass pcclass, String value, int level)
	{
		try
		{
			pcclass.setInitialFeats(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public boolean parse(LoadContext context, CDOMPCClass pcc, String value)
	{
		int featCount;
		try
		{
			featCount = Integer.parseInt(value);
			if (featCount <= 0)
			{
				Logging.errorPrint("Number in " + getTokenName()
					+ " must be greater than zero: " + value);
				return false;
			}
			context.obj.put(pcc, IntegerKey.START_FEATS, featCount);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid Number in " + getTokenName() + ": "
				+ value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, CDOMPCClass obj)
	{
		Integer feats = context.getObjectContext().getInteger(obj,
				IntegerKey.START_FEATS);
		if (feats == null)
		{
			return null;
		}
		if (feats.intValue() <= 0)
		{
			context
					.addWriteMessage(getTokenName()
							+ " must be an integer > 0");
			return null;
		}
		return new String[] { feats.toString() };
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
