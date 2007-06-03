/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Equipment;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreClassTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final Equipment equipment,
		PlayerCharacter aPC)
	{
		Logging.errorPrint("PreClass on equipment: " + equipment.getName()
			+ "  pre: " + toHtmlString(prereq));
		return 0;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		int runningTotal = 0;
		int countedTotal = 0;

		final String aString = prereq.getKey().toUpperCase();
		final int preClass = Integer.parseInt(prereq.getOperand());

		if ("SPELLCASTER".equals(aString)) //$NON-NLS-1$
		{
			int spellCaster = character.isSpellCaster(preClass, false);
			if (spellCaster > 0)
			{
				if (prereq.isCountMultiples())
				{
					countedTotal = spellCaster;
				}
				else
				{
					runningTotal = preClass;
				}
			}
		}
		else if (aString.startsWith("SPELLCASTER.")) //$NON-NLS-1$
		{
			int spellCaster =
					character.isSpellCaster(aString.substring(12), preClass,
						false);
			if (spellCaster > 0)
			{
				if (prereq.isCountMultiples())
				{
					countedTotal = spellCaster;
				}
				else
				{
					runningTotal = preClass;
				}
			}
		}
		else if (aString.equals("ANY"))
		{
			for (PCClass cl : character.getClassList())
			{
				if (prereq.isCountMultiples())
				{
					if (cl.getLevel() >= preClass)
					{
						countedTotal++;
					}
				}
				else
				{
					runningTotal = Math.max(runningTotal, cl.getLevel());
				}
			}
		}
		else
		{
			final PCClass aClass = character.getClassKeyed(aString);
			if (aClass != null)
			{
				if (prereq.isCountMultiples())
				{
					if (aClass.getLevel() >= preClass)
					{
						countedTotal++;
					}
				}
				else
				{
					runningTotal += aClass.getLevel();
				}
			}
		}
		runningTotal = prereq.getOperator().compare(runningTotal, preClass);
		return countedTotal(prereq, prereq.isCountMultiples() ? countedTotal : runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "CLASS"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		final String level = prereq.getOperand();
		final String operator = prereq.getOperator().toDisplayString();

		return PropertyFactory.getFormattedString(
			"PreClass.toHtml", prereq.getKey(), operator, level); //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, PlayerCharacter character) throws PrerequisiteException
	{
		//TODO given 5.12/5.14 changes, this just needs to be rewritten... thpr Jun 2, 2007
		PCGenGraph activeGraph = character.getActiveGraph();
		List<PCClassLevel> levellist = activeGraph.getGrantedNodeList(PCClassLevel.class);
		String classString = prereq.getKey();
		int runningTotal = 0;
		if ("SPELLCASTER".equalsIgnoreCase(classString)) //$NON-NLS-1$
		{
			List<PCClass> classlist = activeGraph.getGrantedNodeList(PCClass.class);
			for (PCClass pcClass : classlist)
			{
				int classLevels =
						(int) character.getTotalBonusTo("CASTERLEVEL", pcClass
							.getKeyName());
				classLevels +=
						(int) character.getTotalBonusTo("PCLEVEL", pcClass
							.getKeyName());
				runningTotal += getClassLevel(pcClass, levellist);
			}
			if (character.isSpellCaster(preClass, false))
			{
				runningTotal = preClass;
			}
		}
		else if (aString.startsWith("SPELLCASTER.")) //$NON-NLS-1$
		{
			if (character.isSpellCaster(aString.substring(12), preClass,
				false))
			{
				runningTotal = preClass;
			}
		}
		else if (Constants.LST_ANY.equals(classString))
		{
			List<PCClass> classlist = activeGraph.getGrantedNodeList(PCClass.class);
			for (PCClass pcClass : classlist)
			{
				runningTotal += getClassLevel(pcClass, levellist);
			}
		}
		else
		{
			PCClass pcClass  = activeGraph.getGrantedNode(PCClass.class, classString);
			runningTotal += getClassLevel(pcClass, levellist);
		}
		// return totalLevels;
	}

	private int getClassLevel(PCClass pcClass, List<PCClassLevel> levellist)
	{
		int classLevel = 0;
		for (PCClassLevel pcl : levellist)
		{
			int level = pcClass.getCDOMLevel(pcl);
			classLevel = Math.max(classLevel, level);
		}
		// Techically if classLevel == -1 we have a VERY funky PC
		// but we'll let that fly for now
		return Math.max(0, classLevel);
	}

}
