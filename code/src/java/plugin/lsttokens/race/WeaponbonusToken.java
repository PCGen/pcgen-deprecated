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

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.inst.CDOMRace;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.cdom.list.WeaponProfList;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with WEAPONBONUS Token
 */
public class WeaponbonusToken extends AbstractToken implements RaceLstToken, CDOMPrimaryToken<CDOMRace>
{
	private static final Class<CDOMWeaponProf> WEAPONPROF_CLASS = CDOMWeaponProf.class;

	private static final Class<WeaponProfList> WEAPONPROFLIST_CLASS =
			WeaponProfList.class;

	@Override
	public String getTokenName()
	{
		return "WEAPONBONUS"; //$NON-NLS-1$
	}

	public boolean parse(Race race, String value)
	{
		final StringTokenizer aTok =
				new StringTokenizer(value, Constants.PIPE, false);

		while (aTok.hasMoreTokens())
		{
			race.addWeaponProfBonus(aTok.nextToken());
		}

		return true;
	}

	public boolean parse(LoadContext context, CDOMRace race, String value)
	{
		return parseWeaponBonus(context, race, value);
	}

	public boolean parseWeaponBonus(LoadContext context, CDOMObject obj,
		String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		CDOMReference<WeaponProfList> swl =
				context.ref.getCDOMReference(WEAPONPROFLIST_CLASS, "*Starting");

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			CDOMReference<CDOMWeaponProf> ref;
			if (Constants.LST_ALL.equals(tokText))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(WEAPONPROF_CLASS);
				context.getListContext().addToList(getTokenName(), obj, swl,
					ref);
			}
			else
			{
				foundOther = true;
				ref =
						TokenUtilities.getTypeOrPrimitive(context,
							WEAPONPROF_CLASS, tokText);
				if (ref == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
						+ getTokenName());
					return false;
				}
			}
			context.getListContext().addToList(getTokenName(), obj, swl, ref);
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMRace race)
	{
		CDOMReference<WeaponProfList> swl =
				context.ref.getCDOMReference(WEAPONPROFLIST_CLASS, "*Starting");
		AssociatedChanges<CDOMReference<CDOMWeaponProf>> changes =
				context.getListContext().getChangesInList(getTokenName(), race,
					swl);
		Collection<CDOMReference<CDOMWeaponProf>> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no add
			return null;
		}
		return new String[]{ReferenceUtilities.joinLstFormat(added,
			Constants.PIPE)};
	}

	public Class<CDOMRace> getTokenClass()
	{
		return CDOMRace.class;
	}
}
