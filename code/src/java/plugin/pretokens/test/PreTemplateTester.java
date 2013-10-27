/*
 * PreTemplate.java
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

import java.util.List;

import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.character.CharacterDataStore;
import pcgen.core.PCTemplate;
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
public class PreTemplateTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
		throws PrerequisiteException
	{
		int runningTotal = 0;

		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
				"PreTemplate.error", prereq.toString())); //$NON-NLS-1$
		}

		if (!character.getTemplateList().isEmpty())
		{

			String templateKey = prereq.getKey().toUpperCase();
			final int wildCard = templateKey.indexOf('%');
			//handle wildcards (always assume they end the line)
			if (wildCard >= 0)
			{
				templateKey = templateKey.substring(0, wildCard);
				for (PCTemplate aTemplate : character.getTemplateList())
				{
					if (aTemplate.getKeyName().toUpperCase().startsWith(
						templateKey))
					{
						runningTotal++;
					}
				}
			}
			else if (character.getTemplateKeyed(templateKey) != null)
			{
				runningTotal++;
			}
		}
		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "TEMPLATE"; //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character) throws PrerequisiteException
	{
		int runningTotal = 0;

		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
				"PreTemplate.error", prereq.toString())); //$NON-NLS-1$
		}

		PCGenGraph activeGraph = character.getActiveGraph();
		List<CDOMTemplate> list =
				activeGraph.getGrantedNodeList(CDOMTemplate.class);
		
		if (!list.isEmpty())
		{
			String templateKey = prereq.getKey().toUpperCase();
			int wildCard = templateKey.indexOf('%');
			//handle wildcards (always assume they end the line)
			if (wildCard >= 0)
			{
				templateKey = templateKey.substring(0, wildCard);
				for (CDOMTemplate aTemplate : list)
				{
					if (aTemplate.getKeyName().toUpperCase().startsWith(
						templateKey))
					{
						runningTotal++;
					}
				}
			}
			else
			{
				if (activeGraph.containsGranted(CDOMTemplate.class, templateKey))
				{
					runningTotal++;
				}
			}
		}
		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

}
