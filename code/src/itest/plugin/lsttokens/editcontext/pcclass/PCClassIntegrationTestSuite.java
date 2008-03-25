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
package plugin.lsttokens.editcontext.pcclass;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	//AbbIntegrationTest.class,
	AddDomainsIntegrationTest.class,
	AttackCycleIntegrationTest.class,
	BonusSpellStatIntegrationTest.class,
	// CastIntegrationTest.class,
	CRFormulaIntegrationTest.class,
	DeityIntegrationTest.class,
	DomainIntegrationTest.class,
	ExchangeLevelIntegrationTest.class,
	ExClassIntegrationTest.class,
	HDIntegrationTest.class,
	// HitDieIntegrationTest.class,
	IsMonsterIntegrationTest.class,
	ItemCreateIntegrationTest.class,
	KnownSpellsFromSpecialtyIntegrationTest.class,
	KnownSpellsIntegrationTest.class,
	// KnownIntegrationTest.class,
	LangbonusIntegrationTest.class,
	LevelsPerFeatIntegrationTest.class,
	MaxLevelIntegrationTest.class,
	MemorizeIntegrationTest.class,
	ModToSkillsIntegrationTest.class,
	MonNonSkillHDIntegrationTest.class,
	MonSkillIntegrationTest.class,
	// ProhibitedIntegrationTest.class,
	// ProhibitspellIntegrationTest.class,
	// SkillListIntegrationTest.class,
	// SpecialtyKnownIntegrationTest.class,
	SpellBookIntegrationTest.class,
	// SpellListIntegrationTest.class,
	SpellStatIntegrationTest.class, SpellTypeIntegrationTest.class,
	StartSkillPtsIntegrationTest.class, 
	VFeatIntegrationTest.class, VisibleIntegrationTest.class,
	WeaponbonusIntegrationTest.class, 
	XtraFeatsIntegrationTest.class})
public class PCClassIntegrationTestSuite extends TestSuite
{
	// No contents, see annotations
}