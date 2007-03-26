package plugin.lsttokens;

import pcgen.core.PCTemplate;
import pcgen.core.Skill;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalListTokenTestCase;

public class CCSkillLstTest extends AbstractGlobalListTokenTestCase<Skill>
{

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public Class<Skill> getTargetClass()
	{
		return Skill.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}

	static GlobalLstToken token = new CcskillLst();
	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}
}
