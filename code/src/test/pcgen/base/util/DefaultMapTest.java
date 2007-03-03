package pcgen.base.util;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class DefaultMapTest extends TestCase {

	DefaultMap dm;

	@Before
	public void setUp() {
		dm = new DefaultMap();
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
