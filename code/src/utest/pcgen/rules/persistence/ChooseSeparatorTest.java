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
package pcgen.rules.persistence;

import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.rules.persistence.ChooseSeparator.BracketMismatchException;

public class ChooseSeparatorTest extends TestCase
{

	@Test
	public void testNullConstructor()
	{
		try
		{
			new ChooseSeparator(null, '|');
			fail();
		}
		catch (IllegalArgumentException iae)
		{
			// OK
		}
	}

	@Test
	public void testSimple()
	{
		ChooseSeparator cs = new ChooseSeparator("Test", '|');
		assertTrue(cs.hasNext());
		assertEquals("Test", cs.next());
		assertFalse(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (NoSuchElementException iae)
		{
			// OK
		}
	}

	@Test
	public void testMismatch()
	{
		ChooseSeparator cs = new ChooseSeparator("Test[Open", '|');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (BracketMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testSecondMismatch()
	{
		ChooseSeparator cs = new ChooseSeparator("Foo|Test[Open", '|');
		assertTrue(cs.hasNext());
		assertEquals("Foo", cs.next());
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (BracketMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testSecondMismatchClose()
	{
		ChooseSeparator cs = new ChooseSeparator("Foo|Test]Open", '|');
		assertTrue(cs.hasNext());
		assertEquals("Foo", cs.next());
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (BracketMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testCloseBeforeOpen()
	{
		ChooseSeparator cs = new ChooseSeparator("Test]Open[", '|');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (BracketMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testNormal()
	{
		ChooseSeparator cs = new ChooseSeparator("Foo[Bar]|Test[Goo,Free]", '|');
		assertTrue(cs.hasNext());
		assertEquals("Foo[Bar]", cs.next());
		assertTrue(cs.hasNext());
		assertEquals("Test[Goo,Free]", cs.next());
		assertFalse(cs.hasNext());
	}

	@Test
	public void testComplexMismatchOne()
	{
		ChooseSeparator cs = new ChooseSeparator("Foo[BarWhee]]|Test[Goo,Free]", '|');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (BracketMismatchException iae)
		{
			// OK
		}
	}

	@Test
	public void testComplexMismatchTwo()
	{
		ChooseSeparator cs = new ChooseSeparator("Foo[Bar[Whee]|Test[Goo,Free]", '|');
		assertTrue(cs.hasNext());
		try
		{
			cs.next();
			fail();
		}
		catch (BracketMismatchException iae)
		{
			// OK
		}
	}
	                 
	@Test
	public void testComplex()
	{
		ChooseSeparator cs = new ChooseSeparator(
				"Foo[Bar[Whee]]|Test[Goo,Free]", '|');
		assertTrue(cs.hasNext());
		assertEquals("Foo[Bar[Whee]]", cs.next());
		assertTrue(cs.hasNext());
		assertEquals("Test[Goo,Free]", cs.next());
		assertFalse(cs.hasNext());
	}
}
