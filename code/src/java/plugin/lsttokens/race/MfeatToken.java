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

import pcgen.core.Race;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with MFEAT Token
 */
public class MfeatToken implements RaceLstToken
{

	public String getTokenName()
	{
		return "MFEAT";
	}

	public boolean parse(Race race, String value)
	{
		race.setMFeatList(value);
		return true;
	}

	public boolean parse(LoadContext context, Race race, String value)
		throws PersistenceLayerException
	{
		// This is a HACK to patch the monster stuff in 5.11
		return true;
	}

	public String unparse(LoadContext context, Race race)
	{
		// Well, so this is a hack too :)
		return null;
	}
}
