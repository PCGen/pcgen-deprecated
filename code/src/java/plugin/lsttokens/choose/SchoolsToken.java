/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.choose;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.choiceset.SimpleCollectionChoiceSet;
import pcgen.cdom.enumeration.SpellSchool;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.ChoiceSetCompatibilityToken;
import pcgen.util.Logging;

public class SchoolsToken implements ChooseLstToken,
		ChoiceSetCompatibilityToken<CDOMObject>
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value != null)
		{
			Logging.addParseMessage(Logging.LST_WARNING, "CHOOSE:"
					+ getTokenName() + " will ignore arguments: " + value);
		}
		// No args - legal
		StringBuilder sb = new StringBuilder();
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName());
		po.setChoiceString(sb.toString());
		return true;
	}

	public String getTokenName()
	{
		return "SCHOOLS";
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public PrimitiveChoiceSet<?> parse(LoadContext context, CDOMObject cdo,
			String value) throws PersistenceLayerException
	{
		return new SimpleCollectionChoiceSet<SpellSchool>(SpellSchool
				.getAllConstants());
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
