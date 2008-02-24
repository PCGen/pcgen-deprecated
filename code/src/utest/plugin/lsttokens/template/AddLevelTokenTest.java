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

import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class AddLevelTokenTest extends AbstractTokenTestCase<CDOMTemplate>
{
	static AddLevelToken token = new AddLevelToken();
	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMTemplate> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputNoPipe() throws PersistenceLayerException
	{
		assertFalse(parse("Fighter:3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoClass() throws PersistenceLayerException
	{
		assertFalse(parse("|3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoLevelCount() throws PersistenceLayerException
	{
		assertFalse(parse("Fighter|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fighter| "));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTwoPipes() throws PersistenceLayerException
	{
		assertFalse(parse("Fighter|3|3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDecimalLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fighter|3.5"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativeLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fighter|-5"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputZeroLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fighter|0"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotAClass() throws PersistenceLayerException
	{
		assertTrue(parse("NotAClass|3"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Fighter");
		runRoundRobin("Fighter|3");
	}

	@Test
	public void testRoundRobinMultiple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Fighter");
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Thief");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Thief");
		runRoundRobin("Fighter|3", "Thief|4");
	}

}