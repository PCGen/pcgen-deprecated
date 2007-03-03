package pcgen.base.util;

import org.junit.Before;
import org.junit.Test;


public class HashMapToInstanceListTest extends HashMapToListTest {

	@Before
	public void setUp() {
		dkm = new HashMapToInstanceList<Integer, Character>();
	}

	@Test
	public void testInstanceBehavior() {
		Character ca = Character.valueOf('a');
		Character cb = Character.valueOf('b');
		Character cc = Character.valueOf('c');
		Character ca1 = new Character('a');
		Integer i1 = Integer.valueOf(1);
		dkm.addToListFor(i1, ca);
		dkm.addToListFor(i1, cb);
		dkm.addToListFor(i1, cc);
		Integer i2 = Integer.valueOf(2);
		dkm.addToListFor(i2, ca);
		dkm.addToListFor(i2, ca);
		Integer i3 = Integer.valueOf(3);
		dkm.addToListFor(i3, cb);
		dkm.addToListFor(i3, cc);
		assertTrue(dkm.containsInList(i1, ca));
		assertFalse(dkm.containsInList(i1, ca1));
		assertFalse(dkm.removeFromListFor(i1, ca1));
		assertTrue(dkm.containsInList(i1, ca));

		assertTrue(dkm.containsInList(i2, ca));
		assertFalse(dkm.containsInList(i2, ca1));
		assertFalse(dkm.removeFromListFor(i2, ca1));
		assertTrue(dkm.containsInList(i2, ca));
		assertTrue(dkm.removeFromListFor(i2, ca));
		// There were two
		assertTrue(dkm.containsInList(i2, ca));
	}

}
