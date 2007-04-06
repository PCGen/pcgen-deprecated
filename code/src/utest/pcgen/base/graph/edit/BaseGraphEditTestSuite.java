package pcgen.base.graph.edit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({InsertGraphNodeTest.class, DeleteGraphEdgeTest.class,
	DeleteGraphNodeTest.class, InsertGraphEdgeTest.class})
public class BaseGraphEditTestSuite extends TestSuite
{
	// No contents, see annotations
}