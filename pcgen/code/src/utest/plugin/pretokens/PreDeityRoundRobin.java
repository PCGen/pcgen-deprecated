/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreDeityParser;
import plugin.pretokens.writer.PreDeityWriter;
import plugin.pretokens.writer.PreHasDeityWriter;

public class PreDeityRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreDeityRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreDeityRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreDeityParser());
		TokenRegistration.register(new PreDeityWriter());
		TokenRegistration.register(new PreHasDeityWriter());
	}

	@Override
	public String getBaseString()
	{
		return "DEITY";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}

	public void testY()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,Y");
	}

	public void testN()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,N");
	}

	public void testPantheon()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,PANTHEON.Foo");
	}

	public void testMultiplePantheon()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,PANTHEON.Bar,PANTHEON.Foo");
	}

	public void testPantheonComplex()
	{
		runRoundRobin("PRE" + getBaseString() + ":3,Foo,PANTHEON.Bar");
	}

}
