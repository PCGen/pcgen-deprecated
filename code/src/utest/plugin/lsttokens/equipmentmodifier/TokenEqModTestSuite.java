package plugin.lsttokens.equipmentmodifier;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AssignToAllTokenTest.class, ChargesTokenTest.class,
	CostdoubleTokenTest.class, CostpreTokenTest.class, CostTokenTest.class,
	FormatcatTokenTest.class, FumblerangeTokenTest.class, ItypeTokenTest.class,
	NameoptTokenTest.class, PlusTokenTest.class, ReplacesTokenTest.class,
	SPropTokenTest.class, VisibleTokenTest.class})
public class TokenEqModTestSuite extends TestSuite
{
	// No contents, see annotations
}
