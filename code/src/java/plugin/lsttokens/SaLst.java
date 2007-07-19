/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
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
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class SaLst extends AbstractToken implements GlobalLstToken
{

	private static final Class<SpecialAbility> SA_CLASS = SpecialAbility.class;

	@Override
	public String getTokenName()
	{
		return "SA";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (obj instanceof Skill)
		{
			Logging.errorPrint("SA not supported in Skills");
			return false;
		}
		parseSpecialAbility(obj, value, anInt);
		return true;
	}

	/**
	 * This method sets the special abilities granted by this [object]. For
	 * efficiency, avoid calling this method except from I/O routines.
	 * 
	 * @param obj
	 *            the PObject that is to receive the new SpecialAbility
	 * @param aString
	 *            String of special abilities delimited by pipes
	 * @param level
	 *            int level at which the ability is gained
	 */
	public void parseSpecialAbility(PObject obj, String aString,
		int level)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", true);

		if (!aTok.hasMoreTokens())
		{
			return;
		}

		StringBuffer saName = new StringBuffer();
		saName.append(aTok.nextToken());

		SpecialAbility sa = new SpecialAbility();

		boolean isPre = false;
		
		while (aTok.hasMoreTokens())
		{
			String cString = aTok.nextToken();

			// Check to see if it's a PRExxx: tag
			if (PreParserFactory.isPreReqString(cString))
			{
				isPre = true;
				try
				{
					PreParserFactory factory = PreParserFactory.getInstance();
					Prerequisite prereq = factory.parse(cString);
					/*
					 * The following subkey is required in order to give context
					 * to the variables as they are calculated (make the context
					 * the current class, so that items like Class Level can be
					 * correctly calculated.
					 */
					if (obj instanceof PCClass
						&& "var".equals(prereq.getKind()))
					{
						prereq.setSubKey("CLASS:" + obj.getKeyName());
					}
					sa.addPreReq(prereq);
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
					if (!"|".equals(cString))
					{
						Logging.errorPrint("Invalid " + getTokenName() + ": "
							+ aString);
						Logging
							.errorPrint("  PRExxx must be at the END of the Token");
						isPre = false;
					}
				}
				saName.append(cString);
			}

			if (".CLEAR".equals(cString))
			{
				obj.clearSpecialAbilityList();
				saName.setLength(0);
			}
		}

		sa.setName(saName.toString());

		if (level >= 0)
		{
			try
			{
				sa.addPreReq(PreParserFactory.createLevelPrereq(obj, level));
			}
			catch (PersistenceLayerException notUsed)
			{
				Logging.errorPrint("Failed to assign level prerequisite.", notUsed);
			}
		}
		if (obj instanceof PCClass)
		{
			sa.setSASource("PCCLASS=" + obj.getKeyName() + "|" + level);
		}

		if (!aString.equals(".CLEAR"))
		{
			Globals.addToSASet(sa);
			obj.addSpecialAbilityToList(sa);
		}
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		return parseSpecialAbility(context, obj, value);
	}

	/**
	 * This method sets the special abilities granted by this [object]. For
	 * efficiency, avoid calling this method except from I/O routines.
	 * 
	 * @param obj
	 *            the PObject that is to receive the new SpecialAbility
	 * @param aString
	 *            String of special abilities delimited by pipes
	 * @param level
	 *            int level at which the ability is gained
	 */
	public boolean parseSpecialAbility(LoadContext context, CDOMObject obj,
		String aString)
	{
		if (isEmpty(aString) || hasIllegalSeparator('|', aString))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(aString, Constants.PIPE);

		String firstToken = tok.nextToken();
		if (firstToken.startsWith("PRE") || firstToken.startsWith("!PRE"))
		{
			Logging.errorPrint("Cannot have only PRExxx subtoken in "
				+ getTokenName());
			return false;
		}

		if (Constants.LST_DOT_CLEAR.equals(firstToken))
		{
			context.graph.removeAll(getTokenName(), obj);
			firstToken = tok.nextToken();
		}

		if (Constants.LST_DOT_CLEAR.equals(firstToken))
		{
			Logging.errorPrint("SA tag confused by redundant '.CLEAR'"
				+ aString);
			return false;
		}

		SpecialAbility sa = new SpecialAbility(firstToken);

		if (!tok.hasMoreTokens())
		{
			context.graph.grant(getTokenName(), obj, sa);
			return true;
		}

		StringBuilder saName = new StringBuilder();
		saName.append(firstToken);

		String token = tok.nextToken();
		while (true)
		{
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				Logging.errorPrint("SA tag confused by '.CLEAR' as a "
					+ "middle token: " + aString);
				return false;
			}
			else if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
			else
			{
				saName.append(Constants.PIPE).append(token);
				// sa.addVariable(FormulaFactory.getFormulaFor(token));
			}

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				// CONSIDER This is a HACK and not the long term strategy of SA:
				sa.setName(saName.toString());
				context.graph.grant(getTokenName(), obj, sa);
				return true;
			}
			token = tok.nextToken();
		}
		// CONSIDER This is a HACK and not the long term strategy of SA:
		sa.setName(saName.toString());

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put Abilities after the "
					+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			/*
			 * The following subkey is required in order to give context to the
			 * variables as they are calculated (make the context the current
			 * class, so that items like Class Level can be correctly
			 * calculated).
			 */
			if (obj instanceof PCClass && "var".equals(prereq.getKind()))
			{
				prereq.setSubKey("CLASS:" + obj.getKeyName());
			}
			sa.addPrerequisite(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}
		context.graph.grant(getTokenName(), obj, sa);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		GraphChanges<SpecialAbility> changes =
				context.graph
					.getChangesFromToken(getTokenName(), obj, SA_CLASS);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> list = new ArrayList<String>(added.size());
		for (LSTWriteable lw : added)
		{
			StringBuilder sb = new StringBuilder();
			SpecialAbility ab = (SpecialAbility) lw;
			sb.append(ab.getDisplayName());
			if (ab.hasPrerequisites())
			{
				sb.append(Constants.PIPE);
				sb.append(getPrerequisiteString(context, ab.getPrerequisiteList()));
			}
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}
}
