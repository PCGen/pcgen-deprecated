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
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.ClassSkillList;
import pcgen.core.Skill;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.MasterListChanges;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.SkillLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with CLASSES Token
 */
public class ClassesToken extends AbstractToken implements SkillLstToken
{

	private static final Class<ClassSkillList> SKILLLIST_CLASS =
			ClassSkillList.class;

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
	{
		if (Constants.LST_ALL.equals(value))
		{
			addSkillAllowed(context, skill, context.ref
				.getCDOMAllReference(SKILLLIST_CLASS));
			return true;
		}
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
		List<CDOMReference<ClassSkillList>> allowed =
				new ArrayList<CDOMReference<ClassSkillList>>();
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
				allowed.add(context.ref.getCDOMReference(SKILLLIST_CLASS,
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
			for (CDOMReference<ClassSkillList> ref : allowed)
			{
				addSkillAllowed(context, skill, ref);
			}
		}
		if (!prevented.isEmpty())
		{
			CDOMReference<ClassSkillList> ref =
					context.ref.getCDOMAllReference(SKILLLIST_CLASS);
			AssociatedPrereqObject allEdge =
					addSkillAllowed(context, skill, ref);
			for (Prerequisite prereq : prevented)
			{
				allEdge.addPrerequisite(prereq);
			}
		}
		return true;
	}

	private AssociatedPrereqObject addSkillAllowed(LoadContext context,
		Skill skill, CDOMReference<ClassSkillList> ref)
	{
		AssociatedPrereqObject edge =
				context.list.addToMasterList(getTokenName(), skill, ref, skill);
		edge.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);
		return edge;
	}

	public String[] unparse(LoadContext context, Skill skill)
	{
		CDOMGroupRef<ClassSkillList> allRef =
				context.ref.getCDOMAllReference(SKILLLIST_CLASS);
		SortedSet<CDOMReference<ClassSkillList>> set =
				new TreeSet<CDOMReference<ClassSkillList>>(
					TokenUtilities.REFERENCE_SORTER);
		boolean usesAll = false;
		boolean usesIndividual = false;
		boolean negated = false;
		for (CDOMReference<ClassSkillList> swl : context.list
			.getMasterLists(SKILLLIST_CLASS))
		{
			MasterListChanges<Skill> changes =
					context.list.getChangesInMasterList(getTokenName(), skill,
						swl);
			if (changes == null)
			{
				// Legal if no CLASSES was present in the Skill
				continue;
			}
			// List<String> list = new ArrayList<String>();
			if (changes.hasRemovedItems())
			{
				context.addWriteMessage(getTokenName()
					+ " does not support .CLEAR.");
				return null;
			}
			if (changes.includesGlobalClear())
			{
				context.addWriteMessage(getTokenName()
					+ " does not support .CLEAR");
				return null;
			}
			if (changes.hasAddedItems())
			{
				for (LSTWriteable added : changes.getAdded())
				{

					if (!skill.getLSTformat().equals(added.getLSTformat()))
					{
						context.addWriteMessage("Skill " + getTokenName()
							+ " token cannot allow another Skill "
							+ "(must only allow itself)");
						return null;
					}
					AssociatedPrereqObject assoc =
							changes.getAddedAssociation(added);
					SkillCost sc =
							assoc.getAssociation(AssociationKey.SKILL_COST);
					if (sc == null)
					{
						context.addWriteMessage("Allowed Skill in "
							+ skill.getKey() + " had no SkillCost");
						return null;
					}
					if (!sc.equals(SkillCost.CLASS))
					{
						context.addWriteMessage("Allowed Skill "
							+ skill.getKey() + " built by " + getTokenName()
							+ "had invalid SkillCost: " + sc
							+ ". Must be CLASS skills if defined by "
							+ getTokenName());
						return null;
					}
					if (swl.equals(allRef))
					{
						usesAll = true;
						if (assoc.hasPrerequisites())
						{
							negated = true;
							List<Prerequisite> prereqs =
									assoc.getPrerequisiteList();
							for (Prerequisite p : prereqs)
							{
								// Mimic getting a Reference back from the
								// Prereq
								set.add(context.ref.getCDOMReference(
									SKILLLIST_CLASS, p.getKey()));
							}
						}
						else
						{
							set.add(swl); // "ALL"
						}
					}
					else
					{
						usesIndividual = true;
						set.add(swl);
					}
				}
			}
		}
		if (!usesAll && !usesIndividual)
		{
			// Legal if no CLASSES was present in the Spell
			return null;
		}
		if (usesAll && usesIndividual)
		{
			context.addWriteMessage("All SkillList Reference was "
				+ "attached to " + skill.getKey() + " by Token "
				+ getTokenName() + " but there are also "
				+ "other references granting " + skill.getKey()
				+ " as a Class Skill.  " + "This is non-sensical");
			return null;
		}

		boolean needBar = false;
		StringBuilder sb = new StringBuilder();
		for (CDOMReference<ClassSkillList> ref : set)
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
