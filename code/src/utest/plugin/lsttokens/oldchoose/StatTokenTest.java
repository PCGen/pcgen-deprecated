package plugin.lsttokens.oldchoose;

import org.junit.Test;

import pcgen.cdom.inst.CDOMStat;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.choose.StatToken;

public class StatTokenTest extends AbstractChooseTokenTestCase
{

	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	static StatToken subToken = new StatToken();

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
	protected Class<CDOMStat> getSubTokenType()
	{
		return CDOMStat.class;
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
		return false;
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
	public void testInvalidDoubleStat() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1" + getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidRemoveAll() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1" + getJoinCharacter() + "TestWP2"));
		assertNoSideEffects();
	}
}
