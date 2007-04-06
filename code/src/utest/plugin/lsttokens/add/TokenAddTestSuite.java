package plugin.lsttokens.add;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AbilityTokenTest.class, ClassSkillsTokenTest.class,
	EquipTokenTest.class, FeatTokenTest.class, LanguageTokenTest.class,
	SATokenTest.class, SkillTokenTest.class, SpellCasterTokenTest.class,
	SpellLevelTokenTest.class, TemplateTokenTest.class, VFeatTokenTest.class})
public class TokenAddTestSuite extends TestSuite
{
	// No contents, see annotations
}
