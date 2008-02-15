/*
 * PreLanguage.java
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
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.character.CharacterDataStore;
import pcgen.core.Globals;
import pcgen.core.Language;
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
public class PreLanguageTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
		throws PrerequisiteException
	{
		final String requiredLang = prereq.getKey();
		final int requiredNumber = Integer.parseInt(prereq.getOperand());
		int runningTotal = 0;

		if (prereq.getKey().equalsIgnoreCase("ANY")) { //$NON-NLS-1$
			runningTotal = character.getLanguagesList().size();
		}
		else
		{
			final Language aLang = Globals.getLanguageKeyed(requiredLang);
			if (aLang != null)
			{
				if (character.getLanguagesList().contains(aLang))
				{
					runningTotal = 1;
				}
			}
			else if (!"ANY".equals(requiredLang)) //$NON-NLS-1$
			{
				throw new PrerequisiteException(PropertyFactory
					.getFormattedString(
						"PreLanguage.error.no_such_language", requiredLang)); //$NON-NLS-1$
			}
		}

		runningTotal =
				prereq.getOperator().compare(runningTotal, requiredNumber);
		return countedTotal(prereq, runningTotal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "LANG"; //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character)
		throws PrerequisiteException
	{
		final String requiredLang = prereq.getKey();
		final int requiredNumber = Integer.parseInt(prereq.getOperand());
		int runningTotal = 0;

		PCGenGraph activeGraph = character.getActiveGraph();

		if (requiredLang.equalsIgnoreCase("ANY")) { //$NON-NLS-1$
			runningTotal = activeGraph.getGrantedNodeCount(Language.class);
		}
		else if (requiredLang.startsWith("TYPE.")
			|| requiredLang.startsWith("TYPE="))
		{
			List<Language> list =
					activeGraph.getGrantedNodeList(Language.class);
			if (list != null)
			{
				LANG: for (Language lang : list)
				{
					StringTokenizer tok =
							new StringTokenizer(requiredLang.substring(5), ".");
					// Must match all listed types in order to qualify
					while (tok.hasMoreTokens())
					{
						Type requiredType = Type.getConstant(tok.nextToken());
						if (!lang.containsInList(ListKey.TYPE, requiredType))
						{
							continue LANG;
						}
					}
					runningTotal++;
				}
			}
		}
		else
		{
			if (activeGraph.containsGranted(Language.class, requiredLang))
			{
				runningTotal = 1;
			}
		}

		runningTotal =
				prereq.getOperator().compare(runningTotal, requiredNumber);
		return countedTotal(prereq, runningTotal);
	}

}
