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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with MODTOSKILLS Token
 */
public class ModtoskillsToken implements PCClassLstToken, CDOMPrimaryToken<CDOMPCClass>
{

	public String getTokenName()
	{
		return "MODTOSKILLS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setModToSkills(!"No".equalsIgnoreCase(value));
		return true;
	}

	public boolean parse(LoadContext context, CDOMPCClass pcc, String value)
	{
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' as the "
					+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.FALSE;
		}
		context.getObjectContext().put(pcc, ObjectKey.MOD_TO_SKILLS, set);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMPCClass pcc)
	{
		Boolean mts =
				context.getObjectContext().getObject(pcc,
					ObjectKey.MOD_TO_SKILLS);
		if (mts == null)
		{
			return null;
		}
		return new String[]{mts.booleanValue() ? "YES" : "NO"};
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
