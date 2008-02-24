/*
 * AbstractTokenLoaderTestCase.java
 * Copyright 2007 (C) Koen Van Daele
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 31-jul-07
 *
 * $Id$
 */
package plugin.lsttokens.loader.testsupport;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.EditorLoadContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * <code>AbstractTokenLoaderTestCase</code> is an abstract test case for
 * testing that tokens get cleared correctly in LoadContext
 * 
 * @author Koen Van Daele <vandaelek@users.sourceforge.net>
 * @version $Revision$
 */
public abstract class AbstractTokenLoaderTestCase<T extends CDOMObject> extends TestCase
{
	protected PCGenGraph graph;
	protected LoadContext context;
	protected T prof;

	private static boolean classSetUpFired = false;

	protected static CampaignSourceEntry sourceCampaign;
	protected static CampaignSourceEntry modCampaign;

	@BeforeClass
	public static final void classSetUp() throws URISyntaxException
	{
		sourceCampaign =
				new CampaignSourceEntry(new Campaign(), new URI(
					"file:/Test%20Case%20Source"));
		modCampaign =
				new CampaignSourceEntry(new Campaign(), new URI(
					"file:/Test%20Case%20Mod"));
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
		graph = new PCGenGraph();
		context = new RuntimeLoadContext(graph);
		prof = context.ref.constructCDOMObject(getCDOMClass(), "TestObj");

		URI sourceURI = sourceCampaign.getURI();
		context.getObjectContext().setSourceURI(sourceURI);
		context.getObjectContext().setExtractURI(sourceURI);
	}

	public char getJoinCharacter()
	{
		return '|';
	}

	/*
	 * Provide an array of testdata.
	 */
	public abstract String[] getTestArray();

	/*
	 * Test Clearing the provided testArray in Runtime Context
	 */
	public void testTestArrayInRuntimeContext()
		throws PersistenceLayerException
	{
		runClearAllTest(getTestArray());
	}
	
	/*
	 * Test clearing the context before adding the testData to ensure that it doesn't 
	 * get cleared.
	 */
	public void testPreClearTestArrayInRuntimeContext() 
		throws PersistenceLayerException
	{
		runPreClearAllTest(getTestArray());
	}
	/*
	 * Try to clear every element in the testarray.
	 */
	public void testDotClearTestArrayInRuntimeContext() 
		throws PersistenceLayerException, URISyntaxException
	{
		runClearAllIndexesTest(getTestArray());
	}

	/*
	 * Test the provided testArray in Editor Context
	 */
	public void testClearTestArrayInEditorContext() 
		throws PersistenceLayerException
	{
		context = new EditorLoadContext();
		runClearAllTest(getTestArray());
	}
	
	public void testPreClearTestArrayInEditorContext() 
		throws PersistenceLayerException
	{
		context = new EditorLoadContext();
		runPreClearAllTest(getTestArray());
	}
	
	public void testDotClearTestArrayInEditorContext() 
		throws PersistenceLayerException, URISyntaxException
	{
		context = new EditorLoadContext();
		runClearAllIndexesTest(getTestArray());
	}

	protected abstract boolean isClearable();

	protected abstract boolean isDotClearable();

	public abstract CDOMPrimaryToken<T> getToken();

	public abstract CDOMLoader<T> getLoader();

	public abstract Class<T> getCDOMClass();

	protected void testParse(LoadContext context, String tok)
		throws PersistenceLayerException
	{
		assertTrue("Couldn't parse" + getToken().getTokenName() + " in "
			+ context.getContextType() + " Context.", getToken().parse(context,
			prof, tok));
		context.commit();
	}

	protected void testUnparse(LoadContext context, String... str)
		throws PersistenceLayerException
	{
		String[] unparsed = getToken().unparse(context, prof);
		assertEquals(str.length, unparsed.length);

		for (int i = 0; i < str.length; i++)
		{
			assertEquals("Expected " + i + " item to be equal in "
				+ context.getContextType() + " Context.", str[i], unparsed[i]);
		}
	}

	/**
	 * Test is the token was successfully cleared.
	 * 
	 * @param context
	 * @throws PersistenceLayerException
	 */
	protected void testCleared(LoadContext context)
		throws PersistenceLayerException
	{
		// TODO Rewrite this using inner classes as a Strategy.
		if (context.getContextType() == "Runtime")
		{
			assertNull(getToken().unparse(context, prof));
		}
		if (context.getContextType() == "Editor")
		{
			String[] unparsed = getToken().unparse(context, prof);
			for (int i = 0; i < unparsed.length; i++)
			{
				assertEquals("Expected " + i
					+ " item to be .CLEAR in Editor Context.", ".CLEAR",
					unparsed[i]);
			}
		}

	}

	public void runClearAllTest(String... str) throws PersistenceLayerException
	{
		if (!isClearable()) {
			return;
		}
		for (String s : str)
		{
			testParse(context, s);
		}
		testParse(context, ".CLEAR");
		// Get back the appropriate token:
		testCleared(context);
	}

	public void runPreClearAllTest(String... str) throws PersistenceLayerException
	{
		if (!isClearable()) {
			return;
		}
		testParse(context,".CLEAR");
		for (String s : str) {
			testParse(context,s);
		}
		assertNotNull(getToken().unparse(context, prof));
	}
	
	/*
	 * Clear each element of the array and test the results
	 */
	public void runClearAllIndexesTest(String... str) 
		throws PersistenceLayerException, URISyntaxException
	{
		if (!isDotClearable()) {
			return;
		}
		for (int i = 0; i < str.length; i++) {
			setUp();
			runClearIndexTest(i,str);
		}
	}

	/*
	 * Clear the element at the specified index and assert that the result is
	 * correct
	 */
	protected void runClearIndexTest(int index, String... str)
		throws PersistenceLayerException
	{
		// parse everything
		for (String s : str)
		{
			testParse(context, s);
		}
		// clear the requested index
		testParse(context, ".CLEAR." + str[index]);
		String[] unparsed = getToken().unparse(context, prof);
		// assert that the return array contains only 1 element in LoadContext
		// Not sure if this should always be so...
		assertEquals(unparsed.length, 1);

		// assert that the correct index was removed
		ArrayList<String> strL = new ArrayList<String>(Arrays.asList(str));
		assertEquals(str[index], strL.remove(index));

		// make a string containing everything but the removed element
		// and compare it to the unparsed token
		String indexCleared =
				StringUtil.join(strL, String.valueOf(getJoinCharacter()));
		assertEquals(indexCleared, unparsed[0]);
	}
}
