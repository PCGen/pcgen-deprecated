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

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import pcgen.core.PCSpell;

/**
 * @author djones4
 * 
 */
public class SpellsLst extends AbstractToken implements GlobalLstToken
{
	@Override
	public String getTokenName()
	{
		return "SPELLS";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (!(obj instanceof Campaign))
		{
			obj.getSpellSupport().addSpells(anInt, createSpellsList(value));
			return true;
		}
		return false;
	}

	/**
	 * SPELLS:<spellbook name>|[<optional parameters, pipe deliminated>] |<spell
	 * name>[,<formula for DC>] |<spell name2>[,<formula2 for DC>] |PRExxx
	 * |PRExxx
	 * 
	 * CASTERLEVEL=<formula> Casterlevel of spells TIMES=<formula> Cast Times
	 * per day, -1=At Will
	 * 
	 * @param sourceLine
	 *            Line from the LST file without the SPELLS:
	 * @return spells list
	 */
	private static List<PCSpell> createSpellsList(final String sourceLine)
	{
		List<PCSpell> spellList = new ArrayList<PCSpell>();
		StringTokenizer tok = new StringTokenizer(sourceLine, "|");
		if (tok.countTokens() > 1)
		{
			String spellBook = tok.nextToken();
			String casterLevel = null;
			String times = "1";
			List<String> preParseSpellList = new ArrayList<String>();
			List<Prerequisite> preList = new ArrayList<Prerequisite>();
			while (tok.hasMoreTokens())
			{
				String token = tok.nextToken();
				if (token.startsWith("CASTERLEVEL="))
				{
					casterLevel = token.substring(12);
				}
				else if (token.startsWith("TIMES="))
				{
					times = token.substring(6);
				}
				else if (token.startsWith("PRE") || token.startsWith("!PRE"))
				{
					try
					{
						PreParserFactory factory =
								PreParserFactory.getInstance();
						preList.add(factory.parse(token));
					}
					catch (PersistenceLayerException ple)
					{
						Logging.errorPrint(ple.getMessage(), ple);
					}
				}
				else
				{
					preParseSpellList.add(token);
				}
			}
			for (int i = 0; i < preParseSpellList.size(); i++)
			{
				StringTokenizer spellTok =
						new StringTokenizer(preParseSpellList.get(i), ",");
				String name = spellTok.nextToken();
				String dcFormula = null;
				if (spellTok.hasMoreTokens())
				{
					dcFormula = spellTok.nextToken();
				}
				PCSpell spell = new PCSpell();
				spell.setName(name);
				spell.setKeyName(spell.getKeyName());
				spell.setSpellbook(spellBook);
				spell.setCasterLevelFormula(casterLevel);
				spell.setTimesPerDay(times);
				spell.setDcFormula(dcFormula);
				for (Prerequisite prereq : preList)
				{
					spell.addPreReq(prereq);
				}
				spellList.add(spell);
			}
		}
		else
		{
			Logging
				.errorPrint("SPELLS: line minimally requires SPELLS:<spellbook name>|<spell name>");
		}
		return spellList;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		// if (!(obj instanceof Campaign)) {
		return createSpellsList(context, obj, value);
		// }
		// return false;
	}

