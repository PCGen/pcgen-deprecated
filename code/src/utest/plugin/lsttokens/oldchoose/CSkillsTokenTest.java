package plugin.lsttokens.oldchoose;

import pcgen.core.Equipment;
import pcgen.core.PCTemplate;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.choose.CSkillsToken;

public class CSkillsTokenTest extends AbstractEmptyChooseTokenTestCase
{

	static PCTemplateLoader loader = new PCTemplateLoader();

	static CSkillsToken subToken = new CSkillsToken();

	@Override
	protected ChooseLstToken getSubToken()
	{
		return subToken;
	}

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	protected Class<Equipment> getSubTokenType()
	{
		return Equipment.class;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}
}
