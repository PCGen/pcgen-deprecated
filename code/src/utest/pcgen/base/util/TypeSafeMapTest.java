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
package pcgen.base.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.base.enumeration.TypeSafeConstant;

public class TypeSafeMapTest extends TestCase {

	public static final class ConstantOne implements TypeSafeConstant {

		private static int ord = 0;

		private int ordinal;

		public ConstantOne() {
			ordinal = ord++;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}

	public static final class ConstantTwo implements TypeSafeConstant {

		private static int ord = 0;

		private int ordinal;

		public ConstantTwo() {
			ordinal = ord++;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}

	public static class FailedConstant implements TypeSafeConstant {

		private static int ord = 0;

		private int ordinal;

		public FailedConstant() {
			ordinal = ord++;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}

	TypeSafeMap tsm, tsm2;

	static ConstantOne one1, one2, one3, one4;

	static ConstantTwo two1, two2, two3;

	private static boolean classSetUpFired = false;

	@BeforeClass
	public static void globalSetUp() {
		one1 = new ConstantOne();
		one2 = new ConstantOne();
		one3 = new ConstantOne();
		one4 = new ConstantOne();
		two1 = new ConstantTwo();
		two2 = new ConstantTwo();
		two3 = new ConstantTwo();
		classSetUpFired = true;
	}

	@Before
	public void setUp() {
		if (!classSetUpFired)
		{
			globalSetUp();
		}
		tsm = new TypeSafeMap(ConstantOne.class);
		tsm2 = new TypeSafeMap(ConstantTwo.class);
	}

	@Test
	public void testConstructor() {
		try {
			new TypeSafeMap((Class) null);
			fail();
		} catch (IllegalArgumentException iae) {
			// This is expected
		}
		try {
			new TypeSafeMap((TypeSafeMap) null);
			fail();
		} catch (IllegalArgumentException iae) {
			// This is expected
		}
		try {
			new TypeSafeMap(Integer.class);
			fail();
		} catch (IllegalArgumentException iae) {
			// This is expected
		}
		try {
			new TypeSafeMap(FailedConstant.class);
			fail();
		} catch (IllegalArgumentException iae) {
			// This is expected
		}
		try {
			TypeSafeMap value = new TypeSafeMap(tsm);
		} catch (IllegalArgumentException iae) {
			fail();
		}
	}

	@Test
	public void badContainsKey() {
		assertFalse(tsm.containsKey(Integer.valueOf(1)));
		assertFalse(tsm.containsKey(two1));
		assertFalse(tsm.containsKey(null));
		tsm.put(one1, Integer.valueOf(1));
		assertFalse(tsm.containsKey(Integer.valueOf(1)));
		// Ensure same ordinal
		assertTrue(one1.getOrdinal() == two1.getOrdinal());
		// But still fails
		assertFalse(tsm.containsKey(two1));
		assertFalse(tsm.containsKey(null));
	}

	@Test
	public void testContainsKey() {
		assertFalse(tsm.containsKey(one1));
		assertFalse(tsm.containsKey(one2));
		assertFalse(tsm.containsKey(one3));
		// intentionally leave one2 unset
		tsm.put(one1, Integer.valueOf(1));
		tsm.put(one3, Integer.valueOf(3));
		assertTrue(tsm.containsKey(one1));
		assertFalse(tsm.containsKey(one2));
		assertTrue(tsm.containsKey(one3));
		tsm.remove(one1);
		assertFalse(tsm.containsKey(one1));
		assertFalse(tsm.containsKey(one2));
		assertTrue(tsm.containsKey(one3));
		tsm.put(one3, Integer.valueOf(4));
		assertFalse(tsm.containsKey(one1));
		assertFalse(tsm.containsKey(one2));
		assertTrue(tsm.containsKey(one3));
	}

	@Test
	public void testContainsValue() {
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		Integer int3 = Integer.valueOf(3);
		assertFalse(tsm.containsValue(int1));
		assertFalse(tsm.containsValue(int2));
		assertFalse(tsm.containsValue(int3));
		assertFalse(tsm.containsValue(null));
		// intentionally leave one2 unset
		tsm.put(one1, int1);
		tsm.put(one3, int3);
		assertTrue(tsm.containsValue(int1));
		assertFalse(tsm.containsValue(int2));
		assertTrue(tsm.containsValue(int3));
		assertFalse(tsm.containsValue(null));
		// Not the keys!!
		assertFalse(tsm.containsValue(one1));
		assertFalse(tsm.containsValue(one2));
		assertFalse(tsm.containsValue(one3));
		tsm.put(one3, null);
		assertTrue(tsm.containsValue(int1));
		// Also test instance equality not required
		assertTrue(tsm.containsValue(new Integer(1)));
		assertFalse(tsm.containsValue(int2));
		assertFalse(tsm.containsValue(int3));
		assertTrue(tsm.containsValue(null));
		tsm.remove(one1);
		assertFalse(tsm.containsValue(int1));
		assertFalse(tsm.containsValue(int2));
		assertFalse(tsm.containsValue(int3));
		assertTrue(tsm.containsValue(null));
	}