	/**
	 * SPELLS:<spellbook name>|[<optional parameters, pipe deliminated>] |<spell
	 * name>[,<formula for DC>] |<spell name2>[,<formula2 for DC>] |PRExxx
	 * |PRExxx
	 * 
	 * CASTERLEVEL=<formula> Casterlevel of spells TIMES=<formula> Cast Times
	 * per day, -1=At Will
	 * 
	 * @param sourceLine
	 *            Line from the LST file without the SPELLS:
	 * @return spells list
	 */
	private boolean createSpellsList(LoadContext context, CDOMObject obj,
		String sourceLine)
	{
		StringTokenizer tok = new StringTokenizer(sourceLine, Constants.PIPE);
		if (tok.countTokens() < 2)
		{
			Logging.errorPrint("SPELLS: line minimally requires "
				+ "SPELLS:<spellbook name>|<spell name>");
			return false;
		}
		String spellBook = tok.nextToken();
		// Formula casterLevel = null;
		String casterLevel = null;
		String times = "1"; // FormulaFactory.getFormulaFor("1");

		String token = tok.nextToken();

		while (true)
		{
			if (token.startsWith("TIMES="))
			{
				// times = FormulaFactory.getFormulaFor(token.substring(6));
				times = token.substring(6);
				if (!tok.hasMoreTokens())
				{
					// Error
				}
				token = tok.nextToken();
			}
			else if (token.startsWith("CASTERLEVEL="))
			{
				// casterLevel =
				// FormulaFactory.getFormulaFor(token.substring(12));
				casterLevel = token.substring(12);
				if (!tok.hasMoreTokens())
				{
					// Error
				}
				token = tok.nextToken();
				//				if (token.startsWith("TIMES=")) {
				//					Logging.errorPrint("Technically, TIMES= must appear BEFORE CASTERLEVEL in SPELLS");
				//				}
			}
			else
			{
				break;
			}
		}

		List<PCGraphGrantsEdge> edgeList =
				new ArrayList<PCGraphGrantsEdge>();
		while (true)
		{
			int commaLoc = token.indexOf(',');
			String name = commaLoc == -1 ? token : token.substring(0, commaLoc);
			PrereqObject spell =
					context.ref.getCDOMReference(Spell.class, name);
			PCGraphGrantsEdge edge =
					context.graph.linkObjectIntoGraph(getTokenName(), obj,
						spell);

			edge.setAssociation(AssociationKey.CASTER_LEVEL, casterLevel);
			edge.setAssociation(AssociationKey.TIMES_PER_DAY, times);
			edge.setAssociation(AssociationKey.SPELLBOOK, spellBook);
			if (commaLoc != -1)
			{
				edge.setAssociation(AssociationKey.DC_FORMULA, token
					.substring(commaLoc + 1));
				// spell.setDC(FormulaFactory.getFormulaFor(spellString
				// .substring(commaLoc + 1)));
			}

			edgeList.add(edge);

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return true;
			}
			token = tok.nextToken();
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put spells after the "
					+ "PRExxx tags in SPELLS:?)");
				return false;
			}
			for (PCGraphGrantsEdge edge : edgeList)
			{
				edge.addPrerequisite(prereq);
			}
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}

		return true;
	}

	public String unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> edgeSet =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					Spell.class);

		if (edgeSet == null || edgeSet.isEmpty())
		{
			return null;
		}
		DoubleKeyMapToList<Set<Prerequisite>, Map<AssociationKey<String>, String>, Thingy> m =
				new DoubleKeyMapToList<Set<Prerequisite>, Map<AssociationKey<String>, String>, Thingy>();
		for (PCGraphEdge edge : edgeSet)
		{
			Spell sp = (Spell) edge.getSinkNodes().get(0);
			Map<AssociationKey<String>, String> am =
					new HashMap<AssociationKey<String>, String>();
			String dc = null;
			for (AssociationKey ak : edge.getAssociationKeys())
			{
				if (AssociationKey.DC_FORMULA.equals(ak))
				{
					dc = edge.getAssociation(AssociationKey.DC_FORMULA);
				}
				else
				{
					am.put(ak, (String) edge.getAssociation(ak));
				}
			}
			m.addToListFor(
				new HashSet<Prerequisite>(edge.getPrerequisiteList()), am,
				new Thingy(sp, dc));
		}

		StringBuilder sb = new StringBuilder();
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		boolean needTab = false;
		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			for (Map<AssociationKey<String>, String> am : m
				.getSecondaryKeySet(prereqs))
			{
				if (needTab)
				{
					sb.append('\t');
				}
				needTab = true;
				sb.append(getTokenName()).append(':');
				sb.append(am.get(AssociationKey.SPELLBOOK));
				String times = am.get(AssociationKey.TIMES_PER_DAY);
				if (!"1".equals(times))
				{
					sb.append(Constants.PIPE).append("TIMES=").append(times);
				}
				String casterLvl = am.get(AssociationKey.CASTER_LEVEL);
				if (casterLvl != null)
				{
					sb.append(Constants.PIPE).append("CASTERLEVEL=").append(
						casterLvl);
				}
				List<Thingy> thingyList = m.getListFor(prereqs, am);
				for (Thingy t : thingyList)
				{
					sb.append(Constants.PIPE);
					sb.append(t.spell.getKeyName());
					if (t.dc != null)
					{
						sb.append(Constants.COMMA).append(t.dc);
					}
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
		}
		return sb.toString();
	}

	private static class Thingy
	{
		public final Spell spell;
		public final String dc;

		public Thingy(Spell sp, String d)
		{
			spell = sp;
			dc = d;
		}
	}
}
