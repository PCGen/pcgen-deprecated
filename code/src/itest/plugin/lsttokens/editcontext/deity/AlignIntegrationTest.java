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
package plugin.lsttokens.editcontext.deity;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Alignment;
import pcgen.core.Deity;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.DeityLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.deity.AlignToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;

public class AlignIntegrationTest extends AbstractIntegrationTestCase<Deity>
{
	static AlignToken token = new AlignToken();
	static DeityLoader loader = new DeityLoader();

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
		URISyntaxException
	{
		super.setUp();
		primaryContext.ref.constructCDOMObject(Alignment.class, "LG");
		primaryContext.ref.constructCDOMObject(Alignment.class, "LN");
		primaryContext.ref.constructCDOMObject(Alignment.class, "LE");
		primaryContext.ref.constructCDOMObject(Alignment.class, "NG");
		primaryContext.ref.constructCDOMObject(Alignment.class, "TN");
		primaryContext.ref.constructCDOMObject(Alignment.class, "NE");
		primaryContext.ref.constructCDOMObject(Alignment.class, "CG");
		primaryContext.ref.constructCDOMObject(Alignment.class, "CN");
		primaryContext.ref.constructCDOMObject(Alignment.class, "CE");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "LG");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "LN");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "LE");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "NG");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "TN");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "NE");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "CG");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "CN");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "CE");
	}

	@Override
	public Class<Deity> getCDOMClass()
	{
		return Deity.class;
	}

	@Override
	public LstObjectFileLoader<Deity> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Deity> getToken()
	{
		return token;
	}

	public Object getConstant(String string)
	{
		return primaryContext.ref.getConstructedCDOMObject(Alignment.class, string);
	}

	public ObjectKey<?> getObjectKey()
	{
		return ObjectKey.ALIGNMENT;
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "TN");
		commit(modCampaign, tc, "LG");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "NG");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "LE");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}
}
