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
package plugin.lsttokens.domain;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMCategorizedSingleRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Ability;
import pcgen.core.Domain;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.DomainLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Deal with FEAT token
 */
public class FeatToken extends AbstractToken implements DomainLstToken
{
	public static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	public boolean parse(Domain domain, String value)
	{
		domain.addFeat(value);
		return true;
	}

	public boolean parse(LoadContext context, Domain domain, String value)
	{
		return parseFeat(context, domain, value);
	}

	public boolean parseFeat(LoadContext context, CDOMObject obj, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		if (!tok.hasMoreTokens())
		{
			return false;
		}

		List<PCGraphGrantsEdge> edgeList = new ArrayList<PCGraphGrantsEdge>();

		String token = tok.nextToken();
		
		if (token.startsWith("PRE") || token.startsWith("!PRE"))
		{
			Logging.errorPrint("Cannot have only PRExxx subtoken in "
				+ getTokenName());
			return false;
		}
		
		while (true)
		{
			CDOMCategorizedSingleRef<Ability> ability =
					context.ref.getCDOMReference(ABILITY_CLASS,
						AbilityCategory.FEAT, token);
			PCGraphGrantsEdge edge =
					context.graph.linkObjectIntoGraph(getTokenName(), obj,
						ability);
			edge.setAssociation(AssociationKey.ABILITY_NATURE,
				AbilityNature.NORMAL);
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
					+ "PRExxx tags in " + getTokenName() + ":?)");
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

	public String[] unparse(LoadContext context, Domain domain)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), domain,
					ABILITY_CLASS);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		HashMapToList<Set<Prerequisite>, CategorizedCDOMReference<Ability>> m =
				new HashMapToList<Set<Prerequisite>, CategorizedCDOMReference<Ability>>();
		for (PCGraphEdge edge : edges)
		{
			AbilityNature an =
					edge.getAssociation(AssociationKey.ABILITY_NATURE);
			if (!AbilityNature.NORMAL.equals(an))
			{
				context.addWriteMessage("Abilities awarded by "
					+ getTokenName() + " must be of NORMAL AbilityNature");
				return null;
			}
			CategorizedCDOMReference<Ability> ab =
					(CategorizedCDOMReference<Ability>) edge.getSinkNodes()
						.get(0);
			if (!AbilityCategory.FEAT.equals(ab.getCDOMCategory()))
			{
				context.addWriteMessage("Abilities awarded by "
					+ getTokenName() + " must be of CATEGORY FEAT");
				return null;
			}
			m.addToListFor(
				new HashSet<Prerequisite>(edge.getPrerequisiteList()), ab);
		}

		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		SortedSet<CategorizedCDOMReference<Ability>> set =
				new TreeSet<CategorizedCDOMReference<Ability>>(
					TokenUtilities.CAT_REFERENCE_SORTER);

		Set<String> list = new TreeSet<String>();

		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			List<CategorizedCDOMReference<Ability>> abilities =
					m.getListFor(prereqs);
			StringBuilder sb = new StringBuilder();
			boolean needBar = false;
			set.clear();
			set.addAll(abilities);
			for (CategorizedCDOMReference<Ability> ab : set)
			{
				if (needBar)
				{
					sb.append(Constants.PIPE);
				}
				needBar = true;
				/*
				 * FIXME This isn't entirely true, is it? I mean, how is the
				 * category handled with CategorizedSingleRef?
				 */
				sb.append(ab.getLSTformat());
			}
			if (prereqs != null && !prereqs.isEmpty())
			{
				TreeSet<String> prereqSet = new TreeSet<String>();
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
					prereqSet.add(swriter.toString());
				}
				sb.append(Constants.PIPE).append(
					StringUtil.join(prereqSet, Constants.PIPE));
			}
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}
}
