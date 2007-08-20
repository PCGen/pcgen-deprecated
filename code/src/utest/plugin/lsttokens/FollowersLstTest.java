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

import pcgen.core.CompanionList;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class FollowersLstTest extends AbstractGlobalTokenTestCase
{

	static GlobalLstToken token = new FollowersLst();
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
	public void testInvalidTypeOnly() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "Follower"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTypeBarOnly() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "Follower|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidEmptyType() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "|4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTwoPipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "Follower||4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTwoPipeTypeTwo() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "Follower|Pet|4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidBarEnding() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "Follower|4|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidBarStarting() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "|Follower|4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidReversed() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CompanionList.class, "Follower");
		assertTrue(token.parse(primaryContext, primaryProf, "Formula|Follower"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CompanionList.class, "Follower");
		secondaryContext.ref.constructCDOMObject(CompanionList.class,
			"Follower");
		runRoundRobin("Follower|4");
	}

	@Test
	public void testRoundRobinFormula() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CompanionList.class, "Follower");
		secondaryContext.ref.constructCDOMObject(CompanionList.class,
			"Follower");
		runRoundRobin("Follower|4+1");
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CompanionList.class, "Follower");
		secondaryContext.ref.constructCDOMObject(CompanionList.class,
			"Follower");
		primaryContext.ref.constructCDOMObject(CompanionList.class, "Pet");
		secondaryContext.ref.constructCDOMObject(CompanionList.class, "Pet");
		runRoundRobin("Follower|4+1", "Pet|PetForm");
	}

}