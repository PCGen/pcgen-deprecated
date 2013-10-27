/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.testsupport;

import java.math.BigDecimal;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.persistence.PersistenceLayerException;

public abstract class AbstractBigDecimalTokenTestCase<T extends CDOMObject>
		extends AbstractTokenTestCase<T>
{

	public abstract ObjectKey<BigDecimal> getObjectKey();

	public abstract boolean isZeroAllowed();

	public abstract boolean isNegativeAllowed();

	public abstract boolean isPositiveAllowed();

	@Test
	public void testInvalidInputUnset() throws PersistenceLayerException
	{
		testInvalidInputs(null);
		assertNoSideEffects();
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
		assertTrue(parse(con.toString()));
		assertTrue(parseSecondary(con.toString()));
		assertEquals(con, primaryProf.get(getObjectKey()));
		testInvalidInputs(con);
		assertNoSideEffects();
	}

	public void testInvalidInputs(BigDecimal val)
		throws PersistenceLayerException
	{
		// Always ensure get is unchanged
		// since no invalid item should set or reset the value
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(parse("TestWP"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(parse("String"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(parse("TYPE=TestType"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(parse("TYPE.TestType"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(parse("ALL"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(parse("ANY"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(parse("FIVE"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(parse("1/2"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		assertFalse(parse("1+3"));
		assertEquals(val, primaryProf.get(getObjectKey()));
		// Require Integer greater than or equal to zero
		if (!isNegativeAllowed())
		{
			assertFalse(parse("-1"));
			assertEquals(val, primaryProf.get(getObjectKey()));
		}
		if (!isPositiveAllowed())
		{
			assertFalse(parse("1"));
			assertEquals(val, primaryProf.get(getObjectKey()));
		}
		if (!isZeroAllowed())
		{
			assertFalse(parse("0"));
			assertEquals(val, primaryProf.get(getObjectKey()));
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			assertTrue(parse("4.5"));
			assertEquals(new BigDecimal(4.5), primaryProf.get(getObjectKey()));
			assertTrue(parse("5"));
			assertEquals(new BigDecimal(5), primaryProf.get(getObjectKey()));
			assertTrue(parse("1"));
			assertEquals(new BigDecimal(1), primaryProf.get(getObjectKey()));
		}
		if (isZeroAllowed())
		{
			assertTrue(parse("0"));
			assertEquals(new BigDecimal(0), primaryProf.get(getObjectKey()));
		}
		if (isNegativeAllowed())
		{
			assertTrue(parse("-2"));
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
