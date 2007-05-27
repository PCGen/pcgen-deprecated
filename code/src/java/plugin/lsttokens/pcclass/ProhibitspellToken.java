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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.pcclass;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;
import pcgen.util.enumeration.ProhibitedSpellType;

/**
 * Class deals with PROHIBITSPELL Token
 */
public class ProhibitspellToken extends AbstractToken implements
		PCClassLstToken, PCClassClassLstToken
{

	@Override
	public String getTokenName()
	{
		return "PROHIBITSPELL";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		SpellProhibitor spellProb = new SpellProhibitor();

		boolean isPre = false;
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken().toUpperCase();

			if (PreParserFactory.isPreReqString(aString))
			{
				isPre = true;
				try
				{
					final PreParserFactory factory =
							PreParserFactory.getInstance();
					spellProb.addPreReq(factory.parse(aString));
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
				}
			}
			else
			{
				if (isPre)
				{
					Logging.errorPrint("Invalid " + getTokenName() + ": " + value);
					Logging.errorPrint("  PRExxx must be at the END of the Token");
				}
				final StringTokenizer elements =
						new StringTokenizer(aString, ".", false);
				final String aType = elements.nextToken();

				for (ProhibitedSpellType type : ProhibitedSpellType.values())
				{
					if (type.toString().equalsIgnoreCase(aType))
					{
						spellProb.setType(type);
						while (elements.hasMoreTokens())
						{
							String aValue = elements.nextToken();
							if (type.equals(ProhibitedSpellType.ALIGNMENT)
								&& (!aValue.equals("GOOD"))
								&& (!aValue.equals("EVIL"))
								&& (!aValue.equals("LAWFUL"))
								&& (!aValue.equals("CHAOTIC")))
							{
								Logging
									.errorPrint("Illegal PROHIBITSPELL:ALIGNMENT subtag '"
										+ aValue + "'");
							}
							else
							{
								if (type.equals(ProhibitedSpellType.SPELL))
								{
									for (String spell : aValue.split(","))
									{
										spellProb.addValue(spell);
									}
								}
								else
								{
									spellProb.addValue(aValue);
								}
							}
						}
					}
				}
				if (spellProb.getType() == null)
				{
					Logging.errorPrint("Illegal PROHIBITSPELL subtag '"
						+ aString + "'");
				}
			}
		}
		pcclass.setProhibitSpell(spellProb);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		SpellProhibitor sp = subParse(context, pcc, value);
		if (sp == null)
		{
			return false;
		}
		context.graph.grant(getTokenName(), pcc, sp);
		return true;
	}

	public SpellProhibitor subParse(LoadContext context, PCClass pcc,
		String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return null;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		SpellProhibitor spellProb = new SpellProhibitor();

		String token = tok.nextToken();

		int dotLoc = token.indexOf(Constants.DOT);
		if (dotLoc == -1)
		{
			Logging.errorPrint(getTokenName()
				+ " has no . separator for arguments: " + value);
			return null;
		}
		String pstString = token.substring(0, dotLoc);
		ProhibitedSpellType type;
		try
		{
			type = ProhibitedSpellType.valueOf(pstString);
			spellProb.setType(type);
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
		String args = token.substring(dotLoc + 1);
		if (args.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " " + type
				+ " has no arguments: " + value);
			return null;
		}

		String joinChar = getJoinChar(type, new LinkedList<String>());
		if (args.indexOf(joinChar) == 0)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with " + joinChar + " : " + value);
			return null;
		}
		if (args.lastIndexOf(joinChar) == args.length() - 1)
		{
			Logging.errorPrint(getTokenName() + " arguments may not end with "
				+ joinChar + " : " + value);
			return null;
		}
		if (args.indexOf(joinChar + joinChar) != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator " + joinChar + joinChar
				+ " : " + value);
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
				// TODO This is a String, should it be typesafe?
				spellProb.addValue(aValue);
			}
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

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		GraphChanges<SpellProhibitor> changes =
				context.graph.getChangesFromToken(getTokenName(), pcc,
					SpellProhibitor.class);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added.isEmpty())
		{
			// Zero indicates no Token present
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (LSTWriteable lstw : added)
		{
			SpellProhibitor sp = (SpellProhibitor) lstw;
			StringBuilder sb = new StringBuilder();
			ProhibitedSpellType pst = sp.getType();
			sb.append(pst.toString().toUpperCase());
			sb.append('.');
			Set<String> spValues = sp.getValueSet();
			String joinChar = getJoinChar(pst, spValues);
			sb.append(StringUtil.join(spValues, joinChar));

			PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
			List<Prerequisite> prereqs = sp.getPreReqList();
			if (prereqs != null && !prereqs.isEmpty())
			{
				for (Prerequisite p : prereqs)
				{
					StringWriter swriter = new StringWriter();
					try
					{
						prereqWriter.write(swriter, p);
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage("Error writing Prerequisite: "
							+ e);
						return null;
					}
					sb.append(Constants.PIPE).append(swriter.toString());
				}
			}
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}

	private String getJoinChar(ProhibitedSpellType pst,
		Collection<String> spValues)
	{
		return pst.getRequiredCount(spValues) == 1 ? Constants.COMMA
			: Constants.DOT;
	}
}
