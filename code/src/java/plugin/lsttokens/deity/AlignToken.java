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
import pcgen.cdom.inst.CDOMAlignment;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.core.Deity;
import pcgen.persistence.lst.DeityLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ALIGN Token
 */
public class AlignToken implements DeityLstToken, CDOMPrimaryToken<CDOMDeity>
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

	public boolean parse(LoadContext context, CDOMDeity deity, String value)
	{
		CDOMAlignment al =
				context.ref.getAbbreviatedObject(CDOMAlignment.class, value);
		if (al == null)
		{
			Logging.errorPrint("In " + getTokenName() + " " + value
				+ " is not an Alignment");
			return false;
		}
		context.getObjectContext().put(deity, ObjectKey.ALIGNMENT, al);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMDeity deity)
	{
		CDOMAlignment at =
				context.getObjectContext()
					.getObject(deity, ObjectKey.ALIGNMENT);
		if (at == null)
		{
			return null;
		}
		return new String[]{at.getLSTformat()};
	}

	public Class<CDOMDeity> getTokenClass()
	{
		return CDOMDeity.class;
	}
}
