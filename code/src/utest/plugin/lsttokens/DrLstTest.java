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

public class DrLstTest extends AbstractGlobalTokenTestCase
{
	static GlobalLstToken token = new DrLst();
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
	public void testInvalidNoSlash() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "+1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNoReduction() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNoKey() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "/+3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTwoSlash() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/3/+3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNoOrSpace() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/+3 or"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNoOrSuffix() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/+3 or "));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNoAndSpace() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/+3 and"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNoAndSuffix() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/+3 and "));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNoSpaceOr() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/or +3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNoOrPrefix() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/ or +3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNoSpaceAnd() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/and +3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNoAndPrefix() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/ and +3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinNormal() throws PersistenceLayerException
	{
		runRoundRobin("10/+1");
	}

	@Test
	public void testRoundRobinComplexOr() throws PersistenceLayerException
	{
		runRoundRobin("10/+1 or +2");
	}

	@Test
	public void testRoundRobinMultiple() throws PersistenceLayerException
	{
		runRoundRobin("10/+1", "5/+2");
	}
}
