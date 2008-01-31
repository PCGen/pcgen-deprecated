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
package plugin.lsttokens.equipment;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AcCheckTokenTest.class, AltCritMultTokenTest.class,
		AltCritRangeTokenTest.class, AltDamageTokenTest.class,
		AltEqModTokenTest.class, AltTypeTokenTest.class,
		BaseItemTokenTest.class, BaseqtyTokenTest.class,
		ContainsTokenTest.class, CostTokenTest.class, CritMultTokenTest.class,
		CritRangeTokenTest.class, DamageTokenTest.class, EdrTokenTest.class,
		EqmodTokenTest.class, FumbleRangeTokenTest.class, HandsTokenTest.class,
		MaxDexTokenTest.class, ModsTokenTest.class, NumPagesTokenTest.class,
		PageUsageTokenTest.class, ProficiencyTokenTest.class,
		QualityTokenTest.class, RangeTokenTest.class,
		RateOfFireTokenTest.class, ReachMultTokenTest.class,
		ReachTokenTest.class, SizeTokenTest.class, SlotsTokenTest.class,
		SpellfailureTokenTest.class, SPropTokenTest.class,
		WeildTokenTest.class, WtTokenTest.class })
public class TokenEquipmentTestSuite extends TestSuite
{
	// No contents, see annotations
}