/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Current Ver: $Revision: 3316 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-07-04 22:41:00 -0400 (Wed, 04 Jul 2007) $
 */
package plugin.lstcompatibility.pcclass;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ProhibitedSpellType;
import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassLevelLstCompatibilityToken;
import pcgen.util.Logging;

/**
 * Class deals with PROHIBITSPELL Token
 */
public class Prohibitspell514Token extends AbstractToken implements
		PCClassLevelLstCompatibilityToken
{

	@Override
	public String getTokenName()
	{
		return "PROHIBITSPELL";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value,
		int level)
	{
		if (level != 1)
		{
			return false;
		}
		SpellProhibitor<?> sp = subParse(context, pcc, value);
		if (sp == null)
		{
			return false;
		}
		context.graph.grant(getTokenName(), pcc, sp);
		return true;
	}

	public SpellProhibitor<?> subParse(LoadContext context, PCClass pcc,
		String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return null;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		String token = tok.nextToken();

		int dotLoc = token.indexOf(Constants.DOT);
		if (dotLoc == -1)
		{
			Logging.errorPrint(getTokenName()
				+ " has no . separator for arguments: " + value);
			return null;
		}
		String pstString = token.substring(0, dotLoc);
		ProhibitedSpellType<?> type;

		try
		{
			type = ProhibitedSpellType.getReference(pstString);
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint(getTokenName()
				+ " encountered an invalid Prohibited Spell Type: " + value);
			Logging.errorPrint("  Legal values are: "
				+ StringUtil.join(Arrays.asList(ProhibitedSpellType.values()),
					", "));
			return null;
		}

		SpellProhibitor<?> spellProb =
				typeSafeParse(context, pcc, type, token.substring(dotLoc + 1));
		if (spellProb == null)
		{
			Logging.errorPrint("  entire token value was: " + value);
			return null;
		}
		if (!tok.hasMoreTokens())
		{
			// No prereqs, so we're done
			return spellProb;
		}
		token = tok.nextToken();

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging
					.errorPrint("   (Did you put more than one limit, or items after the "
						+ "PRExxx tags in " + getTokenName() + ":?)");
				return null;
			}
			spellProb.addPrerequisite(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}
		return spellProb;
	}

	private <T> SpellProhibitor<T> typeSafeParse(LoadContext context,
		PCClass pcc, ProhibitedSpellType<T> type, String args)
	{
		SpellProhibitor<T> spellProb = new SpellProhibitor<T>();
		spellProb.setType(type);

		if (args.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " " + type
				+ " has no arguments");
			return null;
		}

		String joinChar = getJoinChar(type, new LinkedList<T>());
		if (args.indexOf(joinChar) == 0)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with " + joinChar);
			return null;
		}
		if (args.lastIndexOf(joinChar) == args.length() - 1)
		{
			Logging.errorPrint(getTokenName() + " arguments may not end with "
				+ joinChar);
			return null;
		}
		if (args.indexOf(joinChar + joinChar) != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator " + joinChar + joinChar);
			return null;
		}

		StringTokenizer elements = new StringTokenizer(args, joinChar);
		while (elements.hasMoreTokens())
		{
			String aValue = elements.nextToken();
			if (type.equals(ProhibitedSpellType.ALIGNMENT)
				&& (!aValue.equalsIgnoreCase("GOOD"))
				&& (!aValue.equalsIgnoreCase("EVIL"))
				&& (!aValue.equalsIgnoreCase("LAWFUL"))
				&& (!aValue.equalsIgnoreCase("CHAOTIC")))
			{
				Logging.errorPrint("Illegal PROHIBITSPELL:ALIGNMENT subtag '"
					+ aValue + "'");
				return null;
			}
			else
			{
				spellProb.addValue(type.getTypeValue(aValue));
			}
		}
		return spellProb;
	}

	private <T> String getJoinChar(ProhibitedSpellType<T> pst,
		Collection<T> spValues)
	{
		return pst.getRequiredCount(spValues) == 1 ? Constants.COMMA
			: Constants.DOT;
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
