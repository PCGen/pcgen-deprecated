package plugin.lsttokens.oldchoose;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.choose.ArmorProfToken;

public class ArmorProfTokenTest extends AbstractChooseTokenTestCase
{

	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	@Before
	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		this.setPrefix("|1");
	}

	static ArmorProfToken subToken = new ArmorProfToken();

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

	@Test
	public void testInvalidCountNaN() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|x|String"));
	}

}
