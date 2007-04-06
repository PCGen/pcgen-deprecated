package pcgen.base.graph.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({SimpleListMapGraphTest.class,
	GraphChangeSupportTest.class, EdgeChangeEventTest.class,
	GraphUtilitiesTest.class, DirectionalSetMapGraphTest.class,
	DefaultReferencedGraphEdgeTest.class, DirectionalListMapGraphTest.class,
	DefaultHyperEdgeTest.class, DefaultDirectionalHyperEdgeTest.class,
	DefaultGraphEdgeTest.class, DefaultDirectionalGraphEdgeTest.class,
	NodeChangeEventTest.class, SimpleListGraphTest.class})
public class BaseGraphCoreTestSuite extends TestSuite
{
	// No contents, see annotations
}