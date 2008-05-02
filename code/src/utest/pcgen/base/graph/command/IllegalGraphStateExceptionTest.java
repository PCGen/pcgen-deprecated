package pcgen.base.graph.command;

import junit.framework.TestCase;

import org.junit.Test;

public class IllegalGraphStateExceptionTest extends TestCase {

	@Test
	public void testEmptyConstructor()
	{
		String expectedResult = "Message"; 
		IllegalGraphStateException illegalGraphStateException = new IllegalGraphStateException("Message");
		assertNotNull(illegalGraphStateException);
		String result = illegalGraphStateException.getMessage();
		assertTrue(result.equals(expectedResult));
	}
	
}
