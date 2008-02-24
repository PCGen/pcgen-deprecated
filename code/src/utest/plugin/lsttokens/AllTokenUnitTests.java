/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import plugin.lsttokens.ability.TokenAbilityTestSuite;
import plugin.lsttokens.add.TokenAddTestSuite;
import plugin.lsttokens.auto.TokenAutoTestSuite;
import plugin.lsttokens.deity.TokenDeityTestSuite;
import plugin.lsttokens.domain.TokenDomainTestSuite;
import plugin.lsttokens.equipment.TokenEquipmentTestSuite;
import plugin.lsttokens.equipmentmodifier.TokenEqModTestSuite;
import plugin.lsttokens.pcclass.TokenPCClassTestSuite;
import plugin.lsttokens.pcclass.level.TokenPCClassLevelTestSuite;
import plugin.lsttokens.race.TokenRaceTestSuite;
import plugin.lsttokens.remove.TokenRemoveTestSuite;
import plugin.lsttokens.skill.TokenSkillTestSuite;
import plugin.lsttokens.spell.TokenSpellTestSuite;
import plugin.lsttokens.template.TokenTemplateTestSuite;
import plugin.lsttokens.weaponprof.TokenWeaponProfTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { TokenGlobalTestSuite.class, TokenAbilityTestSuite.class,
		TokenAddTestSuite.class, TokenAutoTestSuite.class,
		TokenDeityTestSuite.class, TokenDomainTestSuite.class,
		TokenEquipmentTestSuite.class, TokenEqModTestSuite.class,
		TokenPCClassTestSuite.class, TokenPCClassLevelTestSuite.class,
		TokenRaceTestSuite.class, TokenRemoveTestSuite.class,
		TokenSkillTestSuite.class, TokenSpellTestSuite.class,
		TokenTemplateTestSuite.class, TokenWeaponProfTestSuite.class })
public class AllTokenUnitTests extends TestSuite
{
	// No contents, see annotations
}