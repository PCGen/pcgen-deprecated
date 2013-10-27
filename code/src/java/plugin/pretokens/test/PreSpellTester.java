/*
 * PreSpell.java
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.inst.CDOMSpell;
import pcgen.character.CharacterDataStore;
import pcgen.core.CharacterDomain;
import pcgen.core.Globals;
import pcgen.core.PCSpell;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 *
 */
public class PreSpellTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		int requiredNumber = 0;
		try
		{
			requiredNumber = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException e)
		{
			Logging
				.errorPrint(PropertyFactory
					.getString("PreSpell.error.badly_formed_attribute") + prereq.toString()); //$NON-NLS-1$
		}

		// Build a list of all possible spells
		final List<Spell> aArrayList =
				character.aggregateSpellList("Any", "", "", "", 0, 20); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		//Needs to add domain spells as well
		for (CharacterDomain aCD : character.getCharacterDomainList())
		{
			if ((aCD != null) && (aCD.getDomain() != null))
			{
				aArrayList.addAll(Globals.getSpellsIn(-1,
					"", aCD.getDomain().toString())); //$NON-NLS-1$
			}
		}

		//Are there Innate Spell-like abilities?
		if (character.getAutoSpells())
		{
			for (PCSpell spell : character.getRace().getSpellList())
			{
				aArrayList.add(Globals.getSpellKeyed(spell.toString()));
			}
		}

		final String spellName = prereq.getKey();
		int runningTotal = 0;

		for (Spell aSpell : aArrayList)
		{
			if (aSpell.getKeyName() != null && aSpell.getKeyName().equalsIgnoreCase(spellName))
			{
				runningTotal++;
			}
		}
		runningTotal =
				prereq.getOperator().compare(runningTotal, requiredNumber);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "SPELL"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		final Object[] args =
				new Object[]{prereq.getOperator().toDisplayString(),
					prereq.getOperand(), prereq.getKey()};
		return PropertyFactory.getFormattedString("PreSpell.toHtml", args); //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character) throws PrerequisiteException
	{
		int requiredNumber = 0;
		try
		{
			requiredNumber = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException e)
		{
			Logging
				.errorPrint(PropertyFactory
					.getString("PreSpell.error.badly_formed_attribute") + prereq.toString()); //$NON-NLS-1$
		}

		Set<CDOMSpell> spellSet = new HashSet<CDOMSpell>();
		
		// Build a list of all possible spells (innate & known)
		List<CDOMSpell> aArrayList =
				character.getActiveGraph().getGrantedNodeList(CDOMSpell.class);
		String spellName = prereq.getKey();

		for (CDOMSpell aSpell : aArrayList)
		{
			if (aSpell.getKeyName().equalsIgnoreCase(spellName))
			{
				spellSet.add(aSpell);
			}
		}

		int runningTotal =
				prereq.getOperator().compare(spellSet.size(), requiredNumber);
		return countedTotal(prereq, runningTotal);
	}

}
