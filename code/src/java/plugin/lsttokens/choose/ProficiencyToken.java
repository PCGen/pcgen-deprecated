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
import pcgen.cdom.inst.CDOMArmorProf;
import pcgen.cdom.inst.CDOMShieldProf;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.ChoiceSetCompatibilityToken;
import pcgen.util.Logging;

public class ProficiencyToken extends AbstractToken implements ChooseLstToken,
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

		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			Logging
					.errorPrint("CHOOSE:" + getTokenName()
							+ " must have two or more | delimited arguments : "
							+ value);
			return false;
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		if (tok.countTokens() < 3)
		{
			Logging.errorPrint("COUNT:" + getTokenName()
					+ " requires at least three arguments: " + value);
			return false;
		}
		String first = tok.nextToken();
		if (!first.equals("ARMOR") && !first.equals("SHIELD")
				&& !first.equals("WEAPON"))
		{
			Logging.errorPrint("COUNT:" + getTokenName()
					+ " first argument was not ARMOR, SHIELD, or WEAPON");
			return false;
		}
		String second = tok.nextToken();
		if (!second.equals("PC") && !second.equals("ALL")
				&& !second.equals("UNIQUE"))
		{
			Logging.errorPrint("COUNT:" + getTokenName()
					+ " second argument was not PC, ALL, or UNIQUE");
			return false;
		}
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			int equalsLoc = tokString.indexOf("=");
			if (equalsLoc == tokString.length() - 1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " arguments must have value after = : " + tokString);
				Logging.errorPrint("  entire token was: " + value);
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
		return "PROFICIENCY";
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

		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			Logging
					.errorPrint("CHOOSE:" + getTokenName()
							+ " must have two or more | delimited arguments : "
							+ value);
			return null;
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		if (tok.countTokens() < 3)
		{
			Logging.errorPrint("COUNT:" + getTokenName()
					+ " requires at least three arguments: " + value);
			return null;
		}
		String first = tok.nextToken();
		if (first.equals("ARMOR"))
		{
			return subParse(context, CDOMArmorProf.class, tok);
		}
		else if (first.equals("SHIELD"))
		{
			return subParse(context, CDOMShieldProf.class, tok);
		}
		else if (first.equals("WEAPON"))
		{
			return subParse(context, CDOMWeaponProf.class, tok);
		}
		else
		{
			Logging.errorPrint("COUNT:" + getTokenName()
					+ " first argument was not ARMOR, SHIELD, or WEAPON");
			return null;
		}
	}

	private <T extends CDOMObject> PrimitiveChoiceSet<?> subParse(
			LoadContext context, Class<T> name, StringTokenizer tok)
	{
		String second = tok.nextToken();
		if (!second.equals("PC") && !second.equals("ALL")
				&& !second.equals("UNIQUE"))
		{
			Logging.errorPrint("COUNT:" + getTokenName()
					+ " second argument was not PC, ALL, or UNIQUE");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(second);
		sb.append('[');
		boolean needsPipe = false;
		while (tok.hasMoreTokens())
		{
			if (needsPipe)
			{
				sb.append(Constants.PIPE);
			}
			sb.append(tok.nextToken());
		}
		sb.append(']');
		return context.getChoiceSet(name, sb.toString());
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
