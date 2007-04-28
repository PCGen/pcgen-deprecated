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
package plugin.lsttokens.template;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AddLevelTokenTest.class, BonusFeatsTokenTest.class,
	BonusSkillPointsTokenTest.class, CRTokenTest.class, FaceTokenTest.class,
	FavoredClassTokenTest.class, FeatTokenTest.class,
	GenderLockTokenTest.class, HandsTokenTest.class, HDTokenTest.class,
	HitDieTokenTest.class, LangbonusTokenTest.class, LegsTokenTest.class,
	LevelAdjustmentTokenTest.class, LevelsPerFeatTokenTest.class,
	LevelTokenTest.class, NonPPTokenTest.class, RaceSubtypeTokenTest.class,
	RaceTypeTokenTest.class, ReachTokenTest.class, RegionTokenTest.class,
	RemovableTokenTest.class, RepeatLevelTokenTest.class, SizeTokenTest.class,
	SubraceTokenTest.class, SubregionTokenTest.class, VisibleTokenTest.class,
	WeaponbonusTokenTest.class})
public class TokenTemplateTestSuite extends TestSuite
{
	// No content, see annotations
}
