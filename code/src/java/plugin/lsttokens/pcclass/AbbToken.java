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

import pcgen.cdom.inst.CDOMPCClass;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ABB Token for PCC files
 */
public class AbbToken implements PCClassLstToken, CDOMPrimaryToken<CDOMPCClass>
{

	/**
	 * Return token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "ABB";
	}

	/**
	 * Parse the ABB token
	 * 
	 * @param pcclass
	 * @param value
	 * @param level
	 * @return true
	 */
	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setAbbrev(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMPCClass pcc, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.ref.registerAbbreviation(pcc, value);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMPCClass pcc)
	{
		String abb = context.ref.getAbbreviation(pcc);
		if (abb == null)
		{
			return null;
		}
		return new String[]{abb};
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
