package plugin.lsttokens;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalStringTokenTestCase;

public class OutputNameLstTest extends AbstractGlobalStringTokenTestCase
{
	static GlobalLstToken token = new OutputnameLst();
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

	@Override
	public StringKey getStringKey()
	{
		return StringKey.OUTPUT_NAME;
	}
}
