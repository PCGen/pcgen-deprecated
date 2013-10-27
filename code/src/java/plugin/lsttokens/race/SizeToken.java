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
package plugin.lsttokens.race;

import pcgen.base.formula.Resolver;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.formula.FixedSizeResolver;
import pcgen.cdom.inst.CDOMRace;
import pcgen.cdom.inst.CDOMSizeAdjustment;
import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SIZE Token
 */
public class SizeToken implements RaceLstToken, CDOMPrimaryToken<CDOMRace>
{

	public String getTokenName()
	{
		return "SIZE";
	}

	public boolean parse(Race race, String value)
	{
		race.setSize(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMRace race, String value)
	{
		CDOMSizeAdjustment size =
				context.ref.getAbbreviatedObject(CDOMSizeAdjustment.class,
					value);
		if (size == null)
		{
			Logging.errorPrint("Unable to find Size: " + value);
			return false;
		}
		context.getObjectContext().put(race, ObjectKey.SIZE,
			new FixedSizeResolver(size));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMRace race)
	{
		Resolver<CDOMSizeAdjustment> res =
				context.getObjectContext().getObject(race, ObjectKey.SIZE);
		if (res == null)
		{
			return null;
		}
		return new String[]{res.toLSTFormat()};
	}

	public Class<CDOMRace> getTokenClass()
	{
		return CDOMRace.class;
	}
}
