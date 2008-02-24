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
package plugin.lsttokens.editcontext.pcclass;

import java.net.URISyntaxException;

import org.junit.Test;

import pcgen.cdom.inst.CDOMPCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.pcclass.ExclassToken;

public class ExClassIntegrationTest extends
		AbstractIntegrationTestCase<CDOMPCClass>
{

	static ExclassToken token = new ExclassToken();
	static CDOMTokenLoader<CDOMPCClass> loader = new CDOMTokenLoader<CDOMPCClass>(
			CDOMPCClass.class);

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		prefix = "CLASS:";
	}

	@Override
	public Class<CDOMPCClass> getCDOMClass()
	{
		return CDOMPCClass.class;
	}

	@Override
	public CDOMLoader<CDOMPCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMPCClass> getToken()
	{
		return token;
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Cleric");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Wizard");
		commit(modCampaign, tc, "Cleric");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Sorcerer");
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "Sorcerer");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Wizard");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

}
