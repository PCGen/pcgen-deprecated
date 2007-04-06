package plugin.lsttokens.spell;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PCStat;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.SpellLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class StatTokenTest extends AbstractTokenTestCase<Spell>
{

	static StatToken token = new StatToken();
	static SpellLoader loader = new SpellLoader();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		primaryContext.ref.constructCDOMObject(PCStat.class, "STR");
		secondaryContext.ref.constructCDOMObject(PCStat.class, "STR");
		primaryContext.ref.constructCDOMObject(PCStat.class, "INT");
		secondaryContext.ref.constructCDOMObject(PCStat.class, "INT");
	}

	@Override
	public Class<Spell> getCDOMClass()
	{
		return Spell.class;
	}

	@Override
	public LstObjectFileLoader<Spell> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Spell> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidNotAStat() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "NAN"));
	}

	@Test
	public void testInvalidMultipleStatComma() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "STR,INT"));
	}

	@Test
	public void testInvalidMultipleStatBar() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "STR|INT"));
	}

	@Test
	public void testInvalidMultipleStatDot() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "STR.INT"));
	}

	@Test
	public void testRoundRobinDisplay() throws PersistenceLayerException
	{
		runRoundRobin("STR");
	}

}