	@Test
	public void badRead() {
		assertNull(tsm.get(two1));
		assertNull(tsm.get(Integer.valueOf(1)));
		assertNull(tsm.get(null));
		tsm.put(one1, Integer.valueOf(1));
		assertNull(tsm.get(two1));
		assertNull(tsm.get(Integer.valueOf(1)));
		assertNull(tsm.get(null));
	}

	@Test
	public void testIncrementalLoading() {
		// This is testing for the lack of errors in loading in funny order,
		// no real testing of contents required
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		Integer int3 = Integer.valueOf(3);
		tsm.put(one1, int1);
		tsm.put(one3, int3);
		tsm.put(one2, int2);
	}

	@Test
	public void badRemove() {
		assertNull(tsm.remove(two1));
		assertNull(tsm.remove(Integer.valueOf(1)));
		assertNull(tsm.remove(null));
		tsm.put(one1, Integer.valueOf(1));
		assertNull(tsm.remove(two1));
		assertNull(tsm.remove(Integer.valueOf(1)));
		assertNull(tsm.remove(null));
		assertEquals(Integer.valueOf(1), tsm.get(one1));
	}

	@Test
	public void testBadPut() {
		Integer int2 = Integer.valueOf(2);
		assertTrue(tsm.isEmpty());
		assertEquals(0, tsm.size());
		try {
			tsm.put(two1, int2);
			fail();
		} catch (IllegalArgumentException cce) {
			// OK
		}
	}

	@Test
	public void testBasics() {
		Integer int1 = Integer.valueOf(1);
		assertTrue(tsm.isEmpty());
		assertNull(tsm.get(one1));
		assertEquals(0, tsm.size());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		assertNull(tsm.get(one3));
		assertFalse(tsm.isEmpty());
		assertEquals(1, tsm.size());
		// Unloaded item really is null
		assertNull(tsm.get(one2));
		tsm.put(one1, int1);
		assertFalse(tsm.isEmpty());
		assertEquals(2, tsm.size());
		assertEquals(int1, tsm.get(one1));
		assertNull(tsm.remove(one3));
		assertNull(tsm.get(one3));
		assertEquals(1, tsm.size());
		assertFalse(tsm.isEmpty());
		assertEquals(int1, tsm.remove(one1));
		assertTrue(tsm.isEmpty());
		assertEquals(0, tsm.size());
		assertNull(tsm.get(one1));
		tsm.put(one3, null);
		assertNull(tsm.get(one3));
		assertFalse(tsm.isEmpty());
		// Unloaded item really is null
		assertNull(tsm.get(one2));
		tsm.put(one1, int1);
		assertEquals(2, tsm.size());
		tsm.clear();
		assertEquals(0, tsm.size());
		assertTrue(tsm.isEmpty());
		assertNull(tsm.get(one1));
		assertNull(tsm.remove(one3));
		assertEquals(0, tsm.size());
	}

	@Test
	public void testKeySetAdd() {
		Integer int1 = Integer.valueOf(1);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one1, int1);
		assertEquals(2, tsm.size());
		Set s = tsm.keySet();
		try {
			s.add(one2);
			fail();
		} catch (UnsupportedOperationException uoe) {
			//OK
		}
	}
	
	@Test
	public void testKeySetAddAll() {
		Integer int1 = Integer.valueOf(1);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one1, int1);
		assertEquals(2, tsm.size());
		Set s = tsm.keySet();
		try {
			s.addAll(Arrays.asList(one2));
			fail();
		} catch (UnsupportedOperationException uoe) {
			//OK
		}
	}
	
	@Test
	public void testSimpleKeySet() {
		Integer int1 = Integer.valueOf(1);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one1, int1);
		assertEquals(2, tsm.size());
		Set s = tsm.keySet();
		assertEquals(2, s.size());
		assertTrue(s.contains(one1));
		assertFalse(s.contains(one2));
		assertTrue(s.contains(one3));
		assertFalse(s.contains(two1));
		// Check underlying action interaction
		s.clear();
		assertTrue(tsm.isEmpty());
	}

