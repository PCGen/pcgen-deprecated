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

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.core.Deity;
import pcgen.persistence.lst.DeityLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with DEITYWEAP Token
 */
public class DeityweapToken extends AbstractToken implements DeityLstToken,
		CDOMPrimaryToken<CDOMDeity>
{

	private static final Class<CDOMWeaponProf> WEAPONPROF_CLASS = CDOMWeaponProf.class;

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

	public boolean parse(LoadContext context, CDOMDeity deity, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<CDOMWeaponProf> ref;
			if (Constants.LST_ALL.equalsIgnoreCase(token)
					|| Constants.LST_ANY.equalsIgnoreCase(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(WEAPONPROF_CLASS);
			}
			else
			{
				foundOther = true;
				ref = context.ref.getCDOMReference(WEAPONPROF_CLASS, token);
			}
			context.getObjectContext().addToList(deity, ListKey.DEITYWEAPON,
					ref);
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMDeity deity)
	{
		Changes<CDOMReference<CDOMWeaponProf>> changes = context
				.getObjectContext().getListChanges(deity, ListKey.DEITYWEAPON);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[] { ReferenceUtilities.joinLstFormat(changes
				.getAdded(), Constants.PIPE) };
	}

	public Class<CDOMDeity> getTokenClass()
	{
		return CDOMDeity.class;
	}
}
