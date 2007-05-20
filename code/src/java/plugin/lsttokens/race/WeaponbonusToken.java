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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.Race;
import pcgen.core.WeaponProf;
import pcgen.core.WeaponProfList;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with WEAPONBONUS Token
 */
public class WeaponbonusToken extends AbstractToken implements RaceLstToken
{
	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

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

	public boolean parse(LoadContext context, Race race, String value)
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

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		boolean foundAny = false;
		boolean foundOther = false;
		List<CDOMReference<WeaponProf>> list =
				new ArrayList<CDOMReference<WeaponProf>>();

		while (tok.hasMoreTokens())
		{
			/*
			 * Note this HAS to be done one-by-one, because the
			 * .clearChildNodeOfClass method above does NOT recognize the C/CC
			 * Skill object and therefore doesn't know how to search the
			 * sublists
			 */
			String tokText = tok.nextToken();
			if (Constants.LST_ALL.equals(tokText))
			{
				foundAny = true;
				CDOMReference<WeaponProf> ref =
						context.ref.getCDOMAllReference(WEAPONPROF_CLASS);
				list.add(ref);
			}
			else
			{
				foundOther = true;
				CDOMReference<WeaponProf> ref =
						TokenUtilities.getTypeOrPrimitive(context,
							WEAPONPROF_CLASS, tokText);
				if (ref == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
						+ getTokenName());
					return false;
				}
				list.add(ref);
			}
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		CDOMReference<WeaponProfList> swl =
				context.ref.getCDOMReference(WEAPONPROFLIST_CLASS, "*Starting");
		for (CDOMReference<WeaponProf> prof : list)
		{
			context.list.addToList(getTokenName(), obj, swl, prof);
		}
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		CDOMReference<WeaponProfList> swl =
				context.ref.getCDOMReference(WEAPONPROFLIST_CLASS, "*Starting");
		GraphChanges<WeaponProf> changes =
				context.list.getChangesInList(getTokenName(), race, swl);
		if (changes == null)
		{
			// Legal if no WEAPONBONUS was present in the race
			return null;
		}
		List<String> list = new ArrayList<String>();
		if (changes.hasRemovedItems())
		{
			if (changes.includesGlobalClear())
			{
				context.addWriteMessage("Non-sensical relationship in "
					+ getTokenName()
					+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			list.add(Constants.LST_DOT_CLEAR_DOT
				+ ReferenceUtilities.joinLstFormat(changes.getRemoved(),
					"|.CLEAR."));
		}
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		if (changes.hasAddedItems())
		{
			list.add(ReferenceUtilities.joinLstFormat(changes.getAdded(),
				Constants.PIPE));
		}
		return list.toArray(new String[list.size()]);
	}
}
