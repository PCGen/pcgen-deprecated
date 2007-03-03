package plugin.lsttokens;

import java.math.BigDecimal;

import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.AbstractTokenTestCase;

public abstract class AbstractBigDecimalTokenTestCase<T extends PObject> extends
		AbstractTokenTestCase<T>
{

	public abstract ObjectKey<BigDecimal> getObjectKey();

	public abstract boolean isZeroAllowed();

	public abstract boolean isNegativeAllowed();

	public abstract boolean isPositiveAllowed();

	@Test
	public void testInvalidInputUnset() throws PersistenceLayerException
	{
		testInvalidInputs(null);
	}

	@Test
	public void testInvalidInputSet() throws PersistenceLayerException
	{
		BigDecimal con;
		if (isPositiveAllowed())
		{
			con = new BigDecimal(3);
		}
		else
		{
			con = new BigDecimal(-3);
		}
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, con.toString()));
		assertEquals(con, primaryProf.get(getObjectKey()));
		testInvalidInputs(con);
	}

	public void testInvalidInputs(BigDecimal val)
		throws PersistenceLayerException
	{
		//Always ensure get is unchanged
		// since no invalid item should set or reset the value
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "TestWP"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "String"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TYPE=TestType"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TYPE.TestType"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "ALL"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "ANY"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "FIVE"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "1/2"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "1+3"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		//Require Integer greater than or equal to zero
		if (!isNegativeAllowed())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf, "-1"));
			assertEquals(val, primaryProf.get(getObjectKey()));
		}
		if (!isPositiveAllowed())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf, "1"));
			assertEquals(val, primaryProf.get(getObjectKey()));
		}
		if (!isZeroAllowed())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf, "0"));
			assertEquals(val, primaryProf.get(getObjectKey()));
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			assertTrue(getToken().parse(primaryContext, primaryProf, "4.5"));
			assertEquals(new BigDecimal(4.5), primaryProf.get(getObjectKey()));
			assertTrue(getToken().parse(primaryContext, primaryProf, "5"));
			assertEquals(new BigDecimal(5), primaryProf.get(getObjectKey()));
			assertTrue(getToken().parse(primaryContext, primaryProf, "1"));
			assertEquals(new BigDecimal(1), primaryProf.get(getObjectKey()));
		}
		if (isZeroAllowed())
		{
			assertTrue(getToken().parse(primaryContext, primaryProf, "0"));
			assertEquals(new BigDecimal(0), primaryProf.get(getObjectKey()));
		}
		if (isNegativeAllowed())
		{
			assertTrue(getToken().parse(primaryContext, primaryProf, "-2"));
			assertEquals(new BigDecimal(-2), primaryProf.get(getObjectKey()));
		}
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			runRoundRobin("1");
		}
	}

	@Test
	public void testRoundRobinZero() throws PersistenceLayerException
	{
		if (isZeroAllowed())
		{
			runRoundRobin("0");
		}
	}

	@Test
	public void testRoundRobinNegative() throws PersistenceLayerException
	{
		if (isNegativeAllowed())
		{
			runRoundRobin("-3");
		}
	}

	@Test
	public void testRoundRobinThreePointFive() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			runRoundRobin("3.5");
		}
	}
}
