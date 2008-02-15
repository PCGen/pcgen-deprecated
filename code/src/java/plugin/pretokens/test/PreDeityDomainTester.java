/*
 * PreDeityDomain.java
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.pretokens.test;

import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.character.CharacterDataStore;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.DomainList;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 * 
 */
public class PreDeityDomainTester extends AbstractPrerequisiteTest implements
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
		int runningTotal = 0;

		if (character.getDeity() != null)
		{
			if (character.getDeity().hasDomainKeyed(prereq.getKey()))
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
		return "DEITYDOMAIN"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		return PropertyFactory
			.getFormattedString(
				"PreDeityDomain.toHtml", prereq.getOperator().toDisplayString(), prereq.getKey()); //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character)
		throws PrerequisiteException
	{
		int requiredNumber = Integer.parseInt(prereq.getOperand());
		int runningTotal = 0;
		PCGenGraph activeGraph = character.getActiveGraph();
		List<Deity> list = activeGraph.getGrantedNodeList(Deity.class);
		String requiredDomain = prereq.getKey();
		boolean requiresAny = Constants.LST_ANY.equals(requiredDomain);
		CDOMReference<DomainList> dl = character.getRulesData().getReference(
				DomainList.class, "*Starting");
		for (Deity d : list)
		{
			Collection<CDOMReference<Domain>> mods = d.getListMods(dl);
			System.err.println(mods);
			if (mods != null)
			{
				if (requiresAny)
				{
					for (CDOMReference<Domain> domain : mods)
					{
						System.err.println("!" + domain.getLSTformat());
						String domainString = domain.getLSTformat();
						if (Constants.LST_ALL.equals(domainString))
						{
							runningTotal = character.getRulesData().getAll(
									Domain.class).size();
							break;
						}
						else
						{
							runningTotal++;
						}
					}
				}
				else
				{
					for (CDOMReference<Domain> domain : mods)
					{
						String domainString = domain.getLSTformat();
						if (Constants.LST_ALL.equals(domainString))
						{
							runningTotal++;
						}
						else if (domainString.equalsIgnoreCase(requiredDomain))
						{
							runningTotal++;
						}
					}
				}
			}
		}
		runningTotal =
				prereq.getOperator().compare(runningTotal, requiredNumber);
		return countedTotal(prereq, runningTotal);
	}

}
