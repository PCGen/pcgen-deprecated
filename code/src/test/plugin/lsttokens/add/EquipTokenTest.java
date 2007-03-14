package plugin.lsttokens.add;

import pcgen.core.Equipment;
import pcgen.core.PCTemplate;
import pcgen.persistence.lst.AddLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;

public class EquipTokenTest extends AbstractAddTokenTestCase
{

	private AddLstToken aToken = new EquipToken();

	@Override
	protected AddLstToken getSubToken()
	{
		return aToken;
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

	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}
}
