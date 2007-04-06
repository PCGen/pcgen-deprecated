package plugin.lsttokens.auto;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ArmorProfTokenTest.class, ShieldProfTokenTest.class,
	WeaponProfTokenTest.class})
public class TokenAutoTestSuite extends TestSuite
{
	// No contents, see annotations
}