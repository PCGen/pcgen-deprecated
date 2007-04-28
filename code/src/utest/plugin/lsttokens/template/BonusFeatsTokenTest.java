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
package plugin.lsttokens.template;

import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class BonusFeatsTokenTest extends AbstractTokenTestCase<PCTemplate>
{

	static BonusfeatsToken token = new BonusfeatsToken();
	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<PCTemplate> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInput() throws PersistenceLayerException
	{
		//Always ensure get is unchanged
		// since no invalid item should set or reset the value
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "TestWP"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "String"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TYPE=TestType"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TYPE.TestType"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "ALL"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "ANY"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "FIVE"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "4.5"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "1/2"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "1+3"));
		assertEquals(primaryGraph, secondaryGraph);
		//Require Integer greater than zero
		assertFalse(getToken().parse(primaryContext, primaryProf, "-1"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "0"));
		assertEquals(primaryGraph, secondaryGraph);
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "5"));

		assertTrue(getToken().parse(primaryContext, primaryProf, "1"));

	}

	@Test
	public void testRoundRobinFive() throws PersistenceLayerException
	{
		runRoundRobin("5");
	}

}
