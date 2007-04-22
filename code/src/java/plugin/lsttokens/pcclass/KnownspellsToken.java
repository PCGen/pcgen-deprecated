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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.KnownSpellIdentifier;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SpellFilter;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.PCClassUniversalLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with KNOWNSPELLS Token
 */
public class KnownspellsToken implements PCClassLstToken,
		PCClassUniversalLstToken
{

	public String getTokenName()
	{
		return "KNOWNSPELLS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		StringTokenizer pipeTok;

		if (value.startsWith(".CLEAR"))
		{
			pcclass.clearKnownSpellsList();

			if (".CLEAR".equals(value))
			{
				return true;
			}

			pipeTok = new StringTokenizer(value.substring(6), Constants.PIPE);
		}
		else
		{
			pipeTok = new StringTokenizer(value, Constants.PIPE);
		}

		while (pipeTok.hasMoreTokens())
		{
			String totalFilter = pipeTok.nextToken();
			StringTokenizer commaTok = new StringTokenizer(totalFilter, ",");
			SpellFilter sf = new SpellFilter();

			// must satisfy all elements in a comma delimited list
			while (commaTok.hasMoreTokens())
			{
				String filterString = commaTok.nextToken();

				/*
				 * CONSIDER Want to add deprecation during 5.11 alpha cycle,
				 * thus, can be removed in 5.14 or 6.0 - thpr 11/4/06
				 */
				if (filterString.startsWith("LEVEL."))
				{
					// Logging.errorPrint("LEVEL. format deprecated in
					// KNOWNSPELLS. Please use LEVEL=");
					filterString = "LEVEL=" + filterString.substring(6);
				}
				if (filterString.startsWith("TYPE."))
				{
					// Logging.errorPrint("TYPE. format deprecated in
					// KNOWNSPELLS. Please use TYPE=");
					filterString = "TYPE=" + filterString.substring(5);
				}

				if (filterString.startsWith("LEVEL="))
				{
					// if the argument starts with LEVEL=, compare the level to
					// the desired spellLevel
					sf.setSpellLevel(Integer
						.parseInt(filterString.substring(6)));
				}
				else if (filterString.startsWith("TYPE="))
				{
					// if it starts with TYPE=, compare it to the spells type
					// list
					sf.setSpellType(filterString.substring(5));
				}
				else
				{
					// otherwise it must be the spell's name
					sf.setSpellName(filterString);
				}
			}
			if (sf.isEmpty())
			{
				Logging.errorPrint("Illegal (empty) KNOWNSPELLS Filter: "
					+ totalFilter);
			}
			pcclass.addKnownSpell(sf);
		}
		return true;
	}

	public boolean parse(LoadContext context, PObject po, String value)
		throws PersistenceLayerException
	{
		String known;
		if (value.startsWith(".CLEAR"))
		{
			context.graph.unlinkChildNodesOfClass(getTokenName(), po,
				SpellFilter.class);

			if (".CLEAR".equals(value))
			{
				return true;
			}

			known = value.substring(6);
		}
		else
		{
			known = value;
		}

		if (known.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		if (known.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (known.charAt(known.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (known.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(known, Constants.PIPE);

		while (pipeTok.hasMoreTokens())
		{
			String totalFilter = pipeTok.nextToken();

			if (totalFilter.charAt(0) == ',')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not start with , : " + value);
				return false;
			}
			if (totalFilter.charAt(totalFilter.length() - 1) == ',')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not end with , : " + value);
				return false;
			}
			if (totalFilter.indexOf(",,") != -1)
			{
				Logging.errorPrint(getTokenName()
					+ " arguments uses double separator ,, : " + value);
				return false;
			}

			StringTokenizer commaTok =
					new StringTokenizer(totalFilter, Constants.COMMA);

			/*
			 * This is a rather interesting situation - this takes items that
			 * are ALLOWED and converts them to GRANTS. Therefore, this must be
			 * done as a post-manufacturing run on the Graph.
			 * 
			 * As there is no guarantee when the factory is added that the list
			 * is complete, this resolution of known MUST be performed as a
			 * query against the PC, not stored in the graph as Grants edges.
			 */

			// must satisfy all elements in a comma delimited list
			Integer levelLim = null;
			CDOMReference<Spell> sp = null;
			while (commaTok.hasMoreTokens())
			{
				String filterString = commaTok.nextToken();

				if (filterString.startsWith("LEVEL="))
				{
					if (levelLim != null)
					{
						Logging
							.errorPrint("Cannot have more than one Level limit in "
								+ getTokenName() + ": " + value);
						return false;
					}
					// if the argument starts with LEVEL=, compare the level to
					// the desired spellLevel
					try
					{
						levelLim = Integer.valueOf(filterString.substring(6));
					}
					catch (NumberFormatException e)
					{
						Logging.errorPrint("Invalid Number in "
							+ getTokenName() + ": " + value);
						Logging.errorPrint("  Level must be an integer");
						return false;
					}
				}
				else
				{
					if (sp != null)
					{
						Logging
							.errorPrint("Cannot have more than one Type/Spell limit in "
								+ getTokenName() + ": " + value);
						return false;
					}
					sp =
							TokenUtilities.getTypeOrPrimitive(context,
								Spell.class, filterString);
					if (sp == null)
					{
						Logging.errorPrint("  encountered Invalid limit in "
							+ getTokenName() + ": " + value);
						return false;
					}
				}
			}
			if (sp == null)
			{
				/*
				 * There is no need to check for an invalid construction here
				 * (meaning levelLim is null as well) as that was implicitly
				 * checked by ensuring || did not occur.
				 */
				sp = context.ref.getCDOMAllReference(Spell.class);
			}
			KnownSpellIdentifier ksi = new KnownSpellIdentifier(sp, levelLim);
			context.graph.linkObjectIntoGraph(getTokenName(), po, ksi);
		}
		return true;
	}

	public String[] unparse(LoadContext context, PObject po)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), po,
					KnownSpellIdentifier.class);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		Map<CDOMReference<?>, Integer> map =
				new TreeMap<CDOMReference<?>, Integer>(
					TokenUtilities.REFERENCE_SORTER);
		for (PCGraphEdge edge : edges)
		{
			KnownSpellIdentifier kst = (KnownSpellIdentifier) edge.getNodeAt(1);
			CDOMReference<Spell> ref = kst.getLimit();
			Integer i = kst.getSpellLevel();
			map.put(ref, i);
		}
		List<String> list = new ArrayList<String>();
		for (Entry<CDOMReference<?>, Integer> me : map.entrySet())
		{
			StringBuilder sb = new StringBuilder();
			boolean needComma = false;
			CDOMReference<?> ref = me.getKey();
			String refString = ref.getLSTformat();
			if (!Constants.LST_ALL.equals(refString))
			{
				sb.append(refString);
				needComma = true;
			}
			Integer i = me.getValue();
			if (i != null)
			{
				if (needComma)
				{
					sb.append(',');
				}
				sb.append("LEVEL=").append(i);
			}
			list.add(sb.toString());
		}

		return new String[]{StringUtil.join(list, Constants.PIPE)};
	}
}
