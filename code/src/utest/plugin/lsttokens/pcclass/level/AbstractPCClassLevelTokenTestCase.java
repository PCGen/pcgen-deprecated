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
package plugin.lsttokens.pcclass.level;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMPCClassLevel;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.TokenRegistration;

public abstract class AbstractPCClassLevelTokenTestCase extends TestCase
{
	protected DoubleKeyMapToList<CDOMObject, CDOMReference<?>, AssociatedPrereqObject> primaryGraph;
	protected DoubleKeyMapToList<CDOMObject, CDOMReference<?>, AssociatedPrereqObject> secondaryGraph;
	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected CDOMPCClass primaryProf;
	protected CDOMPCClass secondaryProf;
	protected CDOMPCClassLevel primaryProf1;
	protected CDOMPCClassLevel secondaryProf1;
	protected CDOMPCClassLevel primaryProf2;
	protected CDOMPCClassLevel secondaryProf2;
	protected CDOMPCClassLevel primaryProf3;
	protected CDOMPCClassLevel secondaryProf3;
	protected CDOMTokenLoader<CDOMPCClassLevel> loader = new CDOMTokenLoader<CDOMPCClassLevel>(
			CDOMPCClassLevel.class);

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
		primaryGraph = new DoubleKeyMapToList<CDOMObject, CDOMReference<?>, AssociatedPrereqObject>();
		secondaryGraph = new DoubleKeyMapToList<CDOMObject, CDOMReference<?>, AssociatedPrereqObject>();
		primaryContext = new RuntimeLoadContext(primaryGraph);
		secondaryContext = new RuntimeLoadContext(secondaryGraph);
		primaryProf = 
			primaryContext.ref.constructCDOMObject(CDOMPCClass.class,
					"TestObj");
		secondaryProf =
				secondaryContext.ref.constructCDOMObject(CDOMPCClass.class,
					"TestObj");
		primaryProf1 = primaryProf.getClassLevel(1);
		primaryProf2 = primaryProf.getClassLevel(2);
		primaryProf3 = primaryProf.getClassLevel(3);
		secondaryProf1 = secondaryProf.getClassLevel(1);
		secondaryProf2 = secondaryProf.getClassLevel(2);
		secondaryProf3 = secondaryProf.getClassLevel(3);
	}

	public Class<? extends CDOMPCClassLevel> getCDOMClass()
	{
		return CDOMPCClassLevel.class;
	}

	public static void addToken(LstToken tok)
	{
		TokenLibrary.addToTokenMap(tok);
	}

	public void runRoundRobin(String... str) throws PersistenceLayerException
	{
		// Default is not to write out anything
		assertNull(getToken().unparse(primaryContext, primaryProf1));
		assertNull(getToken().unparse(primaryContext, primaryProf2));
		assertNull(getToken().unparse(primaryContext, primaryProf3));
		// Ensure the graphs are the same at the start
		assertEquals(primaryGraph, secondaryGraph);

		// Set value
		for (String s : str)
		{
			assertTrue(parse(s, 2));
		}
		// Doesn't pollute other levels
		assertNull(getToken().unparse(primaryContext, primaryProf1));
		assertNull(getToken().unparse(primaryContext, primaryProf3));
		// Get back the appropriate token:
		String[] unparsed = getToken().unparse(primaryContext, primaryProf2);

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
		loader.parseLine(secondaryContext, secondaryProf2, unparsedBuilt
				.toString(), testCampaign.getURI());

		// Ensure the objects are the same
		assertEquals(primaryProf, secondaryProf);

		// Ensure the graphs are the same
		assertEquals(primaryGraph, secondaryGraph);

		// And that it comes back out the same again
		// Doesn't pollute other levels
		assertNull(getToken().unparse(secondaryContext, secondaryProf1));
		assertNull(getToken().unparse(secondaryContext, secondaryProf3));
		String[] sUnparsed =
				getToken().unparse(secondaryContext, secondaryProf2);
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

	public abstract CDOMPrimaryToken<CDOMPCClassLevel> getToken();

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
		boolean b = getToken().parse(primaryContext, primaryProf.getClassLevel(level), str);
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
				getToken().parse(secondaryContext, secondaryProf.getClassLevel(level), str);
		if (b)
		{
			secondaryContext.commit();
		}
		return b;
	}
}
