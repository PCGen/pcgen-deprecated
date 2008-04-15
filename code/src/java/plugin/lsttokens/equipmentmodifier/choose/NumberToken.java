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

import pcgen.cdom.helper.NumberChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.helper.SimpleCollectionChoiceSet;
import pcgen.cdom.inst.CDOMEqMod;
import pcgen.core.Constants;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.EqModChooseLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.ChoiceSetToken;
import pcgen.util.Logging;

public class NumberToken extends AbstractToken implements EqModChooseLstToken,
		ChoiceSetToken<CDOMEqMod>
{

	@Override
	public String getTokenName()
	{
		return "NUMBER";
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
			else if (tokString.startsWith("NOSIGN"))
			{
				// OK
			}
			else if (tokString.startsWith("SKIPZERO"))
			{
				// OK
			}
			else if (tokString.startsWith("MULTIPLE"))
			{
				// OK
			}
			else
			{
				Integer.parseInt(tokString);
			}
		}
		if (max == null)
		{
			if (min != null)
			{
				Logging
						.errorPrint("Cannot have MIN=n without MAX=m in CHOOSE:NUMBER: "
								+ value);
				return false;
			}
		}
		else
		{
			if (min == null)
			{
				Logging
						.errorPrint("Cannot have MAX=n without MIN=m in CHOOSE:NUMBER: "
								+ value);
				return false;
			}
			if (max < min)
			{
				Logging
						.errorPrint("Cannot have MAX= less than MIN= in CHOOSE:NUMBER: "
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

	public PrimitiveChoiceSet<?> parse(LoadContext context, CDOMEqMod mod,
			String value) throws PersistenceLayerException
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
		Integer increment = null;
		List<Integer> intList = new ArrayList<Integer>();
		boolean noSign = false;
		boolean skipZero = false;
		boolean multiple = false;
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
			else if (tokString.startsWith("INCREMENT="))
			{
				if (increment != null)
				{
					Logging
							.errorPrint("Cannot specify INCREMENT= twice in CHOOSE: "
									+ value);
					return null;
				}
				increment = Integer.parseInt(tokString.substring(4));
			}
			else if (tokString.startsWith("NOSIGN"))
			{
				if (noSign)
				{
					Logging
							.errorPrint("Cannot specify NOSIGN twice in CHOOSE: "
									+ value);
					return null;
				}
				noSign = true;
			}
			else if (tokString.startsWith("SKIPZERO"))
			{
				if (skipZero)
				{
					Logging
							.errorPrint("Cannot specify SKIPZERO twice in CHOOSE: "
									+ value);
					return null;
				}
				skipZero = true;
			}
			else if (tokString.startsWith("MULTIPLE"))
			{
				if (multiple)
				{
					Logging
							.errorPrint("Cannot specify MULTIPLE twice in CHOOSE: "
									+ value);
					return null;
				}
				multiple = true;
			}
			else
			{
				intList.add(Integer.valueOf(tokString));
			}
		}
		PrimitiveChoiceSet<Integer> cs;
		if (max == null)
		{
			if (min == null)
			{
				cs = new SimpleCollectionChoiceSet<Integer>(intList);
			}
			else
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
			if (!intList.isEmpty())
			{
				Logging
						.errorPrint("Cannot specify individual values and MIN=/MAX= "
								+ value);
				return null;
			}
			NumberChoiceSet ncs = new NumberChoiceSet(min, max);
			ncs.setShowSign(!noSign);
			ncs.setShowZero(!skipZero);
			ncs.setMultiple(multiple);
			if (increment != null)
			{
				if (increment.intValue() < 1)
				{
					Logging.errorPrint("Increment in CHOOSE must be >= 1: "
							+ value);
					return null;
				}
				ncs.setIncrement(increment.intValue());
			}
			cs = ncs;
		}
		return cs;
	}

	public Class<CDOMEqMod> getTokenClass()
	{
		return CDOMEqMod.class;
	}
}
