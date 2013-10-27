/*
 * SpellMemTokenTest.java
 * Copyright 2005 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Oct 8, 2005
 *
 * $Id$
 *
 */
package pcgen.io.exporttoken;

import java.util.Arrays;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import plugin.exporttokens.SpellMemToken;

/**
 * Verify the correct functioning of the SPELLMEM token.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class SpellMemTokenTest extends AbstractCharacterTestCase
{
	private PCClass arcaneClass = null;
	private PCClass divineClass = null;
	private Race human = null;
	private Spell testSpell = null;

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(SpellMemTokenTest.class);
	}

	/**
	 * Basic constructor, name only.
	 * @param name The name of the test class.
	 */
	public SpellMemTokenTest(String name)
	{
		super(name);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();

		// Human
		human = new Race();
		final BonusObj bon = Bonus.newBonus("FEAT|POOL|2");
		human.setBonusInitialFeats(bon);

		testSpell = new Spell();
		testSpell.setName("Test Spell");
		testSpell.setKeyName("TEST_SPELL");
		testSpell.setLevelInfo("CLASS|TestArcane", 1);
		testSpell.setLevelInfo("DOMAIN|Fire", 0);
		testSpell.setLevelInfo("CLASS|TestDivine", 0);
		Globals.addToSpellMap(testSpell.getKeyName(), testSpell);

		arcaneClass = new PCClass();
		arcaneClass.setName("TestArcane");
		arcaneClass.setKeyName("KEY_TEST_ARCANE");
		arcaneClass.setAbbrev("TA");
		arcaneClass.setSpellType("ARCANE");
		arcaneClass.setSpellBaseStat("CHA");
		arcaneClass.setSpellBookUsed(false);
		arcaneClass.setMemorizeSpells(false);
		arcaneClass.setKnown(1, Arrays.asList("4,2,1".split(",")));
		arcaneClass.setCast(1, Arrays.asList("3,1,0".split(",")));
		Globals.getClassList().add(arcaneClass);
		CharacterSpell aCharacterSpell =
				new CharacterSpell(arcaneClass, testSpell);
		aCharacterSpell.addInfo(1, 1, null);
		arcaneClass.getSpellSupport().addCharacterSpell(aCharacterSpell);

		divineClass = new PCClass();
		divineClass.setName("TestDivine");
		divineClass.setKeyName("KEY_TEST_DIVINE");
		divineClass.setAbbrev("TD");
		divineClass.setSpellType("DIVINE");
		divineClass.setSpellBaseStat("WIS");
		divineClass.setSpellBookUsed(false);
		divineClass.setMemorizeSpells(true);
		divineClass.setCast(1, Arrays.asList("3,1,0".split(",")));
		Globals.getClassList().add(divineClass);
		aCharacterSpell = new CharacterSpell(divineClass, testSpell);
		aCharacterSpell.addInfo(1, 1, null);
		divineClass.getSpellSupport().addCharacterSpell(aCharacterSpell);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		Globals.getClassList().remove(divineClass);
		Globals.getClassList().remove(arcaneClass);

		super.tearDown();
	}

	/**
	 * Test the SPELLMEM tag for a spontaneous caster. Checks that the
	 * list of known spells is auto populated and that the spell can be
	 * retrieved correctly.
	 */
	public void testSpontaneousCasterKnown()
	{
		SettingsHandler.setMonsterDefault(false);
		PlayerCharacter character = getCharacter();
		String spellBook = "Travel";
		character.setRace(human);
		character.incrementClassLevel(1, arcaneClass, true);
		character.addSpellBook(spellBook);
		List<CharacterSpell> spellList =
				arcaneClass.getSpellSupport().getCharacterSpell(testSpell, "",
					1);
		CharacterSpell charSpell = spellList.get(0);

		String result =
				character.addSpell(charSpell, null, arcaneClass.getKeyName(),
					Globals.getDefaultSpellBook(), 1, 1);
		assertEquals("No CHA, so should reject attempt to add spell",
			"You can only learn 0 spells for level 1"
				+ "\nand there are no higher-level slots available.", result);

		SpellMemToken token = new SpellMemToken();
		assertEquals("Retrieve spell from known list of arcane caster.",
			"Test Spell", token.getToken("SPELLMEM.0.0.1.0.NAME", character,
				null));
	}

	/**
	 * Test the SPELLMEM tag for a spontaneous caster. Checks that the
	 * list of known spells is auto populated and that a spell can be added to
	 * a prepared list, and that the spell can be retrieved correctly from both
	 * books.
	 */
	public void testPreparedCaster()
	{
		SettingsHandler.setMonsterDefault(false);
		PlayerCharacter character = getCharacter();
		String spellBook = "Travel";
		character.setRace(human);
		character.incrementClassLevel(1, divineClass, true);
		character.addSpellBook(spellBook);
		List<CharacterSpell> spellList =
				divineClass.getSpellSupport().getCharacterSpell(testSpell, "",
					1);
		CharacterSpell charSpell = spellList.get(0);

		String result =
				character.addSpell(charSpell, null, divineClass.getKeyName(),
					Globals.getDefaultSpellBook(), 1, 1);
		assertEquals("Known spells already has all spells, should reject.",
			"The Known Spells spellbook contains all spells of this level that you "
				+ "know. You cannot place spells in multiple times.", result);
		result =
				character.addSpell(charSpell, null, divineClass.getKeyName(),
					spellBook, 1, 1);
		assertEquals("No WIS, so should reject attempt to add spell",
			"You can only prepare 0 spells for level 1"
				+ "\nand there are no higher-level slots available.", result);

		setPCStat(character, "WIS", 12);
		character.calcActiveBonuses();
		result =
				character.addSpell(charSpell, null, divineClass.getKeyName(),
					spellBook, 1, 1);
		assertEquals("Should be no error messages from adding spell", "",
			result);

		SpellMemToken token = new SpellMemToken();
		assertEquals("Retrieve spell from known list of divine caster.",
			"Test Spell", token.getToken("SPELLMEM.0.0.1.0.NAME", character,
				null));
		assertEquals("Retrieve spell from prepared list of divine caster.",
			"Test Spell", token.getToken("SPELLMEM.0.2.1.0.NAME", character,
				null));
	}
}
