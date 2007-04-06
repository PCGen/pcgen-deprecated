package pcgen.base.graph.visitor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({NodeDistanceCalculationTest.class,
	GraphHeapComponentTest.class, DijkstraNodeAlgorithmTest.class,
	DijkstraEdgeAlgorithmTest.class, DepthFirstTraverseAlgorithmTest.class,
	BreadthFirstTraverseAlgorithmTest.class})
public class BaseGraphVisitorTestSuite extends TestSuite
{
	// No contents, see annotations
}