package plugin.lsttokens.oldchoose;

import pcgen.core.PCTemplate;
import pcgen.core.Skill;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.choose.SkillsToken;

public class SkillsTokenTest extends AbstractEmptyChooseTokenTestCase
{
	static PCTemplateLoader loader = new PCTemplateLoader();

	static SkillsToken subToken = new SkillsToken();

	@Override
	protected ChooseLstToken getSubToken()
	{
		return subToken;
	}

	@Override
	protected Class<Skill> getSubTokenType()
	{
		return Skill.class;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

}
