package plugin.lsttokens.oldchoose;

import org.junit.Test;

import pcgen.cdom.inst.CDOMSkill;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.choose.CCSkillListToken;

public class CCSkillListTokenTest extends AbstractChooseTokenTestCase
{

	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	static CCSkillListToken subToken = new CCSkillListToken();

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
	protected Class<CDOMSkill> getSubTokenType()
	{
		return CDOMSkill.class;
	}

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
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

	protected boolean isListLegal()
	{
		return true;
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
	public void testInvalidInputListItem() throws PersistenceLayerException
	{
		if (isPrimitiveLegal() && isListLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + "|LIST" + getJoinCharacter() + "TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputItemList() throws PersistenceLayerException
	{
		if (isPrimitiveLegal() && isListLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + "|TestWP1" + getJoinCharacter() + "LIST"));
			assertNoSideEffects();
		}
	}

	//
	// @Test
	// public void testInvalidInputListType() throws PersistenceLayerException
	// {
	// if (isListLegal() && isTypeLegal())
	// {
	// assertFalse(getToken().parse(
	// primaryContext,
	// primaryProf,
	// getSubTokenString() + "|LIST" + getJoinCharacter()
	// + "TYPE=TestType"));
	// assertTrue(primaryGraph.isEmpty());
	// }
	// }
	//
	// @Test
	// public void testInvalidInputTypeList() throws PersistenceLayerException
	// {
	// if (isListLegal() && isTypeLegal())
	// {
	// assertFalse(getToken().parse(
	// primaryContext,
	// primaryProf,
	// getSubTokenString() + "|TYPE=TestType" + getJoinCharacter()
	// + "LIST"));
	// assertTrue(primaryGraph.isEmpty());
	// }
	// }

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
}
