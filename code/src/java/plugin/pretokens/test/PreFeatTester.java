/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class PreFeatTester extends AbstractPrerequisiteTest implements
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
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
				"PreFeat.error", prereq.toString())); //$NON-NLS-1$
		}

		String key = prereq.getKey();
		String subKey = prereq.getSubKey();
		int runningTotal =
				PrerequisiteUtilities.passesAbilityTest(prereq, character,
					countMults, number, key, subKey, AbilityCategory.FEAT
						.getKeyName(), AbilityCategory.FEAT);
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

		if (aString.startsWith("TYPE="))
		{
			// {0} {1} {2}(s) of type {3}
			return PropertyFactory.getFormattedString("PreFeat.type.toHtml",
				new Object[]{prereq.getOperator().toDisplayString(),
					prereq.getOperand(),
					AbilityCategory.FEAT.getDisplayName().toLowerCase(),
					aString.substring(5)});
		}
		// {2} {3} {1} {0}
		return PropertyFactory.getFormattedString("PreFeat.toHtml",
			new Object[]{AbilityCategory.FEAT.getDisplayName().toLowerCase(),
				aString, prereq.getOperator().toDisplayString(),
				prereq.getOperand()}); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "FEAT"; //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, PlayerCharacter character)
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
		List<Ability> list = activeGraph.getGrantedNodeList(Ability.class);
		ABILITY: for (Ability a : list)
		{
			if (!pcgen.cdom.enumeration.AbilityCategory.FEAT.equals(a
				.getCDOMCategory()))
			{
				continue;
			}
			String featKey = a.getKeyName();
			if (keyIsType)
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
			else if (!featKey.equalsIgnoreCase(key))
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
			//TODO Need an else here for error checking?
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

	private int getWildcardCount(PlayerCharacter character, boolean countMults,
		Ability a, String subKey)
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
				List<PObject> assoc = character.getAssociated(a);
				String subStart = subKey.substring(0, wildCardPos - 1);
				for (PObject po : assoc)
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

	private int getAssociatedCountOfType(PlayerCharacter character,
		boolean countMults, Ability a, String subKey)
	{
		int runningTotal = 0;
		List<PObject> list = character.getAssociated(a);
		POBJECT: for (PObject po : list)
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

	private int getAbilityWeight(PlayerCharacter character, boolean countMults,
		Ability a)
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
