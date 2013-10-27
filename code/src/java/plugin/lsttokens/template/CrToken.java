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

import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with CR Token
 */
public class CrToken implements PCTemplateLstToken,
		CDOMPrimaryToken<CDOMTemplate>
{

	public String getTokenName()
	{
		return "CR";
	}

	public boolean parse(PCTemplate template, String value)
	{
		try
		{
			template.setCR(Integer.parseInt(value));
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMTemplate template,
			String value)
	{
		try
		{
			ChallengeRating cr = new ChallengeRating(value);
			context.getObjectContext().put(template,
					ObjectKey.CHALLENGE_RATING, cr);
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Invalid " + getTokenName() + ": "
					+ iae.getLocalizedMessage());
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMTemplate pct)
	{
		ChallengeRating cr = context.getObjectContext().getObject(pct,
				ObjectKey.CHALLENGE_RATING);
		if (cr == null)
		{
			return null;
		}
		return new String[] { cr.getLSTformat() };
	}

	public Class<CDOMTemplate> getTokenClass()
	{
		return CDOMTemplate.class;
	}
}
