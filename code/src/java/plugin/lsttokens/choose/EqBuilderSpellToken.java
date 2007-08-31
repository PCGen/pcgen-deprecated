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
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseCompatibilityToken;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class EqBuilderSpellToken implements ChooseLstToken,
		ChooseCompatibilityToken
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value == null)
		{
			po.setChoiceString(getTokenName());
			return true;
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
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator || : " + value);
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
			Logging.errorPrint("COUNT:" + getTokenName()
				+ " requires three arguments: " + value);
			return false;
		}
		tok.nextToken();
		String second = tok.nextToken();
		try
		{
			Integer.parseInt(second);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " second argument must be an Integer : " + value);
			return false;
		}
		String third = tok.nextToken();
		if (!third.equals("MAXLEVEL"))
		{
			try
			{
				Integer.parseInt(third);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " third argument must be an Integer or 'MAXLEVEL': "
					+ value);
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

	public String getTokenName()
	{
		return "EQBUILDER.SPELL";
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
			return ChooseLoader.parseToken(context, Spell.class, "ALL");
		}
		else
		{
			StringTokenizer st = new StringTokenizer(value, Constants.PIPE);

			Type spellType = Type.getConstant(st.nextToken());
			boolean anyType =
					spellType.equals(Type.getConstant("ANY"))
						|| spellType.equals(Type.getConstant("ANY"));

			Integer minimumLevel;
			Integer maxLevel;
			// List<String> subTypeList = new ArrayList<String>();
			while (st.hasMoreTokens())
			{
				String aString = st.nextToken();

				try
				{
					minimumLevel = Integer.parseInt(aString);

					break;
				}
				catch (NumberFormatException nfe)
				{
					// TODO Need to implement other stuff :P
					return null;
					// subTypeList.add(aString);
				}
			}

			if (st.hasMoreTokens())
			{
				maxLevel = Integer.parseInt(st.nextToken());
			}

			// TODO a temporary hack
			return ChooseLoader.parseToken(context, Spell.class, "ALL");
		}
	}
}
