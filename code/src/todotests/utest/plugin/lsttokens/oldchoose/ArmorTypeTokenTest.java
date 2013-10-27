package utest.plugin.lsttokens.oldchoose;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.choose.ArmorTypeToken;

public class ArmorTypeTokenTest extends AbstractEmptyChooseTokenTestCase
{
	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	static ArmorTypeToken subToken = new ArmorTypeToken();

	@Override
	protected ChooseLstToken getSubToken()
	{
		return subToken;
	}

	@Override
	protected <T extends CDOMObject> Class<T> getSubTokenType()
	{
		return null;
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
