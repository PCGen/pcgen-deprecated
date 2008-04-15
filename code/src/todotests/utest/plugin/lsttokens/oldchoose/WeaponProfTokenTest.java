package utest.plugin.lsttokens.oldchoose;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.inst.CDOMTemplate;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.choose.WeaponProfToken;

public class WeaponProfTokenTest extends AbstractChooseTokenTestCase
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

	static WeaponProfToken subToken = new WeaponProfToken();

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
	protected Class<CDOMWeaponProf> getSubTokenType()
	{
		return CDOMWeaponProf.class;
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

	@Test
	public void testInvalidCountNaN() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|x|String"));
	}

	@Test
	public void testRoundRobinNumberOnly() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString() + "|2");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinDeityWeapon() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString() + "|1|DEITYWEAPON");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputNotTypeEmpty() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + getPrefix() + "|!TYPE="));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotTypeUnterminated()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + getPrefix() + "|!TYPE=One."));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotTypeDoubleSeparator()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + getPrefix() + "|!TYPE=One..Two"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotTypeFalseStart()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + getPrefix() + "|!TYPE=.One"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputAnyNotType() throws PersistenceLayerException
	{
		if (isAnyLegal() && isTypeLegal())
		{
			assertFalse(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + getPrefix() + "|ANY" + getJoinCharacter()
					+ "!TYPE=TestType"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotTypeAny() throws PersistenceLayerException
	{
		if (isAnyLegal() && isTypeLegal())
		{
			assertFalse(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + getPrefix() + "|!TYPE=TestType"
					+ getJoinCharacter() + "ANY"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidNotTypeAlone() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + getPrefix() + "|!TYPE=TestType"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputCheckNotTypeEqualLength()
		throws PersistenceLayerException
	{
		if (isPrimitiveLegal() && isTypeLegal())
		{
			// Explicitly do NOT build TestWP2 (this checks that the TYPE=
			// doesn't
			// consume the |
			construct(primaryContext, "TestWP1");
			assertTrue(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + getPrefix() + "|TestWP1"
					+ getJoinCharacter() + "!TYPE=TestType"
					+ getJoinCharacter() + "TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidInputCheckNotTypeDotLength()
		throws PersistenceLayerException
	{
		if (isPrimitiveLegal() && isTypeLegal())
		{
			// Explicitly do NOT build TestWP2 (this checks that the TYPE=
			// doesn't
			// consume the |
			construct(primaryContext, "TestWP1");
			assertTrue(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + getPrefix() + "|TestWP1"
					+ getJoinCharacter() + "!TYPE.TestType.OtherTestType"
					+ getJoinCharacter() + "TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinWithEqualNotType()
		throws PersistenceLayerException
	{
		if (isPrimitiveLegal())
		{
			if (isTypeLegal())
			{
				construct(primaryContext, "TestWP1");
				construct(primaryContext, "TestWP2");
				construct(secondaryContext, "TestWP1");
				construct(secondaryContext, "TestWP2");
				runRoundRobin(getSubTokenString() + getPrefix() + "|TestWP1"
					+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
					+ "!TYPE=OtherTestType" + getJoinCharacter()
					+ "!TYPE=TestAltType.TestThirdType.TestType");
				assertTrue(primaryContext.ref.validate());
				assertTrue(secondaryContext.ref.validate());
			}
		}
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		if (isPrimitiveLegal())
		{
			if (isTypeLegal())
			{
				construct(primaryContext, "TestWP1");
				construct(primaryContext, "TestWP2");
				construct(secondaryContext, "TestWP1");
				construct(secondaryContext, "TestWP2");
				runRoundRobin(getSubTokenString() + getPrefix()
					+ "|DEITYWEAPON" + getJoinCharacter() + "TestWP2"
					+ getJoinCharacter() + "!TYPE=OtherTestType"
					+ getJoinCharacter()
					+ "!TYPE=TestAltType.TestThirdType.TestType");
				assertTrue(primaryContext.ref.validate());
				assertTrue(secondaryContext.ref.validate());
			}
		}
	}
}
