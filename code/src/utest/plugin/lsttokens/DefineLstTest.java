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

import java.net.URISyntaxException;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.CDOMStat;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class DefineLstTest extends AbstractGlobalTokenTestCase
{

	static DefineLst token = new DefineLst();
	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		CDOMStat ps = primaryContext.ref.constructCDOMObject(CDOMStat.class, "Strength");
		primaryContext.ref.registerAbbreviation(ps, "STR");
		CDOMStat ss = secondaryContext.ref.constructCDOMObject(CDOMStat.class, "Strength");
		secondaryContext.ref.registerAbbreviation(ss, "STR");
	}

	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoResult() throws PersistenceLayerException
	{
		assertFalse(parse("Medium"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyFormula() throws PersistenceLayerException
	{
		assertFalse(parse("Medium|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyVariable()
		throws PersistenceLayerException
	{
		assertFalse(parse("|Medium"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		assertFalse(parse("Light||Medium"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTwoPipe() throws PersistenceLayerException
	{
		assertFalse(parse("Light|Medium|Heavy"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("VariableName|Formula");
	}

	@Test
	public void testRoundRobinFormula() throws PersistenceLayerException
	{
		runRoundRobin("VariableName|1+2");
	}

	@Test
	public void testRoundRobinJEP() throws PersistenceLayerException
	{
		runRoundRobin("VariableName|CL(\"Fighter\")");
	}
	
	@Test
	public void testRoundRobinLock() throws PersistenceLayerException
	{
		runRoundRobin("LOCK.STR|10");
	}

	@Test
	public void testRoundRobinUnlock() throws PersistenceLayerException
	{
		runRoundRobin("UNLOCK.STR");
	}


}
