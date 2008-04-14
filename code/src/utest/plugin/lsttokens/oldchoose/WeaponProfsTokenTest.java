package plugin.lsttokens.oldchoose;

import org.junit.Test;

import pcgen.cdom.inst.CDOMTemplate;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.choose.WeaponProfsToken;

public class WeaponProfsTokenTest extends AbstractChooseTokenTestCase
{

	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	static WeaponProfsToken subToken = new WeaponProfsToken();

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

	protected boolean isListLegal()
	{
		return true;
	}

	@Test
	public void testInvalidInputAnyList() throws PersistenceLayerException
	{
		if (isListLegal() && isAnyLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + "|ANY" + getJoinCharacter() + "LIST"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputListAny() throws PersistenceLayerException
	{
		if (isListLegal() && isAnyLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + "|LIST" + getJoinCharacter() + "ANY"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testRoundRobinDeityWeapon() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString() + "|DEITYWEAPON");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinList() throws PersistenceLayerException
	{
		if (isListLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenString() + "|LIST");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		if (isListLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenString() + "|LIST|TestWP1");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinComplexToo() throws PersistenceLayerException
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
