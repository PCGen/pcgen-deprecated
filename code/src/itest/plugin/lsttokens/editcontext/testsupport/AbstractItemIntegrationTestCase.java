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

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;

public abstract class AbstractItemIntegrationTestCase<T extends CDOMObject, TC extends CDOMObject>
		extends AbstractIntegrationTestCase<T>
{

	public abstract Class<TC> getTargetClass();

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
	public void testRoundRobinOverwrite() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "TestWP1");
		commit(testCampaign, tc, "TestWP2");
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
		completeRoundRobin(tc);
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
