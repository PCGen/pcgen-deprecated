/*
 * PreVisionParserTest.java
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
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
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.core.prereq.Prerequisite;
import plugin.pretokens.parser.PreVisionParser;

/**
 * @author wardc
 *
 */
public class PreVisionParserTest extends TestCase
{
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args)
	{
		TestRunner.run(PreVisionParserTest.class);
	}

	/**
	 * @return Test 
	 */
	public static Test suite()
	{
		return new TestSuite(PreVisionParserTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testMultiplePasses() throws Exception
	{
		PreVisionParser parser = new PreVisionParser();

		Prerequisite prereq =
				parser.parse("VISION", "1,Blindsight,Darkvision=30", false,
					false);

		assertEquals(
			"<prereq operator=\"gteq\" operand=\"1\" >\n"
				+ "<prereq kind=\"vision\" count-multiples=\"true\" key=\"Blindsight\" operator=\"gteq\" operand=\"30\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"vision\" count-multiples=\"true\" key=\"Darkvision\" operator=\"gteq\" operand=\"30\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString());
	}
}
