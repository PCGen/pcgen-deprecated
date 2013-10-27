/*
 * Created on 22-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Vision;
import pcgen.util.enumeration.VisionType;

/**
 * Tests PREVISION token
 */
public class PreVisionTest extends AbstractCharacterTestCase
{

	/**
	 * Main
	 * @param args
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(PreVisionTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreVisionTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testVision2Pass() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final PCTemplate template = new PCTemplate();
		template.addVision(new Vision(VisionType.getVisionType("Darkvision"),
			"60"));
		character.addTemplate(template);

		final PCTemplate template2 = new PCTemplate();
		template2.addVision(new Vision(VisionType.getVisionType("Low-Light"),
			"30"));
		character.addTemplate(template2);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("30");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testVisionFail() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final PCTemplate template = new PCTemplate();
		template
			.addVision(new Vision(VisionType.getVisionType("Normal"), "60"));

		character.addTemplate(template);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator("gteq");
		prereq.setOperand("30");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testVisionPass() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		final PCTemplate template = new PCTemplate();
		template.addVision(new Vision(VisionType.getVisionType("Darkvision"),
			"60"));

		character.addTemplate(template);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("30");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

}
