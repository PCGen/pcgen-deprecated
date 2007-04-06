package plugin.lsttokens;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

import plugin.lsttokens.ability.TokenAbilityTestSuite;
import plugin.lsttokens.add.TokenAddTestSuite;
import plugin.lsttokens.auto.TokenAutoTestSuite;
import plugin.lsttokens.deity.TokenDeityTestSuite;
import plugin.lsttokens.domain.TokenDomainTestSuite;
import plugin.lsttokens.equipment.TokenEquipmentTestSuite;
import plugin.lsttokens.equipmentmodifier.TokenEqModTestSuite;
import plugin.lsttokens.race.TokenRaceTestSuite;
import plugin.lsttokens.skill.TokenSkillTestSuite;
import plugin.lsttokens.spell.TokenSpellTestSuite;
import plugin.lsttokens.template.TokenTemplateTestSuite;
import plugin.lsttokens.weaponprof.TokenWeaponProfTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TokenAbilityTestSuite.class, TokenAddTestSuite.class,
	TokenAutoTestSuite.class, TokenDeityTestSuite.class,
	TokenDomainTestSuite.class, TokenEquipmentTestSuite.class,
	TokenEqModTestSuite.class, TokenRaceTestSuite.class,
	TokenSkillTestSuite.class, TokenSpellTestSuite.class,
	TokenTemplateTestSuite.class, TokenWeaponProfTestSuite.class})
public class AllTokenUnitTests extends TestSuite
{
	// No contents, see annotations
}