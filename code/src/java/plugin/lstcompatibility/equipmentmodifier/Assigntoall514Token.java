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
 * Current Ver: $Revision: 2959 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-05-20 00:02:34 -0400 (Sun, 20 May 2007) $
 */
package plugin.lstcompatibility.equipmentmodifier;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMEqMod;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;

/**
 * Deals with ASSIGNTOALL token
 */
public class Assigntoall514Token implements CDOMCompatibilityToken<CDOMEqMod>
{

	public String getTokenName()
	{
		return "ASSIGNTOALL";
	}

	public boolean parse(LoadContext context, CDOMEqMod mod, String value)
	{
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			set = Boolean.TRUE;
		}
		else
		{
			set = Boolean.FALSE;
		}
		context.getObjectContext().put(mod, ObjectKey.ASSIGN_TO_ALL, set);
		return true;
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public Class<CDOMEqMod> getTokenClass()
	{
		return CDOMEqMod.class;
	}
}
