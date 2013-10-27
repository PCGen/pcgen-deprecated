/*
 * PreSkill.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision: 2683 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-04-06 16:52:57 -0400 (Fri, 06 Apr 2007) $
 *
 */package plugin.pretokens.test;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.inst.CDOMSkill;
import pcgen.character.CharacterDataStore;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 * 
 */
public class PreSkillTotTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		// Compute the skill name from the Prerequisite
		String requiredSkillKey = prereq.getKey().toUpperCase();
		if (prereq.getSubKey() != null)
		{
			requiredSkillKey += " (" + prereq.getSubKey().toUpperCase() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		final boolean isType =
				(requiredSkillKey.startsWith("TYPE.") || requiredSkillKey.startsWith("TYPE=")); //$NON-NLS-1$ //$NON-NLS-2$
		if (isType)
		{
			requiredSkillKey = requiredSkillKey.substring(5);
		}
		final String skillKey = requiredSkillKey;

		// Now locate all instances of this skillname and test them
		final int percentageSignPosition = skillKey.lastIndexOf('%');
		int runningTotal = 0;

		boolean foundMatch = false;
		boolean foundSkill = false;
		for (Skill aSkill : character.getSkillList())
		{
			final String aSkillKey = aSkill.getKeyName().toUpperCase();
			if (isType)
			{
				if (percentageSignPosition >= 0)
				{
					for (String type : aSkill.getTypeList(false))
					{
						if (type.startsWith(skillKey.substring(0,
							percentageSignPosition)))
						{
							foundMatch = true;
							break;
						}
					}
				}
				else if (aSkill.isType(skillKey))
				{
					foundMatch = true;
				}

				if (foundMatch)
				{
					foundSkill = foundMatch;
					runningTotal += aSkill.getTotalRank(character).intValue();
					if (runningTotal == 0)
					{
						foundMatch = false;
					}
				}
			}
			else if (aSkillKey.equals(skillKey)
				|| ((percentageSignPosition >= 0) && aSkillKey
					.startsWith(skillKey.substring(0, percentageSignPosition))))
			{
				foundSkill = true;
				runningTotal += aSkill.getTotalRank(character).intValue();
				if (runningTotal > 0)
				{
					foundMatch = true;
				}
			}
		}

		// If we are looking for a negative test i.e. !PRESKILL and the PC
		// doesn't have the skill we have to return a match
		if (!foundSkill)
		{
			if (prereq.getOperator() == PrerequisiteOperator.LT)
			{
				runningTotal++;
			}
		}
		return countedTotal(prereq, runningTotal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "SKILL.TOT"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		String skillName = prereq.getKey();
		if (prereq.getSubKey() != null && !prereq.getSubKey().equals("")) //$NON-NLS-1$
		{
			skillName += " (" + prereq.getSubKey() + ")"; //$NON-NLS-1$ //$NON-NLS-2$

		}

		final String foo =
				PropertyFactory.getFormattedString("PreSkill.toHtml", //$NON-NLS-1$
					new Object[]{prereq.getOperator().toDisplayString(),
						prereq.getOperand(), skillName});
		return foo;
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character)
		throws PrerequisiteException
	{
		// Compute the skill name from the Prerequisite
		String requiredSkillKey = prereq.getKey().toUpperCase();
		if (prereq.getSubKey() != null)
		{
			requiredSkillKey += " (" + prereq.getSubKey().toUpperCase() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		int runningTotal = 0;
		PCGenGraph graph = character.getActiveGraph();
		if ((requiredSkillKey.startsWith("TYPE.") || requiredSkillKey
			.startsWith("TYPE=")))
		{
			List<CDOMSkill> list = graph.getGrantedNodeList(CDOMSkill.class);
			SKILL: for (CDOMSkill aSkill : list)
			{
				StringTokenizer tok =
						new StringTokenizer(requiredSkillKey.substring(5), ".");
				// Must match all listed types in order to qualify
				while (tok.hasMoreTokens())
				{
					Type requiredType = Type.getConstant(tok.nextToken());
					if (!aSkill.containsInList(ListKey.TYPE, requiredType))
					{
						continue SKILL;
					}
				}
				runningTotal += character.getTotalWeight(aSkill);
			}
		}
		else
		{
			int percentLoc = requiredSkillKey.lastIndexOf('%');
			if (percentLoc == -1)
			{
				CDOMSkill skill =
						graph.getGrantedNode(CDOMSkill.class, requiredSkillKey);
				runningTotal += character.getTotalWeight(skill);
			}
			else
			{
				List<CDOMSkill> list = graph.getGrantedNodeList(CDOMSkill.class);
				for (CDOMSkill aSkill : list)
				{
					String aSkillKey = aSkill.getKeyName().toUpperCase();
					if (aSkillKey.startsWith(requiredSkillKey.substring(0,
						percentLoc)))
					{
						runningTotal += character.getTotalWeight(aSkill);
					}
				}
			}
		}
		return Math.max(runningTotal, 0);
	}

}
