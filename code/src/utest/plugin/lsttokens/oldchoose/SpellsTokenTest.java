package plugin.lsttokens.oldchoose;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.choose.SpellsToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

public class SpellsTokenTest extends AbstractGlobalTokenTestCase
{

	static PCTemplateLoader loader = new PCTemplateLoader();

	static ChooseLst token = new ChooseLst();

	static SpellsToken subToken = new SpellsToken();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(subToken);
	}

	private String getSubTokenString()
	{
		return "SPELLS";
	}

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}

	@Test
	public void testInvalidNoPrefix() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|String"));
	}

	@Test
	public void testInvalidInputClassEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|CLASS="));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinTestClassEquals()
		throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString() + "|CLASS=Wizard");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputDomainEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|DOMAIN="));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinTestDomainEquals()
		throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString() + "|DOMAIN=Fire");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	// TODO is any separator legal, and if so, how does it work?
}
