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

import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.util.Logging;

public class NumberToken extends AbstractToken implements ChooseLstToken
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value == null)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " requires additional arguments");
			return false;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return false;
		}
		if (hasIllegalSeparator('|', value))
		{
			return false;
		}

		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " must have two or more | delimited arguments : " + value);
			return false;
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		if (tok.countTokens() != 3)
		{
			Logging
				.errorPrint("COUNT:" + getTokenName()
					+ " requires three arguments, MIN=, MAX= and TITLE= : "
					+ value);
			return false;
		}
		if (!tok.nextToken().startsWith("MIN="))
		{
			Logging.errorPrint("COUNT:" + getTokenName()
				+ " first argument was not MIN=");
			return false;
		}
		if (!tok.nextToken().startsWith("MAX="))
		{
			Logging.errorPrint("COUNT:" + getTokenName()
				+ " second argument was not MAX=");
			return false;
		}
		if (!tok.nextToken().startsWith("TITLE="))
		{
			Logging.errorPrint("COUNT:" + getTokenName()
				+ " third argument was not TITLE=");
			return false;
		}
		StringBuilder sb = new StringBuilder();
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName()).append('|').append(value);
		po.setChoiceString(sb.toString());
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "NUMBER";
	}
}
