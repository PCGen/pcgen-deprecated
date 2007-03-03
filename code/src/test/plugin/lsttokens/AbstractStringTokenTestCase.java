package plugin.lsttokens;

import org.junit.Test;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.AbstractTokenTestCase;

public abstract class AbstractStringTokenTestCase<T extends PObject> extends
		AbstractTokenTestCase<T>
{

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Niederösterreich"));
		assertEquals("Niederösterreich", primaryProf.get(getStringKey()));
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, "Finger Lakes"));
		assertEquals("Finger Lakes", primaryProf.get(getStringKey()));
		assertTrue(getToken().parse(primaryContext, primaryProf, "Rheinhessen"));
		assertEquals("Rheinhessen", primaryProf.get(getStringKey()));
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Languedoc-Roussillon"));
		assertEquals("Languedoc-Roussillon", primaryProf.get(getStringKey()));
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, "Yarra Valley"));
		assertEquals("Yarra Valley", primaryProf.get(getStringKey()));
	}

	public abstract StringKey getStringKey();

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		runRoundRobin("Rheinhessen");
	}

	@Test
	public void testRoundRobinWithSpace() throws PersistenceLayerException
	{
		runRoundRobin("Finger Lakes");
	}

	@Test
	public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
	{
		runRoundRobin("Niederösterreich");
	}

	@Test
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		runRoundRobin("Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinY() throws PersistenceLayerException
	{
		runRoundRobin("Yarra Valley");
	}
}
