package plugin.lsttokens;

import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalListTokenTestCase;

public class TemplateLstTest extends
		AbstractGlobalListTokenTestCase<PCTemplate>
{

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

	static GlobalLstToken token = new TemplateLst();
	static PCTemplateLoader loader = new PCTemplateLoader();

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
	public void testChooseInvalidInputString() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"CHOOSE:String"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseInvalidInputType() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"CHOOSE:TestType"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseInvalidInputJoinedComma()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"CHOOSE:TestWP1,TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseInvalidInputJoinedDot()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"CHOOSE:TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseInvalidListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"CHOOSE:TestWP1" + getJoinCharacter()));
	}

	@Test
	public void testChooseInvalidListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"CHOOSE:" + getJoinCharacter() + "TestWP1"));
	}

	@Test
	public void testChooseInvalidListDoubleJoin()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(getToken().parse(
			primaryContext,
			primaryProf,
			"CHOOSE:TestWP2" + getJoinCharacter() + getJoinCharacter()
				+ "TestWP1"));
	}

	@Test
	public void testChooseInvalidInputCheckMult()
		throws PersistenceLayerException
	{
		// Explicitly do NOT build testChooseWP2
		construct(primaryContext, "TestWP1");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"CHOOSE:TestWP1" + getJoinCharacter() + "TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(getToken().parse(primaryContext, primaryProf, "TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"CHOOSE:TestWP1" + getJoinCharacter() + "TestWP2"));
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testChooseRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("CHOOSE:TestWP1");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testChooseRoundRobinThree() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin("CHOOSE:TestWP1" + getJoinCharacter() + "TestWP2"
			+ getJoinCharacter() + "TestWP3");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

}
