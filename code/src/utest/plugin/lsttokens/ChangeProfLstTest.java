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
package plugin.lsttokens;

import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class ChangeProfLstTest extends AbstractGlobalTokenTestCase
{

	static GlobalLstToken token = new ChangeprofLst();
	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSourceOnly() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "Hammer"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSourceEqualOnly() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "Hammer="));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSourceEqualOnlyTypeTwo()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"Hammer=Martial|Pipe="));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidEmptySource() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "=Martial"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTwoEquals() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "Hammer==Martial"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTwoEqualsTypeTwo() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"Hammer=TYPE.Heavy=Martial"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidBarEnding() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "Hammer=Martial|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidBarStarting() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "|Hammer=Martial"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDoublePipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"Hammer=Martial||Pipe=Exotic"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidReversed() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "Martial=Hammer"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidResultPrimitive() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "Hammer=Pipe"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidResultType() throws PersistenceLayerException
	{
		try
		{
			assertFalse(token.parse(primaryContext, primaryProf,
				"Hammer=TYPE.Heavy"));
		}
		catch (IllegalArgumentException e)
		{
			// This is okay too
		}
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		runRoundRobin("Hammer=Martial");
	}

	@Test
	public void testRoundRobinTwo() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Pipe");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Pipe");
		runRoundRobin("Hammer,Pipe=Martial");
	}

	@Test
	public void testRoundRobinType() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		runRoundRobin("Hammer,TYPE.Heavy=Martial");
	}

	@Test
	public void testRoundRobinTwoResult() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Pipe");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Pipe");
		runRoundRobin("Hammer=Martial|Pipe=Exotic");
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Pipe");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Pipe");
		runRoundRobin("Hammer,TYPE.Heavy,TYPE.Medium=Martial|Nail,TYPE.Crazy,TYPE.Disposable=Exotic");
	}
}