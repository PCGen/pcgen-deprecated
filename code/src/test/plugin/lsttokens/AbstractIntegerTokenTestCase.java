package plugin.lsttokens;

import org.junit.Test;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.AbstractTokenTestCase;

public abstract class AbstractIntegerTokenTestCase<T extends PObject> extends
		AbstractTokenTestCase<T>
{

	public abstract IntegerKey getIntegerKey();

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
		Integer con;
		if (isPositiveAllowed())
		{
			con = Integer.valueOf(3);
		}
		else
		{
			con = Integer.valueOf(-3);
		}
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, con.toString()));
		assertEquals(con, primaryProf.get(getIntegerKey()));
		testInvalidInputs(con);
	}

	public void testInvalidInputs(Integer val) throws PersistenceLayerException
	{
		//Always ensure get is unchanged
		// since no invalid item should set or reset the value
		assertEquals(val, primaryProf.get(getIntegerKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "TestWP"));
		assertEquals(val, primaryProf.get(getIntegerKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "String"));
		assertEquals(val, primaryProf.get(getIntegerKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TYPE=TestType"));
		assertEquals(val, primaryProf.get(getIntegerKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TYPE.TestType"));
		assertEquals(val, primaryProf.get(getIntegerKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "ALL"));
		assertEquals(val, primaryProf.get(getIntegerKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "ANY"));
		assertEquals(val, primaryProf.get(getIntegerKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "FIVE"));
		assertEquals(val, primaryProf.get(getIntegerKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "4.5"));
		assertEquals(val, primaryProf.get(getIntegerKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "1/2"));
		assertEquals(val, primaryProf.get(getIntegerKey()));
		assertFalse(getToken().parse(primaryContext, primaryProf, "1+3"));
		assertEquals(val, primaryProf.get(getIntegerKey()));
		//Require Integer greater than or equal to zero
		if (!isNegativeAllowed())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf, "-1"));
			assertEquals(val, primaryProf.get(getIntegerKey()));
		}
		if (!isPositiveAllowed())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf, "1"));
			assertEquals(val, primaryProf.get(getIntegerKey()));
		}
		if (!isZeroAllowed())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf, "0"));
			assertEquals(val, primaryProf.get(getIntegerKey()));
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			assertTrue(getToken().parse(primaryContext, primaryProf, "5"));
			assertEquals(Integer.valueOf(5), primaryProf.get(getIntegerKey()));
			assertTrue(getToken().parse(primaryContext, primaryProf, "1"));
			assertEquals(Integer.valueOf(1), primaryProf.get(getIntegerKey()));
		}
		if (isZeroAllowed())
		{
			assertTrue(getToken().parse(primaryContext, primaryProf, "0"));
			assertEquals(Integer.valueOf(0), primaryProf.get(getIntegerKey()));
		}
		if (isNegativeAllowed())
		{
			assertTrue(getToken().parse(primaryContext, primaryProf, "-2"));
			assertEquals(Integer.valueOf(-2), primaryProf.get(getIntegerKey()));
		}
	}

	@Test
	public void testOutputOne() throws PersistenceLayerException
	{
		assertTrue(0 == primaryContext.getWriteMessageCount());
		primaryProf.put(getIntegerKey(), 1);
		String unparsed = getToken().unparse(primaryContext, primaryProf);
		if (isPositiveAllowed())
		{
			assertEquals(getToken().getTokenName() + ':' + 1, unparsed);
		}
		else
		{
			assertNull(unparsed);
			assertTrue(0 != primaryContext.getWriteMessageCount());
		}
	}

	@Test
	public void testOutputZero() throws PersistenceLayerException
	{
		assertTrue(0 == primaryContext.getWriteMessageCount());
		primaryProf.put(getIntegerKey(), 0);
		String unparsed = getToken().unparse(primaryContext, primaryProf);
		if (isZeroAllowed())
		{
			assertEquals(getToken().getTokenName() + ':' + 0, unparsed);
		}
		else
		{
			assertNull(unparsed);
			assertTrue(0 != primaryContext.getWriteMessageCount());
		}
	}

	@Test
	public void testOutputMinusTwo() throws PersistenceLayerException
	{
		assertTrue(0 == primaryContext.getWriteMessageCount());
		primaryProf.put(getIntegerKey(), -2);
		String unparsed = getToken().unparse(primaryContext, primaryProf);
		if (isNegativeAllowed())
		{
			assertEquals(getToken().getTokenName() + ':' + -2, unparsed);
		}
		else
		{
			assertNull(unparsed);
			assertTrue(0 != primaryContext.getWriteMessageCount());
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
	public void testRoundRobinFive() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			runRoundRobin("5");
		}
	}
}
