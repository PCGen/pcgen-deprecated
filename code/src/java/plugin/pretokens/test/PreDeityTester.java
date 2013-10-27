/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.character.CharacterDataStore;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreDeityTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
		throws PrerequisiteException
	{
		int runningTotal;

		if (prereq.getKey().startsWith("PANTHEON."))//$NON-NLS-1$
		{
			String pantheon = prereq.getKey().substring(9);
			List<String> charDeityPantheon =
					character.getDeity() != null ? character.getDeity()
						.getPantheonList() : new ArrayList<String>();
			if (prereq.getOperator().equals(PrerequisiteOperator.EQ)
				|| prereq.getOperator().equals(PrerequisiteOperator.GTEQ))
			{
				runningTotal = (charDeityPantheon.contains(pantheon)) ? 1 : 0;
			}
			else if (prereq.getOperator().equals(PrerequisiteOperator.NEQ)
				|| prereq.getOperator().equals(PrerequisiteOperator.LT))
			{
				runningTotal = (charDeityPantheon.contains(pantheon)) ? 0 : 1;
			}
			else
			{
				throw new PrerequisiteException(PropertyFactory
					.getFormattedString(
						"PreDeity.error.bad_coparator", prereq.toString())); //$NON-NLS-1$
			}
		}
		else
		{
			final String charDeity =
					character.getDeity() != null ? character.getDeity()
						.getKeyName() : ""; //$NON-NLS-1$
			if (prereq.getOperator().equals(PrerequisiteOperator.EQ)
				|| prereq.getOperator().equals(PrerequisiteOperator.GTEQ))
			{
				runningTotal =
						(charDeity.equalsIgnoreCase(prereq.getKey())) ? 1 : 0;
			}
			else if (prereq.getOperator().equals(PrerequisiteOperator.NEQ)
				|| prereq.getOperator().equals(PrerequisiteOperator.LT))
			{
				runningTotal =
						(charDeity.equalsIgnoreCase(prereq.getKey())) ? 0 : 1;
			}
			else
			{
				throw new PrerequisiteException(PropertyFactory
					.getFormattedString(
						"PreDeity.error.bad_coparator", prereq.toString())); //$NON-NLS-1$
			}
		}

		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "DEITY"; //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character)
		throws PrerequisiteException
	{
		int runningTotal = 0;

		PCGenGraph activeGraph = character.getActiveGraph();
		List<CDOMDeity> list = activeGraph.getGrantedNodeList(CDOMDeity.class);

		if (prereq.getKey().startsWith("PANTHEON."))//$NON-NLS-1$
		{
			Pantheon p = Pantheon.valueOf(prereq.getKey().substring(9));
			for (CDOMDeity d : list)
			{
				List<Pantheon> pantheonList = d.getListFor(ListKey.PANTHEON);
				if (prereq.getOperator().equals(PrerequisiteOperator.EQ)
					|| prereq.getOperator().equals(PrerequisiteOperator.GTEQ))
				{
					runningTotal += (pantheonList.contains(p)) ? 1 : 0;
				}
				else if (prereq.getOperator().equals(PrerequisiteOperator.NEQ)
					|| prereq.getOperator().equals(PrerequisiteOperator.LT))
				{
					runningTotal += (pantheonList.contains(p)) ? 0 : 1;
				}
				else
				{
					throw new PrerequisiteException(PropertyFactory
						.getFormattedString(
							"PreDeity.error.bad_coparator", prereq.toString())); //$NON-NLS-1$
				}
			}
		}
		else
		{
			for (CDOMDeity d : list)
			{
				String charDeity = d.getKeyName();
				if (prereq.getOperator().equals(PrerequisiteOperator.EQ)
					|| prereq.getOperator().equals(PrerequisiteOperator.GTEQ))
				{
					runningTotal +=
							(charDeity.equalsIgnoreCase(prereq.getKey())) ? 1
								: 0;
				}
				else if (prereq.getOperator().equals(PrerequisiteOperator.NEQ)
					|| prereq.getOperator().equals(PrerequisiteOperator.LT))
				{
					runningTotal +=
							(charDeity.equalsIgnoreCase(prereq.getKey())) ? 0
								: 1;
				}
				else
				{
					throw new PrerequisiteException(PropertyFactory
						.getFormattedString(
							"PreDeity.error.bad_coparator", prereq.toString())); //$NON-NLS-1$
				}
			}
		}

		return countedTotal(prereq, runningTotal);
	}
}
