package plugin.lsttokens.add;

import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.core.Ability;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AddLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;

public class FeatTokenTest extends AbstractAddTokenTestCase
{

	private AddLstToken aToken = new FeatToken();

	@Override
	protected AddLstToken getSubToken()
	{
		return aToken;
	}

	@Override
	protected Class<Ability> getSubTokenType()
	{
		return Ability.class;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	protected void construct(LoadContext loadContext, String one)
	{
		Ability ab =
				loadContext.ref.constructCDOMObject(getSubTokenType(), one);
		loadContext.ref.reassociateReference(AbilityCategory.FEAT, ab);
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}
}
