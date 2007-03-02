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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphAllowsEdge;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.core.SpellList;
import pcgen.util.Logging;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;

/**
 * @author djones4
 * 
 */
public class SpelllevelLst extends AbstractToken implements GlobalLstToken
{

	@Override
	public String getTokenName()
	{
		return "SPELLLEVEL";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		// SPELLLEVEL:CLASS|Name1,Name2=Level1|Spell1,Spell2,Spell3|Name3=Level2|Spell4,Spell5|PRExxx|PRExxx
		if (!(obj instanceof Campaign))
		{
			final StringTokenizer tok = new StringTokenizer(value, "|");

			if (tok.countTokens() < 3)
			{
				Logging.errorPrint("Badly formed SPELLLEVEL tag1: " + value);
				return false;
			}

			final String tagType = tok.nextToken(); // CLASS or DOMAIN
			final List<String> preList = new ArrayList<String>();

			// The 2 lists below should always have the same number of items
			final List<String> wNameList = new ArrayList<String>();
			final List<String> wSpellList = new ArrayList<String>();

			while (tok.hasMoreTokens())
			{
				final String nameList = tok.nextToken();

				if (nameList.startsWith("PRE") || nameList.startsWith("!PRE"))
				{
					preList.add(nameList);
					break;
				}

				if (nameList.indexOf("=") < 0)
				{
					Logging
						.errorPrint("Badly formed SPELLLEVEL tag2: " + value);
					return false;
				}

				wNameList.add(nameList);

				if (!tok.hasMoreTokens())
				{
					Logging
						.errorPrint("Badly formed SPELLLEVEL tag3: " + value);
					return false;
				}

				wSpellList.add(tok.nextToken());
			}

			while (tok.hasMoreTokens())
			{
				final String nameList = tok.nextToken();

				if (nameList.startsWith("PRE") || nameList.startsWith("!PRE"))
				{
					preList.add(nameList);
				}
				else
				{
					Logging.errorPrint("Badly formed SPELLLEVEL PRE tag: "
						+ value);
					return false;
				}
			}

			//
			// Parse the prereq list
			//
			List<Prerequisite> prereqs = new ArrayList<Prerequisite>();
			try
			{
				PreParserFactory factory = PreParserFactory.getInstance();
				prereqs = factory.parse(preList);
			}
			catch (PersistenceLayerException ple)
			{
				Logging.errorPrint("Badly formed SPELLLEVEL PRE tag: " + value);
			}

			for (Iterator<String> iSpell = wSpellList.iterator(), iName =
					wNameList.iterator(); iSpell.hasNext() || iName.hasNext();)
			{
				// Check to see if both exists
				if (!(iSpell.hasNext() && iName.hasNext()))
				{
					Logging
						.errorPrint("Badly formed SPELLLEVEL tag4: " + value);
					return false;
				}

				final StringTokenizer bTok =
						new StringTokenizer(iSpell.next(), ",");
				final String classList = iName.next();

				while (bTok.hasMoreTokens())
				{
					final String spellLevel =
							classList.substring(classList.indexOf("=") + 1);
					final String spellName = bTok.nextToken();
					final StringTokenizer cTok =
							new StringTokenizer(classList.substring(0,
								classList.indexOf("=")), ",");

					while (cTok.hasMoreTokens())
					{
						final String className = cTok.nextToken();

						if (className.startsWith("SPELLCASTER.")
							|| !obj.getSpellSupport().containsLevelFor(tagType,
								className, spellName))
						{
							obj.getSpellSupport().addSpellLevel(tagType,
								className, spellName, spellLevel, prereqs);
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		// SPELLLEVEL:CLASS|Name1,Name2=Level1|Spell1,Spell2,Spell3|Name3=Level2|Spell4,Spell5|PRExxx|PRExxx

		// Why?
		// if (obj instanceof Campaign) {
		// return false;
		// }
		StringTokenizer tok = new StringTokenizer(value, "|");

		if (tok.countTokens() < 3)
		{
			Logging.errorPrint("Insufficient values in SPELLLEVEL tag: "
				+ value);
			return false;
		}

		String tagType = tok.nextToken(); // CLASS or DOMAIN

		String tokString = tok.nextToken();

		List<PCGraphEdge> edgeList = new ArrayList<PCGraphEdge>();
		while (true)
		{
			if (!tok.hasMoreTokens())
			{
				Logging.errorPrint("Missing SPELLLEVEL tag after: " + tokString
					+ " - entire token was " + value);
				return false;
			}
			String spellString = tok.nextToken();

			List<PCGraphEdge> localEdgeList =
					subParse(context, obj, tokString, spellString);
			if (localEdgeList == null)
			{
				Logging.errorPrint(getTokenName()
					+ " error - entire token was " + value);
				return false;
			}
			edgeList.addAll(localEdgeList);

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return true;
			}
			tokString = tok.nextToken();
			if (tokString.startsWith("PRE") || tokString.startsWith("!PRE"))
			{
				break;
			}
		}

		while (true)
		{
			if (tokString.startsWith("PRE") || tokString.startsWith("!PRE"))
			{
				Prerequisite p = super.getPrerequisite(tokString);
				for (PCGraphEdge edge : edgeList)
				{
					edge.addPrerequisite(p);
				}
			}
			else
			{
				Logging.errorPrint("Badly formed SPELLLEVEL PRE tag: "
					+ tokString + " Line was " + value);
				return false;
			}
			if (!tok.hasMoreTokens())
			{
				break;
			}
			tokString = tok.nextToken();
		}

		return true;
	}

	private List<PCGraphEdge> subParse(LoadContext context, CDOMObject obj,
		String tokString, String spellString)
	{
		int equalLoc = tokString.indexOf(Constants.EQUALS);
		if (equalLoc == -1)
		{
			Logging.errorPrint("Expected an = in SPELLLEVEL " + "definition: "
				+ tokString);
			return null;
		}

		String casterString = tokString.substring(0, equalLoc);
		String spellLevel = tokString.substring(equalLoc + 1);
		Integer splLevel;
		try
		{
			splLevel = Integer.decode(spellLevel);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Expected a number for SPELLLEVEL, found: "
				+ spellLevel);
			return null;
		}

		StringTokenizer clTok =
				new StringTokenizer(casterString, Constants.COMMA);
		List<Prerequisite> prereqList = new ArrayList<Prerequisite>();
		while (clTok.hasMoreTokens())
		{
			String classString = clTok.nextToken();
			if (classString.startsWith("SPELLCASTER."))
			{
				/*
				 * TODO Deprecate the use of SPELLCASTER? No need for it, since
				 * the function isn't any different.
				 * 
				 * Actually, I think it IS necessary still - thpr 1/8/07
				 */
				/*
				 * FIXME TODO Get the classes of a given caster type? The real
				 * todo here is to force the addition of the appropriate
				 * SpellList object (Arcane, Divine) whenever a Class is of a
				 * particular caster type
				 * 
				 * Note this may be modified by ongoing discussions in
				 * _experimental
				 */
				classString = classString.substring(12);
			}
			CDOMSimpleSingleRef<SpellList> sl =
					context.ref.getCDOMReference(SpellList.class, classString);
//			Prerequisite p =
//					getPrerequisite("PRECONTEXT:SPELLLIST|" + sl.getName());
//			prereqList.add(p);
		}

		StringTokenizer spTok = new StringTokenizer(spellString, ",");

		List<PCGraphEdge> edgeList = new ArrayList<PCGraphEdge>();
		while (spTok.hasMoreTokens())
		{
			String spellName = spTok.nextToken();
			PrereqObject spell =
					context.ref.getCDOMReference(Spell.class, spellName);
			for (Prerequisite p : prereqList)
			{
				/*
				 * This linkAllow* is INSIDE this for loop, as the edges must be
				 * in parallel to allow multiple ways to access the spell (not
				 * multiple ways to restrict the access)
				 */
				PCGraphAllowsEdge edge =
						context.graph.linkAllowIntoGraph(getTokenName(), obj,
							spell);
				edge.setAssociation(AssociationKey.SPELL_LEVEL, splLevel);
				edge.addPrerequisite(p);
				edgeList.add(edge);
			}
		}
		return edgeList;
	}

	public String unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					Spell.class);
		if (edgeList == null || edgeList.isEmpty())
		{
			return null;
		}

		DoubleKeyMapToList<Set<Prerequisite>, Integer, Spell> m =
				new DoubleKeyMapToList<Set<Prerequisite>, Integer, Spell>();
		for (PCGraphEdge edge : edgeList)
		{
			Spell sp = (Spell) edge.getSinkNodes().get(0);
			m.addToListFor(
				new HashSet<Prerequisite>(edge.getPrerequisiteList()), edge
					.getAssociation(AssociationKey.SPELL_LEVEL), sp);
		}

		StringBuilder sb = new StringBuilder();
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		boolean needSpacer = false;
		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			if (needSpacer)
			{
				sb.append('\t');
			}
			sb.append(getTokenName()).append(':');
			boolean needPipe = false;
			for (Integer level : m.getSecondaryKeySet(prereqs))
			{
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				needPipe = true;
				//FIXME Write spell list name
				sb.append(Constants.EQUALS).append(level)
					.append(Constants.PIPE);
				List<Spell> spells = m.getListFor(prereqs, level);
				boolean needComma = false;
				for (Spell ab : spells)
				{
					if (needComma)
					{
						sb.append(Constants.COMMA);
					}
					needComma = true;
					sb.append(ab.getKeyName());
				}
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
							context
								.addWriteMessage("Error writing Prerequisite: "
									+ e);
							return null;
						}
						sb.append(Constants.PIPE).append(swriter.toString());
					}
				}
			}
			needSpacer = true;
		}
		return sb.toString();
	}
}
