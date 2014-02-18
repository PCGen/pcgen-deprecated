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

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * New chooser plugin, handles numbers.
 */
public class NumberToken implements CDOMSecondaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "NUMBER";
	}

	@Override
	public String getParentToken()
	{
		return "CHOOSE";
	}

	@Override
	public ParseResult parseToken(LoadContext context, CDOMObject obj,
		String value)
	{
		if (value == null)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " requires additional arguments", context);
		}
		if (value.indexOf('[') != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments may not contain [] : " + value, context);
		}
		if (value.charAt(0) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments may not start with | : " + value, context);
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments may not end with | : " + value, context);
		}
		if (value.indexOf("||") != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments uses double separator || : " + value, context);
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
							+ " must have two or more | delimited arguments : "
							+ value, context);
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		if (tok.countTokens() != 3)
		{
			return new ParseResult.Fail("COUNT:" + getTokenName()
					+ " requires three arguments, MIN=, MAX= and TITLE= : "
					+ value, context);
		}
		if (!tok.nextToken().startsWith("MIN="))
		{
			return new ParseResult.Fail("COUNT:" + getTokenName()
					+ " first argument was not MIN=", context);
		}
		if (!tok.nextToken().startsWith("MAX="))
		{
			return new ParseResult.Fail("COUNT:" + getTokenName()
					+ " second argument was not MAX=", context);
		}
		if (!tok.nextToken().startsWith("TITLE="))
		{
			return new ParseResult.Fail("COUNT:" + getTokenName()
					+ " third argument was not TITLE=", context);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName()).append('|').append(value);
		context.obj.put(obj, StringKey.CHOICE_STRING, sb.toString());
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		String chooseString = context.getObjectContext().getString(cdo,
				StringKey.CHOICE_STRING);
		if (chooseString == null
			|| chooseString.indexOf(getTokenName() + '|') != 0
			|| cdo instanceof EquipmentModifier)
		{
			return null;
		}
		return new String[] { chooseString
				.substring(getTokenName().length() + 1) };
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
