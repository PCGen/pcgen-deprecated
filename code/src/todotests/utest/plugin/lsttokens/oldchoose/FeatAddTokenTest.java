package utest.plugin.lsttokens.oldchoose;

import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.choose.FeatAddToken;

public class FeatAddTokenTest extends AbstractChooseTokenTestCase
{

	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	static FeatAddToken subToken = new FeatAddToken();

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
	protected Class<CDOMAbility> getSubTokenType()
	{
		return CDOMAbility.class;
	}

	@Override
	protected void construct(LoadContext loadContext, String one)
	{
		CDOMAbility obj =
				loadContext.ref.constructCDOMObject(getSubTokenType(), one);
		loadContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, obj);
	}

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
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
