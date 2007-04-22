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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SpecialAbility;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author djones4
 * 
 */
public class SaLst extends AbstractToken implements GlobalLstToken
{

	@Override
	public String getTokenName()
	{
		return "SA";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
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
	public static void parseSpecialAbility(PObject obj, String aString,
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

		while (aTok.hasMoreTokens())
		{
			String cString = aTok.nextToken();

			// Check to see if it's a PRExxx: tag
			if (PreParserFactory.isPreReqString(cString))
			{
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
				saName.append(cString);
			}

			if (".CLEAR".equals(cString))
			{
				obj.clearSpecialAbilityList();
				saName.setLength(0);
			}
		}

		sa.setName(saName.toString());

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
		if (aString == null || aString.length() == 0)
		{
			Logging.errorPrint(getTokenName() + ": line minimally requires "+getTokenName()+":<text>");
			return false;
		}

		if (aString.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + aString);
			return false;
		}
		if (aString.charAt(aString.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + aString);
			return false;
		}
		if (aString.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + aString);
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
			context.graph.unlinkChildNodesOfClass(getTokenName(), obj,
				SpecialAbility.class);
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
			context.graph.linkObjectIntoGraph(getTokenName(), obj, sa);
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
				context.graph.linkObjectIntoGraph(getTokenName(), obj, sa);
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
		context.graph.linkObjectIntoGraph(getTokenName(), obj, sa);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					SpecialAbility.class);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		List<String> list = new ArrayList<String>(edges.size());
		for (PCGraphEdge edge : edges)
		{
			StringBuilder sb = new StringBuilder();
			SpecialAbility ab = (SpecialAbility) edge.getSinkNodes().get(0);
			sb.append(ab.getDisplayName());
			List<Prerequisite> prereqs = ab.getPrerequisiteList();
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
}
