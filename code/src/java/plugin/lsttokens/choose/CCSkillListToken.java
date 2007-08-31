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
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.ChooseCompatibilityToken;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class CCSkillListToken extends AbstractToken implements ChooseLstToken,
		ChooseCompatibilityToken
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value == null)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " requires additional arguments");
			return false;
		}
		if (value.indexOf('|') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain | : " + value);
			return false;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return false;
		}
		if (hasIllegalSeparator(',', value))
		{
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
		return "CCSKILLLIST";
	}

	public PrimitiveChoiceSet<?> parse(LoadContext context, CDOMObject obj,
		String value) throws PersistenceLayerException
	{
		String newValue = "CROSSCLASS";
		if (value != null)
		{
			if (Constants.LST_LIST.equals(value))
			{
				return ChooseLoader.parseToken(context, Skill.class, newValue);
			}
			if (value.indexOf('|') != -1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not contain | : " + value);
				return null;
			}
			if (value.indexOf('[') != -1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not contain [] : " + value);
				return null;
			}
			if (hasIllegalSeparator(',', value))
			{
				return null;
			}
			StringBuilder sb = new StringBuilder();
			sb.append(newValue);
			sb.append('[');
			StringTokenizer st = new StringTokenizer(value, Constants.COMMA);
			boolean needPipe = false;
			while (st.hasMoreTokens())
			{
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				needPipe = true;
				sb.append(st.nextToken());
			}
			sb.append(']');
			newValue = sb.toString();
		}
		return ChooseLoader.parseToken(context, Skill.class, newValue);
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
}
