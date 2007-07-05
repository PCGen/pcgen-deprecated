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
 * Current Ver: $Revision: 2700 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-04-07 23:56:55 -0400 (Sat, 07 Apr 2007) $
 */
package plugin.lstcompatibility.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassClassLstCompatibilityToken;

public class HasSubClass514Token extends AbstractToken implements
		PCClassClassLstCompatibilityToken
{

	@Override
	public String getTokenName()
	{
		return "HASSUBCLASS";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		return true;
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}
}
