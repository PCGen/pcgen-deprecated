package pcgen.base.graph.monitor;

import junit.framework.TestCase;

import org.junit.Test;

public class GraphMismatchExceptionTest extends TestCase {

	@Test
	public void testConstructor()
	{
		String expectedResult = "Message"; 
		GraphMismatchException graphMismatchException = new GraphMismatchException("Message");
		assertNotNull(graphMismatchException);
		String result = graphMismatchException.getMessage();
		assertTrue(result.equals(expectedResult));
	}

	@Test
	public void testEmptyConstructor()
	{
		GraphMismatchException graphMismatchException = new GraphMismatchException();
		assertNotNull(graphMismatchException);
	}
	
}
