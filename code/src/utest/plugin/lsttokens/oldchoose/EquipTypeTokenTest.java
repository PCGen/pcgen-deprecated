package plugin.lsttokens.oldchoose;

import pcgen.core.Domain;
import pcgen.core.PCTemplate;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.choose.EquipTypeToken;

public class EquipTypeTokenTest extends AbstractChooseTokenTestCase
{

	static PCTemplateLoader loader = new PCTemplateLoader();

	static EquipTypeToken subToken = new EquipTypeToken();

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
	protected Class<Domain> getSubTokenType()
	{
		return Domain.class;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	protected boolean isAnyLegal()
	{
		return false;
	}

	@Override
	protected boolean isPrimitiveLegal()
	{
		return true;
	}
	
	

	@Override
	protected boolean isTypeLegal()
	{
		return false;
	}

	@Override
	protected char getJoinCharacter()
	{
		return '.';
	}

	@Override
	protected boolean requiresConstruction()
	{
		return false;
	}
}
