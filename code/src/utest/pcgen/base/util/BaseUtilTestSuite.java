package pcgen.base.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({MapCollectionTest.class, ListSetTest.class,
	HashMapToListTest.class, HashMapToInstanceListTest.class,
	DoubleKeyMapToInstanceListTest.class, DoubleKeyMapTest.class,
	DefaultMapTest.class, TripleKeyMapTest.class, TypeSafeMapTest.class,
	WeightedCollectionTest.class})
public class BaseUtilTestSuite extends TestSuite
{
	// No contents, see annotations
}