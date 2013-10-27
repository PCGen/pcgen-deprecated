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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with RACETYPE Token
 */
public class RacetypeToken extends AbstractToken implements PCTemplateLstToken, CDOMPrimaryToken<CDOMTemplate>
{

	@Override
	public String getTokenName()
	{
		return "RACETYPE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.setRaceType(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMTemplate template, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext().put(template, ObjectKey.RACETYPE,
			RaceType.getConstant(value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMTemplate pct)
	{
		RaceType raceType =
				context.getObjectContext().getObject(pct, ObjectKey.RACETYPE);
		if (raceType == null)
		{
			return null;
		}
		return new String[]{raceType.toString()};
	}

	public Class<CDOMTemplate> getTokenClass()
	{
		return CDOMTemplate.class;
	}
}
