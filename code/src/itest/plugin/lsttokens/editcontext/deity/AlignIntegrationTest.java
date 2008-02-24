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
import pcgen.cdom.inst.CDOMAlignment;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.deity.AlignToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;

public class AlignIntegrationTest extends
		AbstractIntegrationTestCase<CDOMDeity>
{
	static AlignToken token = new AlignToken();
	static CDOMTokenLoader<CDOMDeity> loader = new CDOMTokenLoader<CDOMDeity>(
			CDOMDeity.class);

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
			URISyntaxException
	{
		super.setUp();
		primaryContext.ref.constructCDOMObject(CDOMAlignment.class, "LG");
		primaryContext.ref.constructCDOMObject(CDOMAlignment.class, "LN");
		primaryContext.ref.constructCDOMObject(CDOMAlignment.class, "LE");
		primaryContext.ref.constructCDOMObject(CDOMAlignment.class, "NG");
		primaryContext.ref.constructCDOMObject(CDOMAlignment.class, "TN");
		primaryContext.ref.constructCDOMObject(CDOMAlignment.class, "NE");
		primaryContext.ref.constructCDOMObject(CDOMAlignment.class, "CG");
		primaryContext.ref.constructCDOMObject(CDOMAlignment.class, "CN");
		primaryContext.ref.constructCDOMObject(CDOMAlignment.class, "CE");
		secondaryContext.ref.constructCDOMObject(CDOMAlignment.class, "LG");
		secondaryContext.ref.constructCDOMObject(CDOMAlignment.class, "LN");
		secondaryContext.ref.constructCDOMObject(CDOMAlignment.class, "LE");
		secondaryContext.ref.constructCDOMObject(CDOMAlignment.class, "NG");
		secondaryContext.ref.constructCDOMObject(CDOMAlignment.class, "TN");
		secondaryContext.ref.constructCDOMObject(CDOMAlignment.class, "NE");
		secondaryContext.ref.constructCDOMObject(CDOMAlignment.class, "CG");
		secondaryContext.ref.constructCDOMObject(CDOMAlignment.class, "CN");
		secondaryContext.ref.constructCDOMObject(CDOMAlignment.class, "CE");
	}

	@Override
	public Class<CDOMDeity> getCDOMClass()
	{
		return CDOMDeity.class;
	}

	@Override
	public CDOMLoader<CDOMDeity> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMDeity> getToken()
	{
		return token;
	}

	public Object getConstant(String string)
	{
		return primaryContext.ref.getAbbreviatedObject(CDOMAlignment.class, string);
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
