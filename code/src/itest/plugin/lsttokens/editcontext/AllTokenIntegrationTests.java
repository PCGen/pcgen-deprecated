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
package plugin.lsttokens.editcontext;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import plugin.lsttokens.editcontext.ability.AbilityIntegrationTestSuite;
import plugin.lsttokens.editcontext.deity.DeityIntegrationTestSuite;
import plugin.lsttokens.editcontext.domain.DomainIntegrationTestSuite;
import plugin.lsttokens.editcontext.equipment.EquipmentIntegrationTestSuite;
import plugin.lsttokens.editcontext.equipmentmodifier.EqModIntegrationTestSuite;
import plugin.lsttokens.editcontext.pcclass.PCClassIntegrationTestSuite;
import plugin.lsttokens.editcontext.race.RaceIntegrationTestSuite;
import plugin.lsttokens.editcontext.skill.SkillIntegrationTestSuite;
import plugin.lsttokens.editcontext.spell.SpellIntegrationTestSuite;
import plugin.lsttokens.editcontext.template.TemplateIntegrationTestSuite;
import plugin.lsttokens.editcontext.weaponprof.WeaponProfIntegrationTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	GlobalIntegrationTestSuite.class,
	AbilityIntegrationTestSuite.class,
	// AddIntegrationTestSuite.class,
	//AutoIntegrationTestSuite.class,
	DeityIntegrationTestSuite.class,
	DomainIntegrationTestSuite.class, EquipmentIntegrationTestSuite.class,
	EqModIntegrationTestSuite.class, PCClassIntegrationTestSuite.class,
	RaceIntegrationTestSuite.class, SkillIntegrationTestSuite.class,
	SpellIntegrationTestSuite.class, TemplateIntegrationTestSuite.class,
	WeaponProfIntegrationTestSuite.class})
public class AllTokenIntegrationTests extends TestSuite
{
	// No contents, see annotations
}