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
package plugin.lsttokens.equipmentmodifier.choose;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.helper.CollectionChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.helper.SpellLevelLimit;
import pcgen.core.Constants;
import pcgen.core.EquipmentModifier;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.EqModChooseCompatibilityToken;
import pcgen.persistence.lst.EqModChooseLstToken;
import pcgen.util.Logging;

public class EqBuilderSpellToken extends AbstractToken implements
		EqModChooseLstToken, EqModChooseCompatibilityToken
{

	private final Class<Spell> SPELL_CLASS = Spell.class;

	public boolean parse(EquipmentModifier po, String prefix, String value)
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
			Logging
					.errorPrint("CHOOSE:" + getTokenName()
							+ " must have two or more | delimited arguments : "
							+ value);
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

	public PrimitiveChoiceSet<?>[] parse(LoadContext context,
			EquipmentModifier mod, String value)
			throws PersistenceLayerException
	{
		List<SpellLevelLimit> sllList = new ArrayList<SpellLevelLimit>();

		if (value == null)
		{
			CDOMReference<Spell> allRef = context.ref
					.getCDOMAllReference(Spell.class);
			sllList.add(new SpellLevelLimit(allRef, 0, Integer.MAX_VALUE));
		}
		else
		{
			if (value.length() == 0 || hasIllegalSeparator('|', value))
			{
				return null;
			}
			StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
			while (st.hasMoreTokens())
			{
				String type = st.nextToken();
				if (!st.hasMoreTokens())
				{
					Logging.errorPrint("Invalid " + getTokenName()
							+ " was expecting a minimum and maximum "
							+ "level to follow spell type: " + type);
					return null;
				}
				String minLevel = st.nextToken();
				int min = Integer.parseInt(minLevel);
				if (!st.hasMoreTokens())
				{
					Logging.errorPrint("Invalid " + getTokenName()
							+ " was expecting a maximum "
							+ "level to follow minimum " + minLevel
							+ " for spell type: " + type);
					return null;
				}
				String maxLevel = st.nextToken();
				int max = Integer.parseInt(maxLevel);
				CDOMReference<Spell> ref = context.ref.getCDOMTypeReference(
						SPELL_CLASS, type);
				sllList.add(new SpellLevelLimit(ref, min, max));
			}
		}
		return new PrimitiveChoiceSet<?>[] { new CollectionChoiceSet<SpellLevelLimit>(
				sllList) };
	}

}
