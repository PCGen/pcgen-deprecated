package plugin.lsttokens.oldchoose;

import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.choose.ShieldProfToken;

public class ShieldProfTokenTest extends AbstractChooseTokenTestCase
{

	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	static ShieldProfToken subToken = new ShieldProfToken();

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

	@Override
	protected boolean isAnyLegal()
	{
		return false;
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
