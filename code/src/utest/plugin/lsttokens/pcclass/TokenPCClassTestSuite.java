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
package plugin.lsttokens.pcclass;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AbbTokenTest.class, AddDomainsTokenTest.class,
		AttackCycleTokenTest.class, BonusSpellStatTokenTest.class,
		CRFormulaTokenTest.class, DeityTokenTest.class, DomainTokenTest.class,
		ExchangeLevelTokenTest.class, ExClassTokenTest.class,
		HDTokenTest.class, IsMonsterTokenTest.class, ItemCreateTokenTest.class,
		KnownSpellsFromSpecialtyTokenTest.class, KnownspellsTokenTest.class,
		LangbonusTokenTest.class, LevelsPerFeatTokenTest.class,
		MaxLevelTokenTest.class, MemorizeTokenTest.class,
		ModToSkillsTokenTest.class, MonNonSkillTHDTokenTest.class,
		MonSkillTokenTest.class, ProhibitedTokenTest.class,
		ProhibitspellTokenTest.class, SkillListTokenTest.class,
		SpellBookTokenTest.class, SpellListTokenTest.class,
		SpellStatTokenTest.class, SpellTypeTokenTest.class,
		StartSkillPtsTokenTest.class, VFeatTokenTest.class,
		VisibleTokenTest.class, WeaponbonusTokenTest.class,
		XtraFeatsTokenTest.class })
public class TokenPCClassTestSuite extends TestSuite
{
	// No contents, see annotations
}