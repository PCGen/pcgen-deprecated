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

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.core.Deity;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.lst.DeityLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with PANTHEON Token
 */
public class PantheonToken extends AbstractToken implements DeityLstToken, CDOMPrimaryToken<CDOMDeity>
{

	@Override
	public String getTokenName()
	{
		return "PANTHEON";
	}

	public boolean parse(Deity deity, String value)
	{
		if (value.length() > 0)
		{
			String[] pantheons = value.split("\\|");
			List<String> pantheonList = CoreUtility.arrayToList(pantheons);
			deity.setPantheonList(pantheonList);
			return true;
		}
		return false;
	}

	public boolean parse(LoadContext context, CDOMDeity deity, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			context.getObjectContext().addToList(deity, ListKey.PANTHEON,
				Pantheon.getConstant(tok.nextToken()));
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMDeity deity)
	{
		Changes<Pantheon> changes =
				context.getObjectContext().getListChanges(deity,
					ListKey.PANTHEON);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[]{StringUtil.join(changes.getAdded(), Constants.PIPE)};
	}

	public Class<CDOMDeity> getTokenClass()
	{
		return CDOMDeity.class;
	}
}
