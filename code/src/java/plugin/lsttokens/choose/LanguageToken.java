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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.helper.CompoundOrChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.core.Constants;
import pcgen.core.Language;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.ChooseCompatibilityToken;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class LanguageToken extends AbstractToken implements ChooseLstToken,
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
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value);
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
		StringBuilder sb = new StringBuilder();
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName()).append('(');
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		boolean first = true;
		while (st.hasMoreTokens())
		{
			if (!first)
			{
				sb.append(',');
			}
			first = false;
			String tokString = st.nextToken();
			if (tokString.indexOf('.') != tokString.lastIndexOf('.'))
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments cannot have two . : " + tokString);
				Logging.errorPrint("  format for argument must be X or X.Y");
				Logging.errorPrint("  entire token was: " + value);
				return false;
			}
			sb.append(tokString);
		}
		sb.append(')');

		po.setChoiceString(sb.toString());
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "LANGUAGE";
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
		if (value == null)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " requires additional arguments");
			return null;
		}
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value);
			return null;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return null;
		}
		if (hasIllegalSeparator('|', value))
		{
			return null;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		List<PrimitiveChoiceSet<Language>> pcsList =
				new ArrayList<PrimitiveChoiceSet<Language>>();
		while (st.hasMoreTokens())
		{
			String tokString = st.nextToken();
			if (hasIllegalSeparator('.', tokString))
			{
				return null;
			}
			int dotLoc = tokString.indexOf('.');
			if (dotLoc != tokString.lastIndexOf('.'))
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments cannot have two . : " + tokString);
				Logging.errorPrint("  format for argument must be X or X.Y");
				Logging.errorPrint("  entire token was: " + value);
				return null;
			}
			if (dotLoc == -1)
			{
				pcsList.add(ChooseLoader.parseToken(context, Language.class,
					"TYPE=" + value));
			}
			else
			{
				// type = tokString.substring(0, dotLoc);
				// language = tokString.substring(dotLoc + 1);
				// TODO Temporary barf
				return null;
			}
		}
		if (pcsList.size() == 1)
		{
			return pcsList.get(0);
		}
		else
		{
			return new CompoundOrChoiceSet<Language>(pcsList);
		}
	}
}
