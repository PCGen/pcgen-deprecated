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
package plugin.lsttokens.editcontext.equipment;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AccheckIntegrationTest.class,
	AltCritMultIntegrationTest.class, AltCritRangeIntegrationTest.class,
	AltDamageIntegrationTest.class, AltEqModIntegrationTest.class,
	AltTypeIntegrationTest.class, BaseItemIntegrationTest.class,
	BaseqtyIntegrationTest.class, ContainsIntegrationTest.class,
	CostIntegrationTest.class, CritMultIntegrationTest.class,
	CritRangeIntegrationTest.class, DamageIntegrationTest.class,
	EdrIntegrationTest.class, EqmodIntegrationTest.class,
	FumbleRangeIntegrationTest.class, HandsIntegrationTest.class,
	MaxDexIntegrationTest.class, ModsIntegrationTest.class,
	NumPagesIntegrationTest.class, PageUsageIntegrationTest.class,
	ProficiencyIntegrationTest.class, QualityIntegrationTest.class,
	RangeIntegrationTest.class, RateOfFireIntegrationTest.class,
	ReachMultIntegrationTest.class, ReachIntegrationTest.class,
	SizeIntegrationTest.class, SlotsIntegrationTest.class,
	SpellFailureIntegrationTest.class, SPropIntegrationTest.class,
	WeildIntegrationTest.class, WtIntegrationTest.class})
public class EquipmentIntegrationTestSuite extends TestSuite
{
	// No contents, see annotations
}