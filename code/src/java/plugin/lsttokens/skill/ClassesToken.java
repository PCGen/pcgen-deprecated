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
package plugin.lsttokens.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.graph.PCGraphAllowsEdge;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Skill;
import pcgen.core.SkillList;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.SkillLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with CLASSES Token
 */
public class ClassesToken extends AbstractToken implements SkillLstToken
{

	@Override
	public String getTokenName()
	{
		return "CLASSES";
	}

	public boolean parse(Skill skill, String value)
	{
		skill.addClassList(value);
		return true;
	}

	public boolean parse(LoadContext context, Skill skill, String value)
		throws PersistenceLayerException
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		if (Constants.LST_ALL.equals(value))
		{
			addSkillAllowed(context, skill, context.ref
				.getCDOMAllReference(SkillList.class));
			return true;
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
		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
		List<CDOMReference<SkillList>> allowed =
				new ArrayList<CDOMReference<SkillList>>();
		List<Prerequisite> prevented = new ArrayList<Prerequisite>();

		while (pipeTok.hasMoreTokens())
		{
			String className = pipeTok.nextToken();
			if (className.startsWith("!"))
			{
				String clString = className.substring(1);
				if (Constants.LST_ALL.equals(clString)
					|| Constants.LST_ANY.equals(clString))
				{
					Logging.errorPrint("Invalid " + getTokenName()
						+ " cannot use !ALL");
					return false;
				}
				prevented.add(getPrerequisite("!PRECLASS:1," + clString));
			}
			else
			{
				allowed.add(context.ref.getCDOMReference(SkillList.class,
					className));
			}
		}
		if (!prevented.isEmpty() && !allowed.isEmpty())
		{
			Logging.errorPrint("Non-sensical " + getTokenName() + ": " + value);
			Logging.errorPrint("  Token contains both negated "
				+ "and non-negated class references");
			return false;
		}

		if (!allowed.isEmpty())
		{
			for (CDOMReference<SkillList> ref : allowed)
			{
				addSkillAllowed(context, skill, ref);
			}
		}
		if (!prevented.isEmpty())
		{
			CDOMReference<SkillList> ref =
					context.ref.getCDOMAllReference(SkillList.class);
			PCGraphAllowsEdge allEdge = addSkillAllowed(context, skill, ref);
			for (Prerequisite prereq : prevented)
			{
				allEdge.addPrerequisite(prereq);
			}
		}
		/*
		 * TODO There is an All/Any mismatch here... (CLASSES uses ALL, PRECLASS
		 * uses ANY)
		 */
		return true;
	}

	private PCGraphAllowsEdge addSkillAllowed(LoadContext context, Skill skill,
		PrereqObject ref)
	{
		PCGraphAllowsEdge edge =
				context.graph.linkAllowIntoGraph(getTokenName(), ref, skill);
		edge.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);
		return edge;
	}

	public String[] unparse(LoadContext context, Skill skill)
	{
		Set<PCGraphEdge> classEdgeSet =
				context.graph.getParentLinksFromToken(getTokenName(), skill,
					SkillList.class);
		if (classEdgeSet.size() == 0)
		{
			return null;
		}
		CDOMGroupRef<SkillList> allRef =
				context.ref.getCDOMAllReference(SkillList.class);
		SortedSet<CDOMReference<SkillList>> set =
				new TreeSet<CDOMReference<SkillList>>(
					TokenUtilities.REFERENCE_SORTER);
		boolean negated = false;
		for (PCGraphEdge edge : classEdgeSet)
		{
			SkillCost sc = edge.getAssociation(AssociationKey.SKILL_COST);
			if (sc == null)
			{
				context.addWriteMessage("Incoming Allows Edge to "
					+ skill.getKey() + " had no SkillCost");
				return null;
			}
			if (!sc.equals(SkillCost.CLASS))
			{
				context.addWriteMessage("Incoming Allows Edge to "
					+ skill.getKey() + " and built by " + getTokenName()
					+ "had invalid SkillCost: " + sc
					+ ". Must be CLASS skills if defined by " + getTokenName());
				return null;
			}
			List<PrereqObject> sourceNodes = edge.getSourceNodes();
			if (sourceNodes.size() != 1)
			{
				context.addWriteMessage("Incoming Edge to " + skill.getKey()
					+ " had more than one source: " + sourceNodes);
				return null;
			}
			CDOMReference<SkillList> ref =
					(CDOMReference<SkillList>) sourceNodes.get(0);
			if (!ref.getReferenceClass().equals(SkillList.class))
			{
				context.addWriteMessage("Incoming Edge to " + skill.getKey()
					+ " was built by " + getTokenName() + " but the source "
					+ ref + " is not a PCClass reference");
				return null;
			}
			if (ref.equals(allRef))
			{
				if (classEdgeSet.size() > 1)
				{
					context.addWriteMessage("All Class Reference was "
						+ "attached to " + skill.getKey() + " by Token "
						+ getTokenName() + " but there are also "
						+ "other references granting " + skill.getKey()
						+ " as a Class Skill.  " + "This is non-sensical");
					return null;
				}
				if (edge.hasPrerequisites())
				{
					negated = true;
					List<Prerequisite> prereqs = edge.getPrerequisiteList();
					for (Prerequisite p : prereqs)
					{
						// Mimic getting a Reference back from the Prereq
						set.add(context.ref.getCDOMReference(SkillList.class, p
							.getKey()));
					}
				}
				else
				{
					set.add(ref); // "ALL"
				}
			}
			else
			{
				set.add(ref);
			}
		}
		boolean needBar = false;
		StringBuilder sb = new StringBuilder();
		for (CDOMReference<SkillList> ref : set)
		{
			if (needBar)
			{
				sb.append(Constants.PIPE);
			}
			if (negated)
			{
				sb.append('!');
			}
			sb.append(ref.getLSTformat());
			needBar = true;
		}
		return new String[]{sb.toString()};
	}
}
