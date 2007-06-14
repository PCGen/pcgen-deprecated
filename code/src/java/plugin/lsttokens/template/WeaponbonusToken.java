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
package plugin.lsttokens.template;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.PCTemplate;
import pcgen.core.WeaponProf;
import pcgen.core.WeaponProfList;
import pcgen.persistence.ListGraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with WEAPONBONUS Token
 */
public class WeaponbonusToken extends AbstractToken implements
		PCTemplateLstToken
{

	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

	private static final Class<WeaponProfList> WEAPONPROFLIST_CLASS =
			WeaponProfList.class;

	@Override
	public String getTokenName()
	{
		return "WEAPONBONUS";
	}

	public boolean parse(PCTemplate template, String value)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		while (aTok.hasMoreTokens())
		{
			template.addWeaponProfBonus(aTok.nextToken());
		}

		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		return parseWeaponBonus(context, template, value);
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
		CDOMReference<WeaponProfList> swl =
				context.ref.getCDOMReference(WEAPONPROFLIST_CLASS, "*Starting");

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_ALL.equals(tokText))
			{
				foundAny = true;
				CDOMReference<WeaponProf> ref =
						context.ref.getCDOMAllReference(WEAPONPROF_CLASS);
				context.list.addToList(getTokenName(), obj, swl, ref);
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
				context.list.addToList(getTokenName(), obj, swl, ref);
			}
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		CDOMReference<WeaponProfList> swl =
				context.ref.getCDOMReference(WEAPONPROFLIST_CLASS, "*Starting");
		ListGraphChanges<WeaponProf> changes =
				context.list.getChangesInList(getTokenName(), pct, swl);
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
