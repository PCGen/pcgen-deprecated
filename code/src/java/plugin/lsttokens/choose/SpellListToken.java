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

import pcgen.core.PObject;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.util.Logging;

public class SpellListToken extends AbstractToken implements ChooseLstToken
{

	/*
	 * Compatibility is in plugin.lstcompatibility.global
	 */

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value == null)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " requires additional arguments in " + po.getDisplayName()
				+ " at " + po.getDefaultSourceString());
			return false;
		}
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value + " in "
				+ po.getDisplayName() + " at " + po.getDefaultSourceString());
			return false;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value + " in "
				+ po.getDisplayName() + " at " + po.getDefaultSourceString());
			return false;
		}
		if (hasIllegalSeparator('|', value))
		{
			return false;
		}
		if (!value.equals("Y") && !value.equals("N") && !value.equals("1")
			&& !value.equals("0"))
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " argument was not Y or N in " + po.getDisplayName() + " at "
				+ po.getDefaultSourceString());
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
		return "SPELLLIST";
	}
}
