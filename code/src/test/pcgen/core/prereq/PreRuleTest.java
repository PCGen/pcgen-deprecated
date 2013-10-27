/*
 * PreRuleTest.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on February 6, 2007
 *
 * Current Ver: $Revision: 1777 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 15:36:01 +1100 (Sun, 17 Dec 2006) $
 *
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.RuleCheck;
import pcgen.core.SettingsHandler;
import plugin.pretokens.parser.PreRuleParser;

/**
 * <code>PreRuleTest</code> checks the fucntion of the rule 
 * prereq tester.
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public class PreRuleTest extends AbstractCharacterTestCase
{
	/**
	 * Runs the test.
	 * @param args
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(PreRuleTest.class);
	}

	/* (non-Javadoc)
	 * @see pcgen.PCGenTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		// TODO Auto-generated method stub
		super.setUp();
		RuleCheck preRule = new RuleCheck();
		preRule.setName("PRERULE");
		preRule.setDefault("N");
		GameMode gameMode = SettingsHandler.getGame();
		gameMode.addRule(preRule);
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		// TODO Auto-generated method stub
		super.tearDown();
	}

	/**
	 * Returns a TestSuite consisting of all the tests in this class.
	 * 
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreRuleTest.class);
	}

	/**
	 * Test to ensure that we return false when races don't match.
	 * 
	 * @throws Exception
	 */
	public void testRuleDisabled() throws Exception
	{
		assertFalse("Our rule should start as false", Globals
			.checkRule("PRERULE"));

		PreRuleParser parser = new PreRuleParser();
		Prerequisite prereq = parser.parse("RULE", "PRERULE", false, false);

		boolean passes = PrereqHandler.passes(prereq, getCharacter(), null);
		assertFalse("PreRule should fail when rule is disabled.", passes);

		prereq = parser.parse("RULE", "PRERULE", true, false);
		passes = PrereqHandler.passes(prereq, getCharacter(), null);
		assertTrue("!PreRule should pass when rule is disabled.", passes);
	}

	/**
	 * Test to ensure that we return false when races don't match.
	 * 
	 * @throws Exception
	 */
	public void testRuleEnabled() throws Exception
	{
		RuleCheck preRule = new RuleCheck();
		preRule.setName("PRERULE");
		preRule.setDefault("Y");
		GameMode gameMode = SettingsHandler.getGame();
		gameMode.addRule(preRule);
		
		assertTrue("Our rule should now be true", Globals
			.checkRule("PRERULE"));

		PreRuleParser parser = new PreRuleParser();
		Prerequisite prereq = parser.parse("RULE", "PRERULE", false, false);

		boolean passes = PrereqHandler.passes(prereq, getCharacter(), null);
		assertTrue("PreRule should pass when rule is enabled.", passes);

		prereq = parser.parse("RULE", "PRERULE", true, false);
		passes = PrereqHandler.passes(prereq, getCharacter(), null);
		assertFalse("!PreRule should fail when rule is enabled.", passes);
	}
}
