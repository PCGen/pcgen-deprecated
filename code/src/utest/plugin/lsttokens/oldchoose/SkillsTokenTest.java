package plugin.lsttokens.oldchoose;

import pcgen.cdom.inst.CDOMSkill;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.choose.SkillsToken;

public class SkillsTokenTest extends AbstractEmptyChooseTokenTestCase
{
	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	static SkillsToken subToken = new SkillsToken();

	@Override
	protected ChooseLstToken getSubToken()
	{
		return subToken;
	}

	@Override
	protected Class<CDOMSkill> getSubTokenType()
	{
		return CDOMSkill.class;
	}

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

}