	@Test
	public void testComplexKeySet() {
		Integer int1 = Integer.valueOf(1);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one1, int1);
		tsm.remove(one1);
		assertEquals(1, tsm.size());
		Set s = tsm.keySet();
		assertEquals(1, s.size());
		assertFalse(s.contains(one1));
		assertFalse(s.contains(one2));
		assertTrue(s.contains(one3));
		assertFalse(s.contains(two1));
		// Check underlying action interaction
		s.clear();
		assertTrue(tsm.isEmpty());
	}

	@Test
	public void testEmptyKeySet() {
		assertTrue(tsm.isEmpty());
		Set s = tsm.keySet();
		assertEquals(0, s.size());
		Iterator it = s.iterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
		try {
			it.next();
			fail();
		} catch (NoSuchElementException nsee) {
			// OK
		}
	}

	@Test
	public void testKeySetIteratorHasNext() {
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		Integer int3 = Integer.valueOf(3);
		Integer int4 = Integer.valueOf(4);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one4, int4);
		tsm.put(one1, int1);
		assertEquals(3, tsm.size());
		Set s = tsm.keySet();
		assertEquals(3, s.size());
		assertTrue(s.contains(one1));
		assertFalse(s.contains(one2));
		assertTrue(s.contains(one3));
		assertTrue(s.contains(one4));
		assertFalse(s.contains(two1));
		Iterator it = s.iterator();
		assertTrue(it.hasNext());
		Object first = it.next();
		assertNotNull(first);
		// No specific order required
		assertTrue(first == one1 || first == one4 || first == one3);
		//Multiple times should be innocent
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		Object second = it.next();
		assertNotNull(second);
		// No specific order required
		assertTrue(first != second);
		assertTrue(second == one1 || second == one4 || second == one3);
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		Object third = it.next();
		assertNotNull(third);
		// No specific order required
		assertTrue(first != second);
		assertTrue(first != third);
		assertTrue(third == one1 || third == one4 || third == one3);
		assertFalse(it.hasNext());
	}

	@Test
	public void testKeySetIteratorNext() {
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		Integer int3 = Integer.valueOf(3);
		Integer int4 = Integer.valueOf(4);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one4, int4);
		tsm.put(one1, int1);
		assertEquals(3, tsm.size());
		Set s = tsm.keySet();
		assertEquals(3, s.size());
		assertTrue(s.contains(one1));
		assertFalse(s.contains(one2));
		assertTrue(s.contains(one3));
		assertTrue(s.contains(one4));
		assertFalse(s.contains(two1));
		// Also need to ensure integrity without hasNext calls :/
		Iterator it = s.iterator();
		Object first = it.next();
		assertNotNull(first);
		// No specific order required
		assertTrue(first == one1 || first == one4 || first == one3);
		Object second = it.next();
		assertNotNull(second);
		// No specific order required
		assertTrue(first != second);
		assertTrue(second == one1 || second == one4 || second == one3);
		Object third = it.next();
		assertNotNull(third);
		// No specific order required
		assertTrue(first != second);
		assertTrue(first != third);
		assertTrue(third == one1 || third == one4 || third == one3);
		try {
			it.next();
			fail();
		} catch (NoSuchElementException nsee) {
			// OK
		}
	}

	@Test
	public void testKeySetIteratorRemove() {
		Integer int1 = Integer.valueOf(1);
		Integer int4 = Integer.valueOf(4);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one4, int4);
		tsm.put(one1, int1);
		assertEquals(3, tsm.size());
		Set s = tsm.keySet();
		assertEquals(3, s.size());
		assertTrue(s.contains(one1));
		assertFalse(s.contains(one2));
		assertTrue(s.contains(one3));
		assertTrue(s.contains(one4));
		assertFalse(s.contains(two1));
		Iterator it = s.iterator();
		Object first = it.next();
		assertNotNull(first);
		// No specific order required
		assertTrue(first == one1 || first == one4 || first == one3);
		Object second = it.next();
		assertNotNull(second);
		// No specific order required
		assertTrue(first != second);
		assertTrue(second == one1 || second == one4 || second == one3);
		// remove second...
		it.remove();
		// iterator still works...
		Object third = it.next();
		assertNotNull(third);
		// No specific order required
		assertTrue(first != second);
		assertTrue(first != third);
		assertTrue(third == one1 || third == one4 || third == one3);
		try {
			it.next();
			fail();
		} catch (NoSuchElementException nsee) {
			// OK
		}
		// But need to check for removed:
		assertEquals(2, tsm.size());
		Set after = tsm.keySet();
		assertEquals(2, s.size());
		assertTrue(after.contains(one1) || one1 == second);
		assertFalse(after.contains(one2));
		assertTrue(after.contains(one3) || one3 == second);
		assertTrue(after.contains(one4) || one4 == second);
		assertFalse(after.contains(two1));
	}

	@Test
	public void testBadKeySetRemove() {
		Integer int1 = Integer.valueOf(1);
		Integer int4 = Integer.valueOf(4);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one4, int4);
		tsm.put(one1, int1);
		assertEquals(3, tsm.size());
		Set s = tsm.keySet();
		assertEquals(3, s.size());
		assertTrue(s.contains(one1));
		assertFalse(s.contains(one2));
		assertTrue(s.contains(one3));
		assertTrue(s.contains(one4));
		assertFalse(s.contains(two1));
		Iterator it = s.iterator();
		try {
			it.remove();
			fail();
		} catch (IllegalStateException ise) {
			// OK
		}
	}

	@Test
	public void testValuesAdd() {
		Integer int1 = Integer.valueOf(1);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one1, int1);
		assertEquals(2, tsm.size());
		Collection s = tsm.values();
		try {
			s.add(one2);
			fail();
		} catch (UnsupportedOperationException uoe) {
			//OK
		}
	}
	
	@Test
	public void testValuesAddAll() {
		Integer int1 = Integer.valueOf(1);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one1, int1);
		assertEquals(2, tsm.size());
		Collection s = tsm.values();
		try {
			s.addAll(Arrays.asList(one2));
			fail();
		} catch (UnsupportedOperationException uoe) {
			//OK
		}
	}
	
	@Test
	public void testSimpleValueCollection() {
		Integer int1 = Integer.valueOf(1);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one1, int1);
		assertEquals(2, tsm.size());
		Collection s = tsm.values();
		assertEquals(2, s.size());
		assertTrue(s.contains(int1));
		assertTrue(s.contains(new Integer(1)));
		assertFalse(s.contains(Integer.valueOf(2)));
		assertTrue(s.contains(null));
		assertFalse(s.contains(one1));
		// Check underlying action interaction
		s.clear();
		assertTrue(tsm.isEmpty());
	}

	@Test
	public void testComplexValueCollection() {
		Integer int1 = Integer.valueOf(1);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one1, int1);
		tsm.remove(one1);
		assertEquals(1, tsm.size());
		Collection s = tsm.values();
		assertEquals(1, s.size());
		assertFalse(s.contains(int1));
		assertFalse(s.contains(one2));
		assertTrue(s.contains(null));
		assertFalse(s.contains(two1));
		// Check underlying action interaction
		s.clear();
		assertTrue(tsm.isEmpty());
	}

	@Test
	public void testEmptyValueCollection() {
		assertTrue(tsm.isEmpty());
		Collection s = tsm.values();
		assertEquals(0, s.size());
		Iterator it = s.iterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
		try {
			it.next();
			fail();
		} catch (NoSuchElementException nsee) {
			// OK
		}
	}

	@Test
	public void testValueCollectionIteratorHasNext() {
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		Integer int3 = Integer.valueOf(3);
		Integer int4 = Integer.valueOf(4);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one4, int4);
		tsm.put(one1, int1);
		assertEquals(3, tsm.size());
		Collection s = tsm.values();
		assertEquals(3, s.size());
		assertTrue(s.contains(int1));
		assertFalse(s.contains(int2));
		assertTrue(s.contains(null));
		assertTrue(s.contains(int4));
		assertFalse(s.contains(one1));
		Iterator it = s.iterator();
		assertTrue(it.hasNext());
		Object first = it.next();
		// No specific order required
		assertTrue(first == int1 || first == null || first == int4);
		//Multiple times should be innocent
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		Object second = it.next();
		// No specific order required
		assertTrue(first != second);
		assertTrue(second == int1 || second == null || second == int4);
		//Multiple times should be innocent
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		Object third = it.next();
		// No specific order required
		assertTrue(first != second);
		assertTrue(first != third);
		assertTrue(third == int1 || third == null || third == int4);
		assertFalse(it.hasNext());
	}

	@Test
	public void testValueCollectionIteratorNext() {
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		Integer int3 = Integer.valueOf(3);
		Integer int4 = Integer.valueOf(4);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one4, int4);
		tsm.put(one1, int1);
		assertEquals(3, tsm.size());
		Collection s = tsm.values();
		assertEquals(3, s.size());
		assertTrue(s.contains(int1));
		assertFalse(s.contains(int2));
		assertTrue(s.contains(null));
		assertTrue(s.contains(int4));
		assertFalse(s.contains(one1));
		Iterator it = s.iterator();
		Object first = it.next();
		// No specific order required
		assertTrue(first == int1 || first == null || first == int4);
		Object second = it.next();
		// No specific order required
		assertTrue(first != second);
		assertTrue(second == int1 || second == null || second == int4);
		Object third = it.next();
		// No specific order required
		assertTrue(first != second);
		assertTrue(first != third);
		assertTrue(third == int1 || third == null || third == int4);
		try {
			it.next();
			fail();
		} catch (NoSuchElementException nsee) {
			// OK
		}
	}

	@Test
	public void testValueCollectionIteratorRemove() {
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		Integer int4 = Integer.valueOf(4);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one4, int4);
		tsm.put(one1, int1);
		assertEquals(3, tsm.size());
		Collection s = tsm.values();
		assertEquals(3, s.size());
		assertTrue(s.contains(int1));
		assertFalse(s.contains(int2));
		assertTrue(s.contains(null));
		assertTrue(s.contains(int4));
		assertFalse(s.contains(one1));
		Iterator it = s.iterator();
		assertTrue(it.hasNext());
		Object first = it.next();
		// No specific order required
		assertTrue(first == int1 || first == null || first == int4);
		assertTrue(it.hasNext());
		Object second = it.next();
		// No specific order required
		assertTrue(first != second);
		assertTrue(second == int1 || second == null || second == int4);
		// remove second...
		it.remove();
		// iterator still works...
		assertTrue(it.hasNext());
		Object third = it.next();
		// No specific order required
		assertTrue(first != second);
		assertTrue(first != third);
		assertTrue(third == int1 || third == null || third == int4);
		assertFalse(it.hasNext());
		// But need to check for removed:
		assertEquals(2, tsm.size());
		Collection after = tsm.values();
		assertEquals(2, s.size());
		assertTrue(after.contains(int1) || int1 == second);
		assertFalse(after.contains(int2));
		assertTrue(after.contains(null) || null == second);
		assertTrue(after.contains(int4) || int4 == second);
		assertFalse(after.contains(one1));
	}

	@Test
	public void testBadValueCollectionRemove() {
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		Integer int4 = Integer.valueOf(4);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one4, int4);
		tsm.put(one1, int1);
		assertEquals(3, tsm.size());
		Collection s = tsm.values();
		assertEquals(3, s.size());
		assertTrue(s.contains(int1));
		assertFalse(s.contains(int2));
		assertTrue(s.contains(null));
		assertTrue(s.contains(int4));
		assertFalse(s.contains(one1));
		Iterator it = s.iterator();
		try {
			it.remove();
			fail();
		} catch (IllegalStateException ise) {
			// OK
		}
	}

	@Test
	public void testNullPutAll() {
		try {
			tsm.putAll(null);
			fail();
		} catch (NullPointerException npe) {
			// OK
		}
	}

	@Test
	public void testBadNonEmptyPutAll() {
		Map m = new HashMap();
		m.put(Double.valueOf(1), Integer.valueOf(2));
		try {
			tsm.putAll(m);
			fail();
		} catch (ClassCastException iae) {
			// OK
		}
	}

	@Test
	public void testPutAll() {
		Map m = new HashMap();
		Integer int1 = Integer.valueOf(1);
		assertTrue(tsm.isEmpty());
		assertNull(tsm.get(one1));
		assertEquals(0, tsm.size());
		// intentionally leave one2 unset
		// intentionally load out of order
		m.put(one3, null);
		tsm.putAll(m);
		assertNull(tsm.get(one3));
		assertFalse(tsm.isEmpty());
		assertEquals(1, tsm.size());
		// Unloaded item really is null
		assertNull(tsm.get(one2));
		m.put(one1, int1);
		tsm.putAll(m);
		assertFalse(tsm.isEmpty());
		assertEquals(2, tsm.size());
		assertEquals(int1, tsm.get(one1));
		assertNull(tsm.remove(one3));
		assertNull(tsm.get(one3));
		assertEquals(1, tsm.size());
		assertFalse(tsm.isEmpty());
		assertEquals(int1, tsm.remove(one1));
		assertTrue(tsm.isEmpty());
		assertEquals(0, tsm.size());
		assertNull(tsm.get(one1));
		m.put(one3, null);
		tsm.putAll(m);
		assertNull(tsm.get(one3));
		assertFalse(tsm.isEmpty());
		// Unloaded item really is null
		assertNull(tsm.get(one2));
		m.put(one1, int1);
		tsm.putAll(m);
		assertEquals(2, tsm.size());
		tsm.clear();
		assertEquals(0, tsm.size());
		assertTrue(tsm.isEmpty());
		assertNull(tsm.get(one1));
		assertNull(tsm.remove(one3));
		assertEquals(0, tsm.size());
		tsm.putAll(m);
		assertEquals(2, tsm.size());
		tsm.clear();
		m.clear();
		Integer int3 = Integer.valueOf(3);
		m.put(one1, int3);
		Integer int4 = Integer.valueOf(4);
		m.put(one3, int4);
		tsm.put(one1, int1);
		Integer int2 = Integer.valueOf(2);
		tsm.put(one2, int2);
		assertEquals(2, tsm.size());
		assertEquals(int1, tsm.get(one1));
		assertEquals(int2, tsm.get(one2));
		assertNull(tsm.get(one3));
		assertNull(tsm.get(one4));
		tsm.putAll(m);
		assertEquals(3, tsm.size());
		assertEquals(int3, tsm.get(one1));
		assertEquals(int2, tsm.get(one2));
		assertEquals(int4, tsm.get(one3));
		assertNull(tsm.get(one4));
		m.clear();
		m.put(one2, null);
		tsm.putAll(m);
		assertEquals(3, tsm.size());
		assertEquals(int3, tsm.get(one1));
		assertNull(tsm.get(one2));
		assertEquals(int4, tsm.get(one3));
		assertNull(tsm.get(one4));
	}

	public class MapEnt implements Map.Entry {

		public final Object k;

		public final Object v;

		public MapEnt(Object key, Object value) {
			k = key;
			v = value;
		}

		public Object getKey() {
			return k;
		}

		public Object getValue() {
			return v;
		}

		public Object setValue(Object arg0) {
			throw new UnsupportedOperationException();
		}

	}

	@Test
	public void testEntrySetAdd() {
		Integer int1 = Integer.valueOf(1);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one1, int1);
		assertEquals(2, tsm.size());
		Collection s = tsm.entrySet();
		try {
			s.add(new MapEnt(one2, int1));
			fail();
		} catch (UnsupportedOperationException uoe) {
			//OK
		}
	}
	
	@Test
	public void testEntrySetAddAll() {
		Integer int1 = Integer.valueOf(1);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one1, int1);
		assertEquals(2, tsm.size());
		Collection s = tsm.entrySet();
		try {
			s.addAll(Arrays.asList(new MapEnt(one2, int1)));
			fail();
		} catch (UnsupportedOperationException uoe) {
			//OK
		}
	}
	
	@Test
	public void testEntrySet() {
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one1, int1);
		tsm.put(one2, int2);
		tsm.remove(one2);
		assertEquals(2, tsm.size());
		Set s = tsm.entrySet();
		assertEquals(2, s.size());
		assertFalse(s.contains(one1));
		assertFalse(s.contains(int1));
		assertTrue(s.contains(new MapEnt(one1, int1)));
		assertFalse(s.contains(new MapEnt(one2, int2)));
		assertTrue(s.contains(new MapEnt(one3, null)));
		assertFalse(s.contains(new MapEnt(two1, int1)));
		assertFalse(s.contains(new MapEnt(null, int1)));
		assertFalse(s.contains(new MapEnt(int2, int1)));
		assertFalse(s.contains(new MapEnt(one1, int2)));
		assertFalse(s.contains(null));
		// Check underlying action interaction
		s.clear();
		assertTrue(tsm.isEmpty());
	}

	@Test
	public void testEmptyEntrySet() {
		assertTrue(tsm.isEmpty());
		Set s = tsm.entrySet();
		assertEquals(0, s.size());
		Iterator it = s.iterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
		try {
			it.next();
			fail();
		} catch (NoSuchElementException nsee) {
			// OK
		}
	}

	@Test
	public void testEntrySetIteratorHasNext() {
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		Integer int3 = Integer.valueOf(3);
		Integer int4 = Integer.valueOf(4);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one4, int4);
		tsm.put(one1, int1);
		assertEquals(3, tsm.size());
		Set s = tsm.entrySet();
		assertEquals(3, s.size());
		assertTrue(s.contains(new MapEnt(one1, int1)));
		assertFalse(s.contains(new MapEnt(one2, int2)));
		assertTrue(s.contains(new MapEnt(one3, null)));
		assertTrue(s.contains(new MapEnt(one4, int4)));
		Iterator<Entry> it = s.iterator();
		//Multiple times should be innocent
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		Entry first = it.next();
		// No specific order required
		Object key1 = first.getKey();
		Object value1 = first.getValue();
		assertTrue(key1 == one1 && value1 == int1 || key1 == one3
				&& value1 == null || key1 == one4 && value1 == int4);
		assertTrue(it.hasNext());
		Entry second = it.next();
		Object key2 = second.getKey();
		Object value2 = second.getValue();
		// No specific order required
		assertTrue(key2 == one1 && value2 == int1 || key2 == one3
				&& value2 == null || key2 == one4 && value2 == int4);
		assertTrue(key1 != key2);
		//Multiple times should be innocent
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		Entry third = it.next();
		Object key3 = third.getKey();
		Object value3 = third.getValue();
		// No specific order required
		assertTrue(key3 == one1 && value3 == int1 || key3 == one3
				&& value3 == null || key3 == one4 && value3 == int4);
		assertTrue(key1 != key3);
		assertTrue(key2 != key3);
		assertFalse(it.hasNext());
	}

	@Test
	public void testEntrySetIteratorNext() {
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		Integer int3 = Integer.valueOf(3);
		Integer int4 = Integer.valueOf(4);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one4, int4);
		tsm.put(one1, int1);
		assertEquals(3, tsm.size());
		Set s = tsm.entrySet();
		assertEquals(3, s.size());
		assertTrue(s.contains(new MapEnt(one1, int1)));
		assertFalse(s.contains(new MapEnt(one2, int2)));
		assertTrue(s.contains(new MapEnt(one3, null)));
		assertTrue(s.contains(new MapEnt(one4, int4)));
		Iterator<Entry> it = s.iterator();
		Entry first = it.next();
		// No specific order required
		Object key1 = first.getKey();
		Object value1 = first.getValue();
		assertTrue(key1 == one1 && value1 == int1 || key1 == one3
				&& value1 == null || key1 == one4 && value1 == int4);
		Entry second = it.next();
		Object key2 = second.getKey();
		Object value2 = second.getValue();
		// No specific order required
		assertTrue(key2 == one1 && value2 == int1 || key2 == one3
				&& value2 == null || key2 == one4 && value2 == int4);
		assertTrue(key1 != key2);
		Entry third = it.next();
		Object key3 = third.getKey();
		Object value3 = third.getValue();
		// No specific order required
		assertTrue(key3 == one1 && value3 == int1 || key3 == one3
				&& value3 == null || key3 == one4 && value3 == int4);
		assertTrue(key1 != key3);
		assertTrue(key2 != key3);
		try {
			it.next();
			fail();
		} catch (NoSuchElementException nsee) {
			// OK
		}
	}

	@Test
	public void testEntrySetIteratorRemove() {
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		Integer int3 = Integer.valueOf(3);
		Integer int4 = Integer.valueOf(4);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one4, int4);
		tsm.put(one1, int1);
		assertEquals(3, tsm.size());
		Set s = tsm.entrySet();
		assertEquals(3, s.size());
		assertTrue(s.contains(new MapEnt(one1, int1)));
		assertFalse(s.contains(new MapEnt(one2, int2)));
		assertTrue(s.contains(new MapEnt(one3, null)));
		assertTrue(s.contains(new MapEnt(one4, int4)));
		Iterator<Entry> it = s.iterator();
		assertTrue(it.hasNext());
		Entry first = it.next();
		// No specific order required
		Object key1 = first.getKey();
		Object value1 = first.getValue();
		assertTrue(key1 == one1 && value1 == int1 || key1 == one3
				&& value1 == null || key1 == one4 && value1 == int4);
		assertTrue(it.hasNext());
		Entry second = it.next();
		Object key2 = second.getKey();
		Object value2 = second.getValue();
		// No specific order required
		assertTrue(key2 == one1 && value2 == int1 || key2 == one3
				&& value2 == null || key2 == one4 && value2 == int4);
		assertTrue(key1 != key2);
		// remove second...
		it.remove();
		// iterator still works...
		assertTrue(it.hasNext());
		Entry third = it.next();
		Object key3 = third.getKey();
		Object value3 = third.getValue();
		// No specific order required
		assertTrue(key3 == one1 && value3 == int1 || key3 == one3
				&& value3 == null || key3 == one4 && value3 == int4);
		assertTrue(key1 != key3);
		assertTrue(key2 != key3);
		assertFalse(it.hasNext());
		// But need to check for removed:
		assertEquals(2, tsm.size());
		Collection after = tsm.values();
		assertEquals(2, s.size());
		assertTrue(after.contains(int1) || int1 == value2);
		assertFalse(after.contains(int2));
		assertTrue(after.contains(null) || null == value2);
		assertTrue(after.contains(int4) || int4 == value2);
		assertFalse(after.contains(one1));
	}

	@Test
	public void testBadEntrySetRemove() {
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		Integer int4 = Integer.valueOf(4);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one4, int4);
		tsm.put(one1, int1);
		assertEquals(3, tsm.size());
		Set s = tsm.entrySet();
		assertEquals(3, s.size());
		assertTrue(s.contains(new MapEnt(one1, int1)));
		assertFalse(s.contains(new MapEnt(one2, int2)));
		assertTrue(s.contains(new MapEnt(one3, null)));
		assertTrue(s.contains(new MapEnt(one4, int4)));
		Iterator it = s.iterator();
		try {
			it.remove();
			fail();
		} catch (IllegalStateException ise) {
			// OK
		}
	}

	@Test
	public void testEntrySetIteratorSetValue() {
		Integer int1 = Integer.valueOf(1);
		Integer int2 = Integer.valueOf(2);
		Integer int3 = Integer.valueOf(3);
		Integer int4 = Integer.valueOf(4);
		Integer int5 = Integer.valueOf(5);
		assertTrue(tsm.isEmpty());
		// intentionally leave one2 unset
		// intentionally load out of order
		tsm.put(one3, null);
		tsm.put(one4, int4);
		tsm.put(one1, int1);
		assertEquals(3, tsm.size());
		Set s = tsm.entrySet();
		assertEquals(3, s.size());
		assertTrue(s.contains(new MapEnt(one1, int1)));
		assertFalse(s.contains(new MapEnt(one2, int2)));
		assertTrue(s.contains(new MapEnt(one3, null)));
		assertTrue(s.contains(new MapEnt(one4, int4)));
		Iterator<Entry> it = s.iterator();
		assertTrue(it.hasNext());
		Entry first = it.next();
		// No specific order required
		Object key1 = first.getKey();
		Object value1 = first.getValue();
		assertTrue(key1 == one1 && value1 == int1 || key1 == one3
				&& value1 == null || key1 == one4 && value1 == int4);
		assertTrue(it.hasNext());
		Entry second = it.next();
		Object key2 = second.getKey();
		Object value2 = second.getValue();
		// No specific order required
		assertTrue(key2 == one1 && value2 == int1 || key2 == one3
				&& value2 == null || key2 == one4 && value2 == int4);
		assertTrue(key1 != key2);
		// modify second...
		assertEquals(value2, second.setValue(int3));
		// iterator still works...
		assertTrue(it.hasNext());
		Entry third = it.next();
		Object key3 = third.getKey();
		Object value3 = third.getValue();
		// No specific order required
		assertTrue(key3 == one1 && value3 == int1 || key3 == one3
				&& value3 == null || key3 == one4 && value3 == int4);
		assertTrue(key1 != key3);
		assertTrue(key2 != key3);
		assertEquals(value3, third.setValue(int5));
		assertFalse(it.hasNext());
		// But need to check for modified:
		assertEquals(3, tsm.size());
		Set after = tsm.entrySet();
		assertEquals(3, s.size());
		if (one1 == key2) {
			assertTrue(s.contains(new MapEnt(one1, int3)));
		} else if (one1 == key3) {
			assertTrue(s.contains(new MapEnt(one1, int5)));
		} else {
			assertTrue(s.contains(new MapEnt(one1, int1)));
		}
		if (one3 == key2) {
			assertTrue(s.contains(new MapEnt(one3, int3)));
		} else if (one3 == key3) {
			assertTrue(s.contains(new MapEnt(one3, int5)));
		} else {
			assertTrue(s.contains(new MapEnt(one3, null)));
		}
		if (one4 == key2) {
			assertTrue(s.contains(new MapEnt(one4, int3)));
		} else if (one4 == key3) {
			assertTrue(s.contains(new MapEnt(one4, int5)));
		} else {
			assertTrue(s.contains(new MapEnt(one4, int4)));
		}
		assertEquals(int3, tsm.get(key2));
		assertEquals(int5, tsm.get(key3));
	}

}
