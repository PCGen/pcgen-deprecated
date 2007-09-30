package plugin.lsttokens.oldchoose;

import org.junit.Test;

import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.choose.StatToken;

public class StatTokenTest extends AbstractChooseTokenTestCase
{

	static PCTemplateLoader loader = new PCTemplateLoader();

	static StatToken subToken = new StatToken();

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
	protected Class<PCStat> getSubTokenType()
	{
		return PCStat.class;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
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
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidRemoveAll() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1" + getJoinCharacter() + "TestWP2"));
		assertTrue(primaryGraph.isEmpty());
	}
}
