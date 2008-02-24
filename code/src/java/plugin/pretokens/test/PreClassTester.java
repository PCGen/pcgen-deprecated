/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.character.CharacterDataStore;
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
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class PreClassTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
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
		else if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
		{
			String typeString = aString.substring(5);
			for (PCClass cl : character.getClassList())
			{
				if (cl.isType(typeString))
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
		return countedTotal(prereq, prereq.isCountMultiples() ? countedTotal
			: runningTotal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "CLASS"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
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

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character)
		throws PrerequisiteException
	{
		// TODO given 5.12/5.14 changes, this just needs to be rewritten... thpr
		// Jun 2, 2007
		PCGenGraph activeGraph = character.getActiveGraph();
		String classString = prereq.getKey();
		int preClass = Integer.parseInt(prereq.getOperand());
		int runningTotal = 0;
		int countedTotal = 0;
		if ("SPELLCASTER".equalsIgnoreCase(classString)) //$NON-NLS-1$
		{
			List<CDOMPCClass> classlist =
					activeGraph.getGrantedNodeList(CDOMPCClass.class);
			for (CDOMPCClass pcClass : classlist)
			{
				Type type = pcClass.get(ObjectKey.SPELL_TYPE);
				if (type != null)
				{
					int thisLevel = character.getLevel(pcClass);
					if (prereq.isCountMultiples())
					{
						if (thisLevel >= preClass)
						{
							countedTotal++;
						}
					}
					else
					{
						runningTotal += thisLevel;
					}
				}
			}
		}
		else if (classString.startsWith("SPELLCASTER.")) //$NON-NLS-1$
		{
			String typeString = classString.substring(12);
			try
			{
				Type reqType = Type.valueOf(typeString);
				List<CDOMPCClass> classlist =
						activeGraph.getGrantedNodeList(CDOMPCClass.class);
				for (CDOMPCClass pcClass : classlist)
				{
					Type type = pcClass.get(ObjectKey.SPELL_TYPE);
					if (reqType.equals(type))
					{
						int thisLevel = character.getLevel(pcClass);
						if (prereq.isCountMultiples())
						{
							if (thisLevel >= preClass)
							{
								countedTotal++;
							}
						}
						else
						{
							runningTotal += thisLevel;
						}
					}
				}
			}
			catch (IllegalArgumentException iae)
			{
				Logging.errorPrint("Invalid Type: " + typeString
					+ " found in PRECLASS");
			}
		}
		else if (Constants.LST_ANY.equals(classString))
		{
			List<CDOMPCClass> classlist =
					activeGraph.getGrantedNodeList(CDOMPCClass.class);
			for (CDOMPCClass pcClass : classlist)
			{
				int thisLevel = character.getLevel(pcClass);
				if (prereq.isCountMultiples())
				{
					if (thisLevel >= preClass)
					{
						countedTotal++;
					}
				}
				else
				{
					runningTotal += thisLevel;
				}
			}
		}
		// TODO need to account for type
		else
		{
			CDOMPCClass pcClass =
					activeGraph.getGrantedNode(CDOMPCClass.class, classString);
			int thisLevel = character.getLevel(pcClass);
			if (prereq.isCountMultiples())
			{
				if (thisLevel >= preClass)
				{
					countedTotal++;
				}
			}
			else
			{
				runningTotal += thisLevel;
			}
		}
		runningTotal = prereq.getOperator().compare(runningTotal, preClass);
		return countedTotal(prereq, prereq.isCountMultiples() ? countedTotal
			: runningTotal);
	}
}
