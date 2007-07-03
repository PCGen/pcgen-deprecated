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

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Deity;
import pcgen.core.WeaponProf;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.DeityLstToken;

/**
 * Class deals with DEITYWEAP Token
 */
public class DeityweapToken extends AbstractToken implements DeityLstToken
{

	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

	@Override
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
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			context.obj.addToList(deity, ListKey.DEITYWEAPON, context.ref
				.getCDOMReference(WEAPONPROF_CLASS, tok.nextToken()));
		}
		return true;
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		Changes<CDOMSimpleSingleRef<WeaponProf>> changes =
				context.obj.getListChanges(deity, ListKey.DEITYWEAPON);
		if (changes == null)
		{
			return null;
		}
		return new String[]{ReferenceUtilities.joinLstFormat(
			changes.getAdded(), Constants.PIPE)};
	}
}
