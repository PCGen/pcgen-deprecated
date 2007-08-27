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
package plugin.lsttokens.pcclass;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.core.Campaign;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.RuntimeLoadContext;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.PCClassLevelLoader;
import pcgen.persistence.lst.PCClassLevelLstToken;
import pcgen.persistence.lst.TokenStore;
import plugin.lsttokens.testsupport.TokenRegistration;

public abstract class AbstractPCClassLevelTokenTestCase extends TestCase
{
	protected PCGenGraph primaryGraph;
	protected PCGenGraph secondaryGraph;
	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected PCClass primaryProf;
	protected PCClass secondaryProf;

	private static boolean classSetUpFired = false;
	protected static CampaignSourceEntry testCampaign;

	@BeforeClass
	public static final void classSetUp() throws URISyntaxException
	{
		testCampaign =
				new CampaignSourceEntry(new Campaign(), new URI(
					"file:/Test%20Case"));
		classSetUpFired = true;
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		if (!classSetUpFired)
		{
			classSetUp();
		}
		// Yea, this causes warnings...
		TokenRegistration.register(getToken());
		primaryGraph = new PCGenGraph();
		secondaryGraph = new PCGenGraph();
		primaryContext = new RuntimeLoadContext(primaryGraph);
		secondaryContext = new RuntimeLoadContext(secondaryGraph);
		primaryProf =
				primaryContext.ref.constructCDOMObject(getCDOMClass(),
					"TestObj");
		secondaryProf =
				secondaryContext.ref.constructCDOMObject(getCDOMClass(),
					"TestObj");
	}

	public Class<? extends PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	public static void addToken(LstToken tok)
	{
		TokenStore.inst().addToTokenMap(tok);
	}

	public void runRoundRobin(String... str) throws PersistenceLayerException
	{
		// Default is not to write out anything
		assertNull(getToken().unparse(primaryContext, primaryProf, 1));
		assertNull(getToken().unparse(primaryContext, primaryProf, 2));
		assertNull(getToken().unparse(primaryContext, primaryProf, 3));
		// Ensure the graphs are the same at the start
		assertEquals(primaryGraph, secondaryGraph);

		// Set value
		for (String s : str)
		{
			assertTrue(parse(s, 2));
		}
		// Doesn't pollute other levels
		assertNull(getToken().unparse(primaryContext, primaryProf, 1));
		assertNull(getToken().unparse(primaryContext, primaryProf, 3));
		// Get back the appropriate token:
		String[] unparsed = getToken().unparse(primaryContext, primaryProf, 2);

		assertEquals(str.length, unparsed.length);

		for (int i = 0; i < str.length; i++)
		{
			assertEquals("Expected " + i + " item to be equal", str[i],
				unparsed[i]);
		}

		// Do round Robin
		StringBuilder unparsedBuilt = new StringBuilder();
		for (String s : unparsed)
		{
			unparsedBuilt.append(getToken().getTokenName()).append(':').append(
				s).append('\t');
		}
		PCClassLevelLoader.parseLine(secondaryContext, secondaryProf,
			unparsedBuilt.toString(), testCampaign, 2);

		// Ensure the objects are the same
		assertEquals(primaryProf, secondaryProf);

		// Ensure the graphs are the same
		assertEquals(primaryGraph, secondaryGraph);

		// And that it comes back out the same again
		// Doesn't pollute other levels
		assertNull(getToken().unparse(secondaryContext, secondaryProf, 1));
		assertNull(getToken().unparse(secondaryContext, secondaryProf, 3));
		String[] sUnparsed =
				getToken().unparse(secondaryContext, secondaryProf, 2);
		assertEquals(unparsed.length, sUnparsed.length);

		for (int i = 0; i < unparsed.length; i++)
		{
			assertEquals("Expected " + i + " item to be equal", unparsed[i],
				sUnparsed[i]);
		}
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
		assertEquals(0, primaryContext.getWriteMessageCount());
		assertEquals(0, secondaryContext.getWriteMessageCount());
	}

	public abstract PCClassLevelLstToken getToken();

	public void isCDOMEqual(CDOMObject cdo1, CDOMObject cdo2)
	{
		assertTrue(cdo1.isCDOMEqual(cdo2));
	}

	public void assertNoSideEffects()
	{
		assertTrue(primaryGraph.isEmpty());
		isCDOMEqual(primaryProf, secondaryProf);
		assertFalse(primaryContext.getListContext().hasMasterLists());
		assertEquals(primaryGraph, secondaryGraph);
	}

	public boolean parse(String str, int level)
		throws PersistenceLayerException
	{
		boolean b = getToken().parse(primaryContext, primaryProf, str, level);
		if (b)
		{
			primaryContext.commit();
		}
		return b;
	}

	public boolean parseSecondary(String str, int level)
		throws PersistenceLayerException
	{
		boolean b =
				getToken().parse(secondaryContext, secondaryProf, str, level);
		if (b)
		{
			secondaryContext.commit();
		}
		return b;
	}
}
