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

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with EXCLASS Token
 */
public class ExclassToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "EXCLASS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setExClass(value);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		CDOMReference<PCClass> cl =
				context.ref.getCDOMReference(PCClass.class, value);
		context.obj.put(pcc, ObjectKey.EX_CLASS, cl);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		CDOMReference<PCClass> cl =
				context.obj.getObject(pcc, ObjectKey.EX_CLASS);
		if (cl == null)
		{
			return null;
		}
		return new String[]{cl.getLSTformat()};
	}
}
