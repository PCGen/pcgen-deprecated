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
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.ChoiceSetCompatibilityToken;
import pcgen.util.Logging;

public class SpellsToken extends AbstractToken implements ChooseLstToken,
		ChoiceSetCompatibilityToken<CDOMObject>
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

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			int equalsLoc = tokText.indexOf("=");
			if (equalsLoc == tokText.length() - 1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " arguments must have value after = : " + tokText);
				Logging.errorPrint("  entire token was: " + value);
				return false;
			}
			if (!tokText.startsWith("CLASS=") && !tokText.startsWith("DOMAIN="))
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " argument must start with CLASS= or DOMAIN= : "
						+ tokText);
				Logging.errorPrint("  Entire Token was: " + value);
				return false;
			}
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
		return "SPELLS";
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
		if (hasIllegalSeparator('|', value))
		{
			return null;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		StringBuilder sb = new StringBuilder();
		boolean needPipe = false;
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			int equalsLoc = tokText.indexOf("=");
			if (equalsLoc == tokText.length() - 1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " arguments must have value after = : " + tokText);
				Logging.errorPrint("  entire token was: " + value);
				return null;
			}
			if (needPipe)
			{
				sb.append(Constants.PIPE);
			}
			if (tokText.startsWith("CLASS="))
			{
				sb.append("CLASSLIST=").append(tokText.substring(6));
			}
			else if (tokText.startsWith("DOMAIN="))
			{
				sb.append("DOMAINLIST=").append(tokText.substring(7));
			}
			else
			{
				sb.append(tokText);
			}
			needPipe = true;
		}
		return context.getChoiceSet(CDOMSpell.class, sb.toString());
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
