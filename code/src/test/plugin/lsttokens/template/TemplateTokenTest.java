package plugin.lsttokens.template;

import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.AbstractListTokenTestCase;

public class TemplateTokenTest extends
		AbstractListTokenTestCase<PCTemplate, PCTemplate>
{

	static TemplateToken token = new TemplateToken();
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
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public Class<PCTemplate> getTargetClass()
	{
		return PCTemplate.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return false;
	}

	@Test
	public void testInvalidEmptyChoose()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "CHOOSE:"));
	}

	@Test
	public void testInvalidChooseListStart()
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		assertFalse(token.parse(primaryContext, primaryProf, "CHOOSE:|TestWP1"));
	}

	@Test
	public void testInvalidChooseDoubleJoin()
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP2");
		assertFalse(token.parse(primaryContext, primaryProf, "CHOOSE:TestWP2||TestWP1"));
	}

	@Test
	public void testInvalidChooseListEnd()
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		assertFalse(token.parse(primaryContext, primaryProf, "CHOOSE:TestWP1|"));
	}

	@Test
	public void testInvalidEmptyAddChoice()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "ADDCHOICE:"));
	}

	@Test
	public void testValidChooseInputs()
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP2");
		assertTrue(token.parse(primaryContext, primaryProf, "CHOOSE:TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(token.parse(primaryContext, primaryProf,
			"CHOOSE:TestWP1|TestWP2"));
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinChooseOne() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP2");
		secondaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		secondaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP2");
		runRoundRobin("CHOOSE:TestWP1");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinChooseThree() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP2");
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP3");
		secondaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		secondaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP2");
		secondaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP3");
		runRoundRobin("CHOOSE:TestWP1|TestWP2|TestWP3");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP2");
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP3");
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP4");
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP5");
		secondaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		secondaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP2");
		secondaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP3");
		secondaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP4");
		secondaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP5");
		runRoundRobin("TestWP4|TestWP5", "CHOOSE:TestWP1|TestWP2|TestWP3");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}
}
