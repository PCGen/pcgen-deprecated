package plugin.lsttokens.oldchoose;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.choose.SpellsToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

public class SpellsTokenTest extends AbstractGlobalTokenTestCase
{

	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

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
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
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
		assertNoSideEffects();
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
		assertNoSideEffects();
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
