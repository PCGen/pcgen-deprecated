package plugin.lsttokens.template;

import org.junit.Test;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.AbstractIntegerTokenTestCase;

public class LevelsPerFeatTokenTest extends
		AbstractIntegerTokenTestCase<PCTemplate>
{

	static LevelsperfeatToken token = new LevelsperfeatToken();
	static PCTemplateLoader loader = new PCTemplateLoader();

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

	@Override
	public CDOMToken<PCTemplate> getToken()
	{
		return token;
	}

	@Override
	public IntegerKey getIntegerKey()
	{
		return IntegerKey.LEVELS_PER_FEAT;
	}

	@Override
	public boolean isNegativeAllowed()
	{
		return false;
	}

	@Override
	public boolean isZeroAllowed()
	{
		return true;
	}

	@Override
	public boolean isPositiveAllowed()
	{
		return true;
	}

	@Test
	public void dummyTest()
	{
		//Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}
}
