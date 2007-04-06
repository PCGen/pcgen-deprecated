package plugin.lsttokens.spell;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CastTimeTokenTest.class, ClassesTokenTest.class,
	CompsTokenTest.class, CostTokenTest.class, CtTokenTest.class,
	DescriptorTokenTest.class, DomainsTokenTest.class, DurationTokenTest.class,
	PPCostTokenTest.class, RangeTokenTest.class, SaveInfoTokenTest.class,
	SchoolTokenTest.class, SpellResTokenTest.class, StatTokenTest.class,
	SubschoolTokenTest.class, TargetAreaTokenTest.class,
	VariantsTokenTest.class, XPCostTokenTest.class})
public class TokenSpellTestSuite extends TestSuite
{
	// No contents, see annotations
}
