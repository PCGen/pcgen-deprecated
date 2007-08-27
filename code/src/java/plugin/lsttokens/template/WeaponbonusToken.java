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

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.core.PCTemplate;
import pcgen.core.WeaponProf;
import pcgen.core.WeaponProfList;
import pcgen.persistence.AssociatedChanges;
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
				context.getListContext().addToList(getTokenName(), template,
					swl, ref);
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
				context.getListContext().addToList(getTokenName(), template,
					swl, ref);
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
		AssociatedChanges<CDOMReference<WeaponProf>> changes =
				context.getListContext().getChangesInList(getTokenName(), pct,
					swl);
		if (changes == null)
		{
			// Legal if no WEAPONBONUS was present in the race
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added.isEmpty())
		{
			// Zero indicates no add
			return null;
		}
		return new String[]{ReferenceUtilities.joinLstFormat(added,
			Constants.PIPE)};
	}
}
