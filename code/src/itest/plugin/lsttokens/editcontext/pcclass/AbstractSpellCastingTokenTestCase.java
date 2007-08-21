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

import org.junit.Test;

import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLevelLoader;

public abstract class AbstractSpellCastingTokenTestCase extends
		AbstractPCClassLevelTokenTestCase
{

	@Override
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
			assertTrue(getToken().parse(primaryContext, primaryProf, s, 2));
		}
		// Doesn't pollute other levels
		assertNull(getToken().unparse(primaryContext, primaryProf, 1));
		// Get back the appropriate token:
		String[] unparsed = getToken().unparse(primaryContext, primaryProf, 2);

		assertEquals(str.length, unparsed.length);

		for (int i = 0; i < str.length; i++)
		{
			assertEquals("Expected " + i + " item to be equal", str[i],
				unparsed[i]);
		}

		// And works for subsequent levels
		unparsed = getToken().unparse(primaryContext, primaryProf, 3);

		assertEquals(str.length, unparsed.length);

		for (int i = 0; i < str.length; i++)
		{
			assertEquals("Expected SL " + i + " item to be equal", str[i],
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
		String[] sUnparsed =
				getToken().unparse(secondaryContext, secondaryProf, 2);
		assertEquals(unparsed.length, sUnparsed.length);

		for (int i = 0; i < unparsed.length; i++)
		{
			assertEquals("Expected " + i + " item to be equal", unparsed[i],
				sUnparsed[i]);
		}
	}

	@Test
	public void testInvalidListEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "", 2));
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "1,", 2));
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ",1", 2));
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "1,,2", 2));
	}

	@Test
	public void testInvalidListNegativeNumber()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "1,-2", 2));
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("3");
	}

	@Test
	public void testRoundRobinList() throws PersistenceLayerException
	{
		runRoundRobin("3,2,1");
	}

}
