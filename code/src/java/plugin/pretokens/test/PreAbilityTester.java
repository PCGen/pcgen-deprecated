/*
 * PreAbilityTester.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on January 23, 2006
 *
 * Current Ver: $Revision: 1777 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 15:36:01 +1100 (Sun, 17 Dec 2006) $
 *
 */
package plugin.pretokens.test;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.character.CharacterDataStore;
import pcgen.core.AbilityCategory;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.util.PropertyFactory;

/**
 * <code>PreAbilityParser</code> tests whether a character passes ability
 * prereqs.
 * 
 * Last Editor: $Author: jdempsey $ Last Edited: $Date: 2006-12-17 15:36:01
 * +1100 (Sun, 17 Dec 2006) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1777 $
 */
public class PreAbilityTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final Equipment equipment,
		final PlayerCharacter aPC) throws PrerequisiteException
	{
		if (aPC == null)
		{
			return 0;
		}
		return passes(prereq, aPC);
	}

	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
		throws PrerequisiteException
	{
		final boolean countMults = prereq.isCountMultiples();

		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
				"PreAbility.error", prereq.toString())); //$NON-NLS-1$
		}

		GameMode gameMode = SettingsHandler.getGame();
		String key = prereq.getKey();
		String subKey = prereq.getSubKey();
		String categoryName = prereq.getCategoryName();
		AbilityCategory category = gameMode.getAbilityCategory(categoryName);
		int runningTotal =
				PrerequisiteUtilities.passesAbilityTest(prereq, character,
					countMults, number, key, subKey, categoryName, category);
		return countedTotal(prereq, runningTotal);
	}

	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		String aString = prereq.getKey();
		if ((prereq.getSubKey() != null) && !prereq.getSubKey().equals(""))
		{
			aString = aString + " ( " + prereq.getSubKey() + " )";
		}

		if (aString.startsWith("TYPE=")) //$NON-NLS-1$
		{
			if (prereq.getCategoryName().length() > 0)
			{
				// {0} {1} {2}(s) of type {3}
				return PropertyFactory.getFormattedString(
					"PreAbility.type.toHtml", //$NON-NLS-1$
					new Object[]{prereq.getOperator().toDisplayString(),
						prereq.getOperand(), prereq.getCategoryName(),
						aString.substring(5)});
			}
			else
			{
				// {0} {1} ability(s) of type {2}
				return PropertyFactory.getFormattedString(
					"PreAbility.type.noCat.toHtml", //$NON-NLS-1$ 
					new Object[]{prereq.getOperator().toDisplayString(),
						prereq.getOperand(), aString.substring(5)});
			}

		}
		// {2} {3} {1} {0}
		return PropertyFactory.getFormattedString("PreAbility.toHtml", //$NON-NLS-1$
			new Object[]{prereq.getCategoryName(), aString,
				prereq.getOperator().toDisplayString(), prereq.getOperand()});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "ABILITY"; //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character)
		throws PrerequisiteException
	{
		boolean countMults = prereq.isCountMultiples();

		int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
				"PreFeat.error", prereq.toString())); //$NON-NLS-1$
		}

		String key = prereq.getKey();
		String subKey = prereq.getSubKey();
		String categoryName = prereq.getCategoryName();
		pcgen.cdom.enumeration.CDOMAbilityCategory category =
				pcgen.cdom.enumeration.CDOMAbilityCategory.valueOf(categoryName);
		//TODO What if CATEGORY=null??
		final boolean keyIsAny = key.equalsIgnoreCase("ANY"); //$NON-NLS-1$
		boolean keyIsType = key.startsWith("TYPE=") || key.startsWith("TYPE."); //$NON-NLS-1$ //$NON-NLS-2$
		boolean subKeyIsType =
				subKey != null
					&& (subKey.startsWith("TYPE=") || subKey.startsWith("TYPE.")); //$NON-NLS-1$ //$NON-NLS-2$
		if (keyIsType)
		{
			key = key.substring(5);
		}
		if (subKeyIsType)
		{
			subKey = subKey.substring(5);
		}

		int runningTotal = 0;
		PCGenGraph activeGraph = character.getActiveGraph();
		List<CDOMAbility> list = activeGraph.getGrantedNodeList(CDOMAbility.class);
		ABILITY: for (CDOMAbility a : list)
		{
			if (!category.equals(a.getCDOMCategory()))
			{
				continue;
			}
			String featKey = a.getKeyName();
			if (!keyIsAny && keyIsType)
			{
				StringTokenizer tok = new StringTokenizer(key, ".");
				// Must match all listed types in order to qualify
				while (tok.hasMoreTokens())
				{
					Type requiredType = Type.getConstant(tok.nextToken());
					if (!a.containsInList(ListKey.TYPE, requiredType))
					{
						continue ABILITY;
					}
				}
			}
			else if (!keyIsAny && !featKey.equalsIgnoreCase(key))
			{
				if (!subKeyIsType && subKey != null)
				{
					String s1 = key + " (" + subKey + ")";
					String s2 = key + "(" + subKey + ")";
					if (featKey.equalsIgnoreCase(s1)
						|| featKey.equalsIgnoreCase(s2))
					{
						runningTotal++;
						if (!countMults)
						{
							break;
						}
					}
				}
				continue ABILITY;
			}
			//TODO Need an else !keyIsAny here for error checking?
			// either this feat has matched on the name, or the type
			if (subKey == null)
			{
				runningTotal += getAbilityWeight(character, countMults, a);
			}
			else if (subKeyIsType) // TYPE syntax
			{
				runningTotal +=
						getAssociatedCountOfType(character, countMults, a,
							subKey);
			}
			else if (featKey.equalsIgnoreCase(key)
				&& character.containsAssociatedKey(a, subKey))
			{
				Boolean mult = a.get(ObjectKey.MULTIPLE_ALLOWED);
				if (countMults && mult != null && mult.booleanValue())
				{
					// TODO I think this is broken - matches 5.12, tho'
					// - thpr Jun 2, 07
					runningTotal += character.getAssociatedCount(a);
				}
				else
				{
					runningTotal++;
				}
			}
			else
			{
				runningTotal +=
						getWildcardCount(character, countMults, a, subKey);
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

	private int getWildcardCount(CharacterDataStore character, boolean countMults,
			CDOMAbility a, String subKey)
	{
		int count = 0;

		int wildCardPos = subKey.indexOf('%');

		if (wildCardPos > -1)
		{
			if (wildCardPos == 0)
			{
				if (countMults)
				{
					count += character.getAssociatedCount(a);
				}
				else
				{
					count++;
				}
			}
			else
			{
				List<CDOMObject> assoc = character.getAssociated(a);
				String subStart = subKey.substring(0, wildCardPos - 1);
				for (CDOMObject po : assoc)
				{
					if (po.getKeyName().regionMatches(true, 0, subStart, 0,
						wildCardPos))
					{
						count++;
						if (!countMults)
						{
							break;
						}
					}
				}
			}
		}
		return count;
	}

	private int getAssociatedCountOfType(CharacterDataStore character,
		boolean countMults, CDOMAbility a, String subKey)
	{
		int runningTotal = 0;
		List<CDOMObject> list = character.getAssociated(a);
		POBJECT: for (CDOMObject po : list)
		{
			StringTokenizer tok = new StringTokenizer(subKey.substring(5), ".");
			// Must match all listed types in order to qualify
			while (tok.hasMoreTokens())
			{
				Type requiredType = Type.getConstant(tok.nextToken());
				if (!po.containsInList(ListKey.TYPE, requiredType))
				{
					continue POBJECT;
				}
			}
			runningTotal++;
			if (!countMults)
			{
				break;
			}
		}
		return runningTotal;
	}

	private int getAbilityWeight(CharacterDataStore character, boolean countMults,
			CDOMAbility a)
	{
		int increment;
		Boolean mult = a.get(ObjectKey.MULTIPLE_ALLOWED);
		if (countMults && mult != null && mult.booleanValue())
		{
			Boolean stack = a.get(ObjectKey.STACKS);
			if (stack != null && stack.booleanValue())
			{
				increment = character.getTotalWeight(a);
			}
			else
			{
				increment = character.getAssociatedCount(a);
			}
		}
		else
		{
			increment = 1;
		}
		return increment;
	}

}
