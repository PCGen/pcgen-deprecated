package plugin.lsttokens.oldchoose;

import org.junit.Test;

import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.choose.DomainToken;

public class DomainTokenTest extends AbstractChooseTokenTestCase
{

	static PCTemplateLoader loader = new PCTemplateLoader();

	static DomainToken subToken = new DomainToken();

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
		return true;
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
		return ',';
	}

	@Override
	protected boolean requiresConstruction()
	{
		return true;
	}

	@Test
	public void testInvalidInputDeityEmpty() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + "|DEITY="));
			assertTrue(primaryGraph.isEmpty());
		}
	}

	@Test
	public void testRoundRobinDeity() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Deity.class, "Zeus");
		secondaryContext.ref.constructCDOMObject(Deity.class, "Zeus");
		runRoundRobin(getSubTokenString() + "|DEITY=Zeus");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinQualify() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString() + "|QUALIFY");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinPC() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString() + "|PC");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString() + "|PC,QUALIFY,TestWP1");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}
}
