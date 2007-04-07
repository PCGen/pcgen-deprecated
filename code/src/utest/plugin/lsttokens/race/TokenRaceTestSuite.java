package plugin.lsttokens.race;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CRTokenTest.class, FaceTokenTest.class,
	FavoredClassTokenTest.class, FeatTokenTest.class, HandsTokenTest.class,
	HitDiceAdvancementTokenTest.class, HitDieTokenTest.class,
	LangbonusTokenTest.class, LangNumTokenTest.class, LegsTokenTest.class,
	LevelAdjustmentTokenTest.class, MFeatTokenTest.class,
	MonCCSkillTokenTest.class, MonCSkillTokenTest.class,
	MonsterClassTokenTest.class, RaceSubtypeTokenTest.class,
	RaceTypeTokenTest.class, ReachTokenTest.class, SizeTokenTest.class,
	SkillMultTokenTest.class, StartFeatsTokenTest.class,
	WeaponbonusTokenTest.class, XtraSkillPointsPerLevelTokenTest.class})
public class TokenRaceTestSuite extends TestSuite
{
	// No contents, see annotations
}