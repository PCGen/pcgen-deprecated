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

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Deity;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.DeityLstToken;

/**
 * Class deals with DEITYWEAP Token
 */
public class DeityweapToken implements DeityLstToken
{

	public String getTokenName()
	{
		return "DEITYWEAP";
	}

	public boolean parse(Deity deity, String value)
	{
		deity.setFavoredWeapon(value);
		return true;
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			deity.addToListFor(ListKey.DEITY_WEAPON, context.ref
				.getCDOMReference(WeaponProf.class, tok.nextToken()));
		}
		return true;
	}

	public String unparse(LoadContext context, Deity deity)
	{
		List<CDOMSimpleSingleRef<WeaponProf>> profs =
				deity.getListFor(ListKey.DEITY_WEAPON);
		if (profs.isEmpty())
		{
			return null;
		}
		StringBuilder sb =
				new StringBuilder().append(getTokenName()).append(':');
		boolean needPipe = false;
		for (CDOMSimpleSingleRef<WeaponProf> wp : profs)
		{
			if (needPipe)
			{
				sb.append(Constants.PIPE);
			}
			sb.append(wp.getLSTformat());
		}
		return sb.toString();
	}
}
