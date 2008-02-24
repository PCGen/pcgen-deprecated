package plugin.lsttokens.oldchoose;

import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.choose.CSkillsToken;

public class CSkillsTokenTest extends AbstractEmptyChooseTokenTestCase
{

	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	static CSkillsToken subToken = new CSkillsToken();

	@Override
	protected ChooseLstToken getSubToken()
	{
		return subToken;
	}

	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	@Override
	protected Class<CDOMEquipment> getSubTokenType()
	{
		return CDOMEquipment.class;
	}

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}
}
