/*
 * PreArmorTypeTest.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package pcgen.core.prereq;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Ability;
import pcgen.core.AbilityUtilities;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;

/**
 * <code>PreArmorTypeTest</code> tests that the PREARMORTYPE tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public class PreArmorTypeTest extends AbstractCharacterTestCase
{
	/*
	 * Class under test for int passes(Prerequisite, PlayerCharacter)
	 */
	public void testPassesPrerequisitePlayerCharacter()
	{
		final PlayerCharacter character = getCharacter();

		final Equipment armor = new Equipment();
		armor.setName("Leather");
		armor.typeList().add("ARMOR");

		character.addEquipment(armor);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("armortype");
		prereq.setKey("CHAINMAIL");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertFalse("Doesn't have chainmail equipped", PrereqHandler.passes(
			prereq, character, null));

		armor.setName("Chainmail");

		assertFalse("Chainmail is not equipped", PrereqHandler.passes(prereq,
			character, null));

		armor.setIsEquipped(true, character);

		assertTrue("Chainmail is equipped", PrereqHandler.passes(prereq,
			character, null));

		armor.setName("Chainmail (Masterwork)");

		assertFalse("Should be an exact match only", PrereqHandler.passes(
			prereq, character, null));

		prereq.setKey("CHAINMAIL%");

		assertTrue("Should be allow wildcard match", PrereqHandler.passes(
			prereq, character, null));
	}

	/**
	 * Test armor type tests
	 * @throws Exception
	 */
	public void testType() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Equipment armor = new Equipment();
		armor.setName("Chainmail");

		character.addEquipment(armor);
		armor.setIsEquipped(true, character);

		Prerequisite prereq = new Prerequisite();
		prereq.setKind("armortype");
		prereq.setKey("TYPE=Medium");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertFalse("Equipment has no type", PrereqHandler.passes(prereq,
			character, null));

		armor.typeList().add("ARMOR");
		armor.typeList().add("MEDIUM");

		assertTrue("Armor is medium", PrereqHandler.passes(prereq, character,
			null));

		prereq.setKey("TYPE.Heavy");

		assertFalse("Armor is not heavy", PrereqHandler.passes(prereq,
			character, null));

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREARMORTYPE:2,TYPE=Medium,Full%");

		assertFalse("Armor is not Full something", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREARMORTYPE:2,TYPE=Medium,Chain%");
		assertTrue("Armor is medium and Chain", PrereqHandler.passes(prereq,
			character, null));
	}

	/**
	 * Test LIST
	 * @throws Exception
	 */
	public void testList() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Ability mediumProf =
				TestHelper.makeAbility("Armor Proficiency (Medium)", "FEAT",
					"General");
		mediumProf.addAutoArray("ARMORPROF", "TYPE.Medium");
		AbilityUtilities.modFeat(character, null,
			"KEY_Armor Proficiency (Medium)", true, false);

		final Equipment chainmail = new Equipment();
		chainmail.typeList().add("ARMOR");
		chainmail.typeList().add("MEDIUM");
		chainmail.setName("Chainmail");
		EquipmentList.addEquipment(chainmail);

		Prerequisite prereq = new Prerequisite();
		prereq.setKind("armortype");
		prereq.setKey("LIST");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertFalse("No armor equipped", PrereqHandler.passes(prereq,
			character, null));

		character.addEquipment(chainmail);
		chainmail.setIsEquipped(true, character);

		assertTrue("Proficient armor equipped", PrereqHandler.passes(prereq,
			character, null));

		chainmail.setIsEquipped(false, character);

		final Equipment fullPlate = new Equipment();
		fullPlate.typeList().add("ARMOR");
		fullPlate.typeList().add("HEAVY");
		fullPlate.setName("Full Plate");
		EquipmentList.addEquipment(fullPlate);

		fullPlate.setIsEquipped(false, character);

		assertFalse("Not Proficient in armor equipped", PrereqHandler.passes(
			prereq, character, null));
	}
}
