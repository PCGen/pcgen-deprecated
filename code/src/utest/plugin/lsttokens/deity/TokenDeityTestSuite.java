package plugin.lsttokens.deity;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AlignTokenTest.class, AppearanceTokenTest.class,
	DeityWeapTokenTest.class, DomainsTokenTest.class,
	FollowerAlignTokenTest.class, PantheonTokenTest.class, RaceTokenTest.class,
	SymbolTokenTest.class, TitleTokenTest.class, WorshippersTokenTest.class})
public class TokenDeityTestSuite extends TestSuite
{
	// No contents, see annotations
}
