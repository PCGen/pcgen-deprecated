package plugin.lsttokens.oldchoose;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.Equipment;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.choose.ArmorProfToken;

public class ArmorProfTokenTest extends AbstractChooseTokenTestCase
{

	static PCTemplateLoader loader = new PCTemplateLoader();

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
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
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
