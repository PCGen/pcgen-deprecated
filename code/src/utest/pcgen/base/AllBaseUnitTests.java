package pcgen.base;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

import pcgen.base.graph.command.BaseGraphCommandTestSuite;
import pcgen.base.graph.core.BaseGraphCoreTestSuite;
import pcgen.base.graph.edit.BaseGraphEditTestSuite;
import pcgen.base.graph.monitor.BaseGraphMonitorTestSuite;
import pcgen.base.graph.visitor.BaseGraphVisitorTestSuite;
import pcgen.base.util.BaseUtilTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({BaseGraphCommandTestSuite.class,
	BaseGraphCoreTestSuite.class, BaseGraphEditTestSuite.class,
	BaseGraphMonitorTestSuite.class, BaseGraphVisitorTestSuite.class,
	BaseUtilTestSuite.class})
public class AllBaseUnitTests extends TestSuite
{
	// No contents, see annotations
}