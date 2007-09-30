package plugin.lsttokens.oldchoose;

import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.core.Ability;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.choose.FeatListToken;

public class FeatListTokenTest extends AbstractChooseTokenTestCase
{

	static PCTemplateLoader loader = new PCTemplateLoader();

	static FeatListToken subToken = new FeatListToken();

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
	protected Class<Ability> getSubTokenType()
	{
		return Ability.class;
	}

	@Override
	protected void construct(LoadContext loadContext, String one)
	{
		Ability obj =
				loadContext.ref.constructCDOMObject(getSubTokenType(), one);
		loadContext.ref.reassociateReference(AbilityCategory.FEAT, obj);
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	protected boolean isAnyLegal()
	{
		return true;
	}

	@Override
	protected boolean isTypeLegal()
	{
		return true;
	}

	@Override
	protected char getJoinCharacter()
	{
		return '|';
	}

	@Override
	protected boolean isPrimitiveLegal()
	{
		return true;
	}

	@Override
	protected boolean requiresConstruction()
	{
		return true;
	}
}
