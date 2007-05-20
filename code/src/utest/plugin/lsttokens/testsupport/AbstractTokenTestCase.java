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
package plugin.lsttokens.testsupport;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;

import pcgen.cdom.graph.PCGenGraph;
import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstLoader;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;

public abstract class AbstractTokenTestCase<T extends PObject> extends TestCase
{
	protected PCGenGraph primaryGraph;
	protected PCGenGraph secondaryGraph;
	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected T primaryProf;
	protected T secondaryProf;
	protected String prefix = "";
	protected int expectedPrimaryMessageCount = 0;

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
		primaryContext = new LoadContext(primaryGraph);
		secondaryContext = new LoadContext(secondaryGraph);
		URI testURI = testCampaign.getURI();
		primaryContext.obj.setSourceURI(testURI);
		primaryContext.obj.setExtractURI(testURI);
		secondaryContext.obj.setSourceURI(testURI);
		secondaryContext.obj.setExtractURI(testURI);
		primaryProf =
				primaryContext.ref.constructCDOMObject(getCDOMClass(),
					"TestObj");
		secondaryProf =
				secondaryContext.ref.constructCDOMObject(getCDOMClass(),
					"TestObj");
		expectedPrimaryMessageCount = 0;
	}

	public abstract Class<? extends T> getCDOMClass();

	public static void addToken(LstToken tok)
	{
		TokenStore.inst().addToTokenMap(tok);
	}

	public void runRoundRobin(String... str) throws PersistenceLayerException
	{
		// Default is not to write out anything
		assertNull(getToken().unparse(primaryContext, primaryProf));
		// Ensure the graphs are the same at the start
		assertEquals(primaryGraph, secondaryGraph);

		// Set value
		for (String s : str)
		{
			assertTrue(getToken().parse(primaryContext, primaryProf, s));
		}
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);

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
		getLoader().parseLine(secondaryContext, secondaryProf,
			prefix + "TestObj\t" + unparsedBuilt.toString(), testCampaign);

		// Ensure the objects are the same
		assertEquals(primaryProf, secondaryProf);

		// Ensure the graphs are the same
		assertEquals(primaryGraph, secondaryGraph);

		// And that it comes back out the same again
		String[] sUnparsed =
				getToken().unparse(secondaryContext, secondaryProf);
		assertEquals(unparsed.length, sUnparsed.length);

		for (int i = 0; i < unparsed.length; i++)
		{
			assertEquals("Expected " + i + " item to be equal", unparsed[i],
				sUnparsed[i]);
		}
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
		assertEquals(expectedPrimaryMessageCount, primaryContext
			.getWriteMessageCount());
		assertEquals(0, secondaryContext.getWriteMessageCount());
	}

	public abstract LstLoader<T> getLoader();

	public abstract CDOMToken<T> getToken();

}
