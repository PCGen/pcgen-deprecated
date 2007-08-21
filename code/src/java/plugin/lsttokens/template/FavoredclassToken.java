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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with FAVOREDCLASS Token
 */
public class FavoredclassToken extends AbstractToken implements
		PCTemplateLstToken
{
	public static final Class<PCClass> PCCLASS_CLASS = PCClass.class;

	@Override
	public String getTokenName()
	{
		return "FAVOREDCLASS";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.setFavoredClass(value);
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		return parseFavoredClass(context, template, value);
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
			CDOMReference<PCClass> ref;
			String token = tok.nextToken();
			if (Constants.LST_ALL.equals(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(PCCLASS_CLASS);
			}
			else
			{
				foundOther = true;
				ref =
						TokenUtilities.getTypeOrPrimitive(context,
							PCCLASS_CLASS, token);
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

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Changes<CDOMReference<PCClass>> changes =
				context.getObjectContext().getListChanges(pct,
					ListKey.FAVORED_CLASS);
		if (changes == null)
		{
			return null;
		}
		Collection<CDOMReference<PCClass>> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			return null;
		}
		return new String[]{ReferenceUtilities.joinLstFormat(added,
			Constants.COMMA)};
	}
}
