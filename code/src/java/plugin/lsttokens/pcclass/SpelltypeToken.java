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
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SPELLTYPE Token
 */
public class SpelltypeToken implements PCClassLstToken, CDOMPrimaryToken<CDOMPCClass>
{

	public String getTokenName()
	{
		return "SPELLTYPE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		/*
		 * CONSIDER In the future it may be useful here to check for "" or
		 * "None" and filter those out (never set the spell type) - thpr 11/9/06
		 */
		pcclass.setSpellType(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMPCClass pcc, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(pcc, ObjectKey.SPELL_TYPE,
			Type.getConstant(value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMPCClass pcc)
	{
		Type type =
				context.getObjectContext().getObject(pcc, ObjectKey.SPELL_TYPE);
		if (type == null)
		{
			return null;
		}
		return new String[]{type.toString()};
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
