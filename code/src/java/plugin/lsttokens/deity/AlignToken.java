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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Alignment;
import pcgen.core.Deity;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.DeityLstToken;
import pcgen.util.Logging;

/**
 * Class deals with ALIGN Token
 */
public class AlignToken implements DeityLstToken
{

	public String getTokenName()
	{
		return "ALIGN";
	}

	public boolean parse(Deity deity, String value)
	{
		deity.setAlignment(value);
		return true;
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		try
		{
			context.obj.put(deity, ObjectKey.ALIGNMENT, context.ref
				.getConstructedCDOMObject(Alignment.class, value));
			return true;
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint("Invalid Alignment found in " + getTokenName()
				+ ": " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		Alignment at = context.obj.getObject(deity, ObjectKey.ALIGNMENT);
		if (at == null)
		{
			return null;
		}
		return new String[]{at.toString()};
	}
}
