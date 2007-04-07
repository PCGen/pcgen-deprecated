package plugin.lsttokens.race;

import org.junit.Test;

import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.RaceLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class HitDiceAdvancementTokenTest extends AbstractTokenTestCase<Race>
{

	static HitdiceadvancementToken token = new HitdiceadvancementToken();
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
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, ""));
	}

	@Test
	public void testInvalidNotEnoughValues() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "1"));
	}

	// @Test
	// public void testInvalidTooManyValues() throws PersistenceLayerException
	// {
	// assertFalse(token.parse(primaryContext, primaryProf,
	// "1,2,3,4,5,6,7,8,9,0"));
	// }

	@Test
	public void testInvalidEmptyValue1() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, ",2,3,4"));
	}

	@Test
	public void testInvalidEmptyValue2() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "1,,3,4"));
	}

	@Test
	public void testInvalidEmptyValueLast() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "1,2,3,"));
	}

	@Test
	public void testInvalidNegativeValue() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "-1,2,3"));
	}

	@Test
	public void testInvalidDecreasingValue() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5,3,8"));
	}

	@Test
	public void testInvalidEmbeddedSplat() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5,*,8"));
	}

	@Test
	public void testInvalidNaN() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5,N"));
	}

	@Test
	public void testInvalidTooMuchSplat() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5,8*"));
	}

	@Test
	public void testInvalidTooMuchAfterSplat() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5,*8"));
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		this.runRoundRobin("1,2,3");
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		this.runRoundRobin("5,7,9,*");
	}
}
