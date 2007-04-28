/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.pcclass;

import java.net.URISyntaxException;

import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class AttackCycleTokenTest extends AbstractTokenTestCase<PCClass>
{

	static AttackcycleToken token = new AttackcycleToken();
	static PCClassLoaderFacade loader = new PCClassLoaderFacade();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		prefix = "CLASS:";
	}

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public LstLoader getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<PCClass> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNoCycle() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "BAB"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyCycle() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "BAB|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyType() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputOpenStart() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|BAB|3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputOpenEnd() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "BAB|4|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoublePipeTypeOne()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "BAB||5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoublePipeTypeTwo()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"BAB|5||UAB|5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoublePipeTypeThree()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"BAB|5|UAB||4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputGAB() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "GAB|5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputGABSecond() throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "BAB|4|GAB|5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinBab() throws PersistenceLayerException
	{
		runRoundRobin("BAB|3");
	}

	@Test
	public void testRoundRobinRab() throws PersistenceLayerException
	{
		runRoundRobin("RAB|4");
	}

	@Test
	public void testRoundRobinUab() throws PersistenceLayerException
	{
		runRoundRobin("UAB|5");
	}

	@Test
	public void testRoundRobinMixed() throws PersistenceLayerException
	{
		runRoundRobin("BAB|3|RAB|4|UAB|5");
	}
}
