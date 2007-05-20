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
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class DefineLstTest extends AbstractGlobalTokenTestCase
{

	static DefineLst token = new DefineLst();
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
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNoResult() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Medium"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyFormula() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Medium|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyVariable()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|Medium"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Light||Medium"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTwoPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Light|Medium|Heavy"));
		assertTrue(primaryGraph.isEmpty());
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
}
