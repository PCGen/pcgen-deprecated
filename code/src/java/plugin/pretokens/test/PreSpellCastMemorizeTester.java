/*
 * Created on 21-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import java.util.List;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.character.CharacterDataStore;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

/**
 * @author Valued Customer
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreSpellCastMemorizeTester extends AbstractPrerequisiteTest
		implements PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindHandled()
	 */
	public String kindHandled()
	{
		return "spellcast.memorize"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.prereq.Prerequisite, pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{

		final int requiredNumber = Integer.parseInt(prereq.getOperand());
		final boolean prereqMemorized =
				prereq.getKey().toUpperCase().startsWith("Y"); //$NON-NLS-1$
		int runningTotal = 0;

		for (PCClass aClass : character.getClassList())
		{
			if (aClass.getMemorizeSpells() == prereqMemorized)
			{
				runningTotal++;
			}
		}

		runningTotal =
				prereq.getOperator().compare(runningTotal, requiredNumber);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		final boolean prereqMemorized =
				prereq.getKey().toUpperCase().startsWith("Y"); //$NON-NLS-1$

		if (prereqMemorized)
		{
			return PropertyFactory
				.getFormattedString(
					"PreSpellCastMemorize.toHtml_does_memorise", prereq.getOperator().toDisplayString()); //$NON-NLS-1$
		}
		return PropertyFactory
			.getFormattedString(
				"PreSpellCastMemorize.toHtml_does_not_memorise", prereq.getOperator().toDisplayString()); //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character) throws PrerequisiteException
	{
		int requiredNumber = Integer.parseInt(prereq.getOperand());
		Boolean prereqMemorized =
				Boolean.valueOf(prereq.getKey().toUpperCase().startsWith("Y"));
		int runningTotal = 0;
		
		PCGenGraph activeGraph = character.getActiveGraph();
		List<PCClass> list = activeGraph.getGrantedNodeList(PCClass.class);
		for (PCClass aClass : list)
		{
			if (prereqMemorized.equals(aClass.get(ObjectKey.MEMORIZE_SPELLS)))
			{
				runningTotal++;
			}
		}

		runningTotal =
				prereq.getOperator().compare(runningTotal, requiredNumber);
		return countedTotal(prereq, runningTotal);
	}

}
