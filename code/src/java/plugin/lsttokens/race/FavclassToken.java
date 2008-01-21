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

import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CategorizedCDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.core.SubClass;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.util.Logging;

/**
 * Class deals with FAVCLASS Token
 */
public class FavclassToken extends AbstractToken implements RaceLstToken
{
	public static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	public static final Class<SubClass> SUBCLASS_CLASS = SubClass.class;

	@Override
	public String getTokenName()
	{
		return "FAVCLASS";
	}

	public boolean parse(Race race, String value)
	{
		race.setFavoredClass(value);
		return true;
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		return parseFavoredClass(context, race, value);
	}

	public boolean parseFavoredClass(LoadContext context, CDOMObject cdo,
		String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);

		while (tok.hasMoreTokens())
		{
			CDOMReference<? extends PCClass> ref;
			String token = tok.nextToken();
			if (Constants.LST_ALL.equals(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(PCCLASS_CLASS);
			}
			else
			{
				foundOther = true;
				int dotLoc = token.indexOf('.');
				if (dotLoc == -1)
				{
					//Primitive
					ref = context.ref.getCDOMReference(PCCLASS_CLASS, token);
				}
				else
				{
					//SubClass
					String parent = token.substring(0, dotLoc);
					String subclass = token.substring(dotLoc + 1);
					SubClassCategory scc = SubClassCategory.getConstant(parent);
					ref = context.ref.getCDOMReference(SUBCLASS_CLASS, scc,
							subclass);
				}
			}
			if (ref == null)
			{
				Logging.errorPrint("  ...error encountered in "
					+ getTokenName());
				return false;
			}
			context.getObjectContext().addToList(cdo, ListKey.FAVORED_CLASS,
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

	public String[] unparse(LoadContext context, Race race)
	{
		Changes<CDOMReference<? extends PCClass>> changes =
				context.getObjectContext().getListChanges(race,
					ListKey.FAVORED_CLASS);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		SortedSet<String> set = new TreeSet<String>();
		for (CDOMReference<? extends PCClass> ref : changes.getAdded())
		{
			Class<? extends PCClass> refClass = ref.getReferenceClass();
			if (SUBCLASS_CLASS.equals(refClass))
			{
				Category<SubClass> parent = ((CategorizedCDOMReference<SubClass>) ref)
						.getCDOMCategory();
				set.add(parent.toString() + "." + ref.getLSTformat());
			}
			else 
			{
				set.add(ref.getLSTformat());
			}
		}
		return new String[] { StringUtil.join(set, Constants.COMMA) };
	}
}
