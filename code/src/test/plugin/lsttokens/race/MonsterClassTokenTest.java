package plugin.lsttokens.race;

import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.RaceLoader;
import plugin.lsttokens.AbstractTokenTestCase;

public class MonsterClassTokenTest extends AbstractTokenTestCase<Race>
{

	static MonsterclassToken token = new MonsterclassToken();
	static RaceLoader loader = new RaceLoader();

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public LstObjectFileLoader<Race> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Race> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidNoColon() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter"));
	}

	@Test
	public void testInvalidTwoColon() throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "Fighter:4:1"));
	}

	@Test
	public void testInvalidLevelNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter:-4"));
	}

	@Test
	public void testInvalidLevelZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter:0"));
	}

	@Test
	public void testInvalidLevelNaN() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Fighter:Level"));
	}

	@Test
	public void testBadClass() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "Fighter:4"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("Fighter:4");
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testMultiple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		runRoundRobin("Fighter:4", "Wizard:5");
		assertTrue(primaryContext.ref.validate());
	}
}
