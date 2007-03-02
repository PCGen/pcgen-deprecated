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
import java.util.StringTokenizer;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMCategorizedSingleRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ObjectKey;
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
		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		if (!tok.hasMoreTokens())
		{
			return false;
		}

		List<PCGraphGrantsEdge> edgeList = new ArrayList<PCGraphGrantsEdge>();

		String token = tok.nextToken();
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

	public String unparse(LoadContext context, Domain domain)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), domain,
					ABILITY_CLASS);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		HashMapToList<Set<Prerequisite>, Ability> m =
				new HashMapToList<Set<Prerequisite>, Ability>();
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
			Ability ab = (Ability) edge.getSinkNodes().get(0);
			if (!AbilityCategory.FEAT.equals(ab.get(ObjectKey.CATEGORY)))
			{
				context.addWriteMessage("Abilities awarded by "
					+ getTokenName() + " must be of CATEGORY FEAT");
				return null;
			}
			m.addToListFor(
				new HashSet<Prerequisite>(edge.getPrerequisiteList()), ab);
		}

		StringBuilder sb = new StringBuilder();
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		boolean needSpacer = false;
		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			List<Ability> abilities = m.getListFor(prereqs);
			if (needSpacer)
			{
				sb.append('\t');
			}
			sb.append(getTokenName()).append(':');
			boolean needBar = false;
			for (Ability ab : abilities)
			{
				if (needBar)
				{
					sb.append(Constants.PIPE);
				}
				needBar = true;
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
						context.addWriteMessage("Error writing Prerequisite: "
							+ e);
						return null;
					}
					sb.append(Constants.PIPE).append(swriter.toString());
				}
			}
			needSpacer = true;
		}
		return sb.toString();
	}
}
