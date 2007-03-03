package plugin.lsttokens.skill;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.SkillLoader;
import plugin.lsttokens.AbstractTokenTestCase;
import plugin.lsttokens.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;

public class ClassesTokenTest extends AbstractTokenTestCase<Skill>
{

	static ClassesToken token = new ClassesToken();
	static SkillLoader loader = new SkillLoader();

	private static boolean classSetUpFired = false;

	@BeforeClass
	public static final void ltClassSetUp() throws PersistenceLayerException
	{
		TokenRegistration.register(new PreClassParser());
		classSetUpFired = true;
	}

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
		URISyntaxException
	{
		super.setUp();
		if (!classSetUpFired)
		{
			ltClassSetUp();
		}
	}

	@Override
	public Class<Skill> getCDOMClass()
	{
		return Skill.class;
	}

	@Override
	public LstObjectFileLoader<Skill> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Skill> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputLeadingBar() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|Wizard"));
	}

	@Test
	public void testInvalidInputTrailingBar() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Wizard|"));
	}

	@Test
	public void testInvalidInputNegationMix() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Wizard|!Sorcerer"));
	}

	@Test
	public void testInvalidInputNegationMixTwo()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"!Wizard|Sorcerer"));
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Wizard||Sorcerer"));
	}

	// @Test
	// public void testInvalidInputEmptyType() throws PersistenceLayerException
	// {
	// assertFalse(getToken().parse(primaryContext, primaryProf, "TYPE."));
	// }

	@Test
	public void testInvalidInputNotClass() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "Wizard"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputNotClassCompound()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Wizard|Sorcerer"));
		assertFalse(primaryContext.ref.validate());
	}

	// @Test(expected = IllegalArgumentException.class)
	public void testInvalidInputAllPlus() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		try
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"Wizard|ALL"));
			fail();
		}
		catch (IllegalArgumentException iae)
		{
			// OK
		}
	}

	// @Test(expected = IllegalArgumentException.class)
	public void testInvalidInputNegativeAllPlus()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		try
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"!Wizard|ALL"));
			fail();
		}
		catch (IllegalArgumentException iae)
		{
			// OK
		}
	}

	@Test
	public void testInvalidInputNegativeAll() throws PersistenceLayerException
	{
		// This technically gets caught by the PRECLASS parser...
		assertFalse(getToken().parse(primaryContext, primaryProf, "!ALL"));
	}

	@Test
	public void testRoundRobinAll() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		runRoundRobin("ALL");
		assertTrue(primaryContext.ref.validate());
		assertEquals(0, primaryContext.getWriteMessageCount());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		runRoundRobin("Wizard");
		assertTrue(primaryContext.ref.validate());
		assertEquals(0, primaryContext.getWriteMessageCount());
	}

	@Test
	public void testRoundRobinNegated() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		runRoundRobin("!Wizard");
		assertTrue(primaryContext.ref.validate());
		assertEquals(0, primaryContext.getWriteMessageCount());
	}

	@Test
	public void testRoundRobinPipe() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		primaryContext.ref.constructCDOMObject(PCClass.class, "Sorcerer");
		runRoundRobin("Sorcerer|Wizard");
		assertTrue(primaryContext.ref.validate());
		assertEquals(0, primaryContext.getWriteMessageCount());
	}

	@Test
	public void testRoundRobinNegatedPipe() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		primaryContext.ref.constructCDOMObject(PCClass.class, "Sorcerer");
		runRoundRobin("!Sorcerer|!Wizard");
		assertTrue(primaryContext.ref.validate());
		assertEquals(0, primaryContext.getWriteMessageCount());
	}
}
