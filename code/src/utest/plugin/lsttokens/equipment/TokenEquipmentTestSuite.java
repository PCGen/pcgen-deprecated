package plugin.lsttokens.equipment;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AcCheckTokenTest.class, AltCritMultTokenTest.class,
	AltCritRangeTokenTest.class, AltDamageTokenTest.class,
	AltEqModTokenTest.class, AltTypeTokenTest.class, BaseItemTokenTest.class,
	BaseqtyTokenTest.class, ContainsTokenTest.class, CostTokenTest.class,
	CritMultTokenTest.class, CritRangeTokenTest.class, DamageTokenTest.class,
	EdrTokenTest.class, EqmodTokenTest.class, FumbleRangeTokenTest.class,
	HandsTokenTest.class, MaxDexTokenTest.class, ModsTokenTest.class,
	NumPagesTokenTest.class, PageUsageTokenTest.class,
	ProficiencyTokenTest.class, QualityTokenTest.class, RangeTokenTest.class,
	RateOfFireTokenTest.class, ReachMultTokenTest.class, ReachTokenTest.class,
	SlotsTokenTest.class, SpellfailureTokenTest.class, SPropTokenTest.class,
	WeildTokenTest.class, WtTokenTest.class})
public class TokenEquipmentTestSuite extends TestSuite
{
	// No contents, see annotations
}