/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.spell;

import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.DefaultMap;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with COST Token
 */
public class CostToken implements SpellLstToken
{

	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;

	public String getTokenName()
	{
		return "COST";
	}

	public boolean parse(Spell spell, String value)
	{
		spell.setCost(value);
		return true;
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName()
				+ " requires the default value to exist");
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " requires default value; may not start with | : " + value);
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
		DefaultMap<CDOMSimpleSingleRef<PCClass>, Integer> dm =
				new DefaultMap<CDOMSimpleSingleRef<PCClass>, Integer>();
		String defaultCost = pipeTok.nextToken();

		int startRest = value.indexOf(Constants.PIPE);
		if (startRest != -1)
		{
			if (value.charAt(value.length() - 1) == '|')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not end with | : " + value);
				return false;
			}
			if (value.charAt(startRest + 1) == '|')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not start with | : " + value);
				return false;
			}
			if (value.indexOf("||") != -1)
			{
				Logging.errorPrint(getTokenName()
					+ " arguments uses double separator || : " + value);
				return false;
			}
		}

		try
		{
			Integer i = Integer.valueOf(defaultCost);
			if (i.intValue() < 0)
			{
				Logging.errorPrint("Default Cost for Spell " + spell.getKey()
					+ " was found to be negative.  Must have a "
					+ "non-negative default cost.");
				return false;
			}
			dm.setDefaultValue(i);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName() + " token's default value ("
				+ defaultCost + ") was not a number, in line: " + value);
			return false;
		}
		while (pipeTok.hasMoreTokens())
		{
			String classOverride = pipeTok.nextToken();
			int commaLoc = classOverride.indexOf(Constants.COMMA);
			if (commaLoc == -1)
			{
				Logging.errorPrint(getTokenName() + " " + " override value "
					+ classOverride + " did not contain a comma");
				return false;
			}
			if (commaLoc != classOverride.lastIndexOf(Constants.COMMA))
			{
				Logging.errorPrint(getTokenName() + " " + " override value "
					+ classOverride + " contains more than one comma");
				return false;
			}
			String classString = classOverride.substring(0, commaLoc);
			CDOMSimpleSingleRef<PCClass> pcc =
					context.ref.getCDOMReference(PCCLASS_CLASS, classString);
			try
			{
				Integer i =
						Integer.valueOf(classOverride.substring(commaLoc + 1));
				if (i.intValue() < 0)
				{
					Logging.errorPrint("Cost for Class " + classString
						+ " in Spell " + spell.getKey()
						+ " was found to be negative.  Must have a "
						+ "non-negative cost.");
					return false;
				}
				if (dm.put(pcc, i) != null)
				{
					Logging
						.errorPrint("Class " + classString
							+ " was referenced multiple times in "
							+ getTokenName());
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint(getTokenName() + " " + classString
					+ " override value was not a number");
				return false;
			}
		}
		context.getObjectContext().put(spell, ObjectKey.COMPONENT_COST, dm);
		return true;
	}

	public String[] unparse(LoadContext context, Spell spell)
	{
		DefaultMap<CDOMSimpleSingleRef<PCClass>, Integer> dm =
				context.getObjectContext().getObject(spell,
					ObjectKey.COMPONENT_COST);
		if (dm == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Integer defaultValue = dm.getDefaultValue();
		if (defaultValue == null)
		{
			context.addWriteMessage("Default Cost for Spell " + spell.getKey()
				+ " was found to be null.  Must have a default cost "
				+ "if there are class costs specified.");
			return null;
		}
		if (defaultValue.intValue() < 0)
		{
			context.addWriteMessage("Default Cost for Spell " + spell.getKey()
				+ " was found to be negative.  Must have a "
				+ "non-negative default cost if there are class "
				+ "costs specified.");
			return null;
		}
		sb.append(defaultValue);
		TreeSet<CDOMSimpleSingleRef<PCClass>> set =
				new TreeSet<CDOMSimpleSingleRef<PCClass>>(
					TokenUtilities.REFERENCE_SORTER);
		set.addAll(dm.keySet());
		for (CDOMSimpleSingleRef<PCClass> key : set)
		{
			String className = key.getName();
			Integer cost = dm.get(key);
			if (cost.intValue() < 0)
			{
				context.addWriteMessage("Cost for PCClass " + className
					+ " for Spell " + spell.getKey()
					+ " was found to be negative.  Must have a "
					+ "non-negative cost if there are class "
					+ "costs specified.");
				return null;
			}
			sb.append(Constants.PIPE);
			sb.append(className).append(Constants.COMMA).append(cost);
		}
		return new String[]{sb.toString()};
	}
}
