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

import pcgen.cdom.helper.CollectionChoiceSet;
import pcgen.cdom.helper.NumberChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.core.Constants;
import pcgen.core.EquipmentModifier;
import pcgen.core.PCStat;
import pcgen.core.SettingsHandler;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.EqModChooseCompatibilityToken;
import pcgen.persistence.lst.EqModChooseLstToken;
import pcgen.util.Logging;

public class StatBonusToken extends AbstractToken implements
		EqModChooseLstToken, EqModChooseCompatibilityToken
{

	private static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

	public String getTokenName()
	{
		return "STATBONUS";
	}

	public boolean parse(EquipmentModifier mod, String prefix, String value)
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
		List<PCStat> list = SettingsHandler.getGame().getUnmodifiableStatList();
		Integer min = null;
		Integer max = null;
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			if (tokString.startsWith("MIN="))
			{
				min = Integer.valueOf(tokString.substring(4));
				// OK
			}
			else if (tokString.startsWith("MAX="))
			{
				max = Integer.valueOf(tokString.substring(4));
				// OK
			}
			else if (tokString.startsWith("TITLE="))
			{
				// OK
			}
			else if (tokString.startsWith("INCREMENT="))
			{
				// OK
				Integer.parseInt(tokString.substring(4));
			}
			else
			{
				// Ensure this is a primitive STAT
				boolean found = false;
				for (PCStat stat : list)
				{
					if (tokString.equals(stat.getAbb()))
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					Logging.errorPrint("Did not find STAT: " + tokString
							+ " used in CHOOSE:STATBONUS " + value);
				}
			}
		}
		if (max == null)
		{
			if (min != null)
			{
				Logging
						.errorPrint("Cannot have MIN=n without MAX=m in CHOOSE:STATBONUS: "
								+ value);
				return false;
			}
		}
		else
		{
			if (min == null)
			{
				Logging
						.errorPrint("Cannot have MAX=n without MIN=m in CHOOSE:STATBONUS: "
								+ value);
				return false;
			}
			if (max < min)
			{
				Logging
						.errorPrint("Cannot have MAX= less than MIN= in CHOOSE:STATBONUS: "
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
		mod.setChoiceString(sb.toString());
		return true;
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
		if (isEmpty(value) || hasIllegalSeparator('|', value))
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
		Integer min = null;
		Integer max = null;
		ArrayList<PCStat> statList = new ArrayList<PCStat>();
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			if (tokString.startsWith("MIN="))
			{
				if (min != null)
				{
					Logging.errorPrint("Cannot specify MIN= twice in CHOOSE: "
							+ value);
					return null;
				}
				min = Integer.valueOf(tokString.substring(4));
			}
			else if (tokString.startsWith("MAX="))
			{
				if (max != null)
				{
					Logging.errorPrint("Cannot specify MAX= twice in CHOOSE: "
							+ value);
					return null;
				}
				max = Integer.valueOf(tokString.substring(4));
			}
			else
			{
				PCStat ref = context.ref.getConstructedCDOMObject(PCSTAT_CLASS,
						tokString);
				if (ref == null)
				{
					return null;
				}
				statList.add(ref);
			}
		}
		if (max == null)
		{
			if (min != null)
			{
				Logging
						.errorPrint("Cannot have MIN=n without MAX=m in CHOOSE:NUMBER: "
								+ value);
				return null;
			}
		}
		else
		{
			if (min == null)
			{
				Logging
						.errorPrint("Cannot have MAX=n without MIN=m in CHOOSE:NUMBER: "
								+ value);
				return null;
			}
			if (max < min)
			{
				Logging
						.errorPrint("Cannot have MAX= less than MIN= in CHOOSE:NUMBER: "
								+ value);
				return null;
			}
		}
		if (statList.isEmpty())
		{
			statList
					.addAll(context.ref.getConstructedCDOMObjects(PCSTAT_CLASS));
		}
		statList.trimToSize();
		return new PrimitiveChoiceSet<?>[] {
				new CollectionChoiceSet<PCStat>(statList),
				new NumberChoiceSet(min, max) };
	}

}
