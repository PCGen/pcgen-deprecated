package pcgen.base.graph.command;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({InsertNodesCommandTest.class, InsertNodeCommandTest.class,
	InsertEdgesCommandTest.class, InsertEdgeCommandTest.class,
	DeleteNodesCommandTest.class, DeleteNodeCommandTest.class,
	DeleteEdgesCommandTest.class, DeleteEdgeCommandTest.class,
	TransferAdjacentGraphEdgesToNewNodeCommandTest.class,
	PruneUnconnectedToNodeCommandTest.class, PruneFromNodeCommandTest.class,
	PruneFromEdgeCommandTest.class, GraftFromEdgeCommandTest.class,
	GraftFromNodeCommandTest.class, ReplaceGraphNodeCommandTest.class})
public class BaseGraphCommandTestSuite extends TestSuite
{
	// No contents, see annotations
}