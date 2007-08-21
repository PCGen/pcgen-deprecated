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
package plugin.lsttokens.editcontext.testsupport;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public abstract class AbstractListIntegrationTestCase<T extends PObject, TC extends PObject>
		extends AbstractIntegrationTestCase<T>
{

	public abstract Class<TC> getTargetClass();

	public abstract boolean isTypeLegal();

	public abstract boolean isPrereqLegal();

	public abstract boolean isClearLegal();

	public abstract boolean isClearDotLegal();

	public abstract char getJoinCharacter();

	PreClassParser preclass = new PreClassParser();
	PreClassWriter preclasswriter = new PreClassWriter();
	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(preclass);
		TokenRegistration.register(preclasswriter);
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
	}

	@Test
	public void testRoundRobinAdd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "TestWP1");
		commit(modCampaign, tc, "TestWP2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinAddSame() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "TestWP1");
		commit(modCampaign, tc, "TestWP1");
		System.err.println(primaryGraph.getNodeList());
		System.err.println(primaryGraph.getEdgeList());
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinAddType() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "TestWP1");
			commit(modCampaign, tc, "TYPE=TestType");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinAddPrereq() throws PersistenceLayerException
	{
		if (isPrereqLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "TestWP1");
			commit(modCampaign, tc, "TestWP1|PRERACE:1,Human");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinRemovePrereq() throws PersistenceLayerException
	{
		if (isPrereqLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "TestWP1|PRERACE:1,Human");
			commit(modCampaign, tc, "TestWP1");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinStartType() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "TYPE=TestAltType.TestThirdType.TestType");
			commit(modCampaign, tc, "TestWP2");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinDotClear() throws PersistenceLayerException
	{
		if (isClearLegal())
		{
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "TestWP2");
			commit(modCampaign, tc, ".CLEAR");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinDotClearDot() throws PersistenceLayerException
	{
		if (isClearDotLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "TestWP1" + getJoinCharacter() + "TestWP2");
			commit(modCampaign, tc, ".CLEAR.TestWP1");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinDotClearDotAll() throws PersistenceLayerException
	{
		if (isClearDotLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "TestWP1" + getJoinCharacter() + "TestWP2");
			commit(modCampaign, tc, ".CLEAR.TestWP1" + getJoinCharacter()
				+ ".CLEAR.TestWP2");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinNoOriginal() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "TestWP2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoMod() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "TestWP2");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(getTargetClass(), one);
	}
}
