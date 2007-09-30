package plugin.lsttokens.oldchoose;

import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.choose.SpellClassesToken;

public class SpellClassesTokenTest extends AbstractEmptyChooseTokenTestCase
{
	static PCTemplateLoader loader = new PCTemplateLoader();

	static SpellClassesToken subToken = new SpellClassesToken();

	@Override
	protected ChooseLstToken getSubToken()
	{
		return subToken;
	}

	@Override
	protected <T extends PObject> Class<T> getSubTokenType()
	{
		return null;
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
