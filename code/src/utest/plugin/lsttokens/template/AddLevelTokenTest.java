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

import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class AddLevelTokenTest extends AbstractTokenTestCase<PCTemplate>
{
	static AddLevelToken token = new AddLevelToken();
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
	public void testInvalidInputNoPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter:3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNoClass() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNoLevelCount() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter| "));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTwoPipes() throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "Fighter|3|3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDecimalLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "Fighter|3.5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNegativeLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter|-5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputZeroLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter|0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNotAClass() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "NotAClass|3"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("Fighter|3");
	}

	@Test
	public void testRoundRobinMultiple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		primaryContext.ref.constructCDOMObject(PCClass.class, "Thief");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Thief");
		runRoundRobin("Fighter|3", "Thief|4");
	}

}