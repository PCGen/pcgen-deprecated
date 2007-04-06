package plugin.lsttokens.skill;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ACheckTokenTest.class, ClassesTokenTest.class,
	ExclusiveTokenTest.class, KeystatTokenTest.class,
	UseuntrainedTokenTest.class, VisibleTokenTest.class})
public class TokenSkillTestSuite extends TestSuite
{
	// No contents, see annotations
}