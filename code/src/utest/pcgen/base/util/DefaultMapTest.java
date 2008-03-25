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

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class DefaultMapTest extends TestCase {

	DefaultMap<Integer, Double> dm;

	@Override
	@Before
	public void setUp() {
		dm = new DefaultMap<Integer, Double>();
	}

	public void populate() {
		dm.put(Integer.valueOf(0), Double.valueOf(0));
		dm.put(Integer.valueOf(1), Double.valueOf(1));
		dm.put(Integer.valueOf(2), Double.valueOf(0));
		dm.put(Integer.valueOf(3), Double.valueOf(1));
		dm.put(Integer.valueOf(-1), null);
		dm.put(null, Double.valueOf(-1));
	}

	@Test
	public void testPutGet() {
		populate();
		assertEquals(Double.valueOf(0), dm.get(Integer.valueOf(0)));
		assertEquals(Double.valueOf(1), dm.get(Integer.valueOf(1)));
		assertEquals(Double.valueOf(0), dm.get(Integer.valueOf(2)));
		assertEquals(Double.valueOf(1), dm.get(Integer.valueOf(3)));
		assertNull(dm.get(Integer.valueOf(-1)));
		assertEquals(Double.valueOf(-1), dm.get(null));
	}
	
	@Test
	public void testDefaultValue() {
		populate();
		//Start as null
		assertNull(dm.get(Integer.valueOf(-2)));
		assertNull(dm.getDefaultValue());
		dm.setDefaultValue(Double.valueOf(5));
		assertEquals(Double.valueOf(5), dm.get(Integer.valueOf(-2)));
		assertEquals(Double.valueOf(5), dm.getDefaultValue());
		//Null is allowed
		dm.setDefaultValue(null);
		assertNull(dm.getDefaultValue());
		assertNull(dm.get(Integer.valueOf(-2)));
	}
}
