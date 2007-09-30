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
package pcgen.tokenruntimeload;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;

import pcgen.cdom.graph.PCGenGraph;
import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.persistence.EditorLoadContext;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstLoader;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;

public abstract class AbstractIntegrationTestCase<T extends PObject> extends
		TestCase
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
	protected static CampaignSourceEntry modCampaign;

	public abstract LstLoader<T> getLoader();

	@BeforeClass
	public static final void classSetUp() throws URISyntaxException
	{
		testCampaign =
				new CampaignSourceEntry(new Campaign(), new URI(
					"file:/Test%20Case"));
		modCampaign =
				new CampaignSourceEntry(new Campaign(), new URI(
					"file:/Test%20Case%20Modifier"));
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
		primaryGraph = new PCGenGraph();
		secondaryGraph = new PCGenGraph();
		primaryContext = new EditorLoadContext();
		secondaryContext = new EditorLoadContext();
		primaryProf =
				primaryContext.ref.constructCDOMObject(getCDOMClass(),
					"TestObj");
		secondaryProf =
				secondaryContext.ref.constructCDOMObject(getCDOMClass(),
					"TestObj");
	}

	public abstract Class<? extends T> getCDOMClass();

	public static void addToken(LstToken tok)
	{
		TokenStore.inst().addToTokenMap(tok);
	}

	protected void verifyClean()
	{
		// Ensure the graphs are the same at the start
		assertEquals(primaryGraph, secondaryGraph);
		// Ensure the graphs are the same at the start
		assertTrue(primaryProf.isCDOMEqual(secondaryProf));
	}
}
