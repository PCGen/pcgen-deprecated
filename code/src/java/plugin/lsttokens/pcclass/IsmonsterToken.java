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
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ISMONSTER Token
 */
public class IsmonsterToken implements PCClassLstToken, CDOMPrimaryToken<CDOMPCClass>
{

	private static final Class<ClassSkillList> SKILLLIST_CLASS =
			ClassSkillList.class;

	public String getTokenName()
	{
		return "ISMONSTER";
	}

	public boolean parse(final PCClass pcclass, final String value,
		final int level)
	{
		if (value.startsWith("Y")) //$NON-NLS-1$
		{
			pcclass.setMonsterFlag(true);
		}
		else if (value.startsWith("N")) //$NON-NLS-1$
		{
			pcclass.setMonsterFlag(false);
		}
		else
		{
			Logging.errorPrint("Unknown option " + value + " in "
				+ getTokenName());
		}
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
			CDOMReference<ClassSkillList> msl =
					context.ref.getCDOMReference(SKILLLIST_CLASS, "*Monster");
			context.getGraphContext().grant(getTokenName(), pcc, msl);
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
				{
					Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
					return false;
				}
			}
			set = Boolean.FALSE;
		}
		context.getObjectContext().put(pcc, ObjectKey.IS_MONSTER, set);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMPCClass pcc)
	{
		Boolean isM =
				context.getObjectContext().getObject(pcc, ObjectKey.IS_MONSTER);
		if (isM == null)
		{
			return null;
		}
		return new String[]{isM.booleanValue() ? "YES" : "NO"};
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
