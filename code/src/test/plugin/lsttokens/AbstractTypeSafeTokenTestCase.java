package plugin.lsttokens;

import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.AbstractTokenTestCase;

public abstract class AbstractTypeSafeTokenTestCase<T extends PObject> extends
		AbstractTokenTestCase<T>
{

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Niederösterreich"));
		assertEquals(getConstant("Niederösterreich"), primaryProf
			.get(getObjectKey()));
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, "Finger Lakes"));
		assertEquals(getConstant("Finger Lakes"), primaryProf
			.get(getObjectKey()));
		assertTrue(getToken().parse(primaryContext, primaryProf, "Rheinhessen"));
		assertEquals(getConstant("Rheinhessen"), primaryProf
			.get(getObjectKey()));
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Languedoc-Roussillon"));
		assertEquals(getConstant("Languedoc-Roussillon"), primaryProf
			.get(getObjectKey()));
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, "Yarra Valley"));
		assertEquals(getConstant("Yarra Valley"), primaryProf
			.get(getObjectKey()));
	}

	public abstract Object getConstant(String string);

	public abstract ObjectKey<?> getObjectKey();

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
