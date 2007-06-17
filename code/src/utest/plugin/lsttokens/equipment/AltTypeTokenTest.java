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
package plugin.lsttokens.equipment;

import org.junit.Test;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.testsupport.AbstractTypeSafeListTestCase;

public class AltTypeTokenTest extends AbstractTypeSafeListTestCase<Equipment>
{

	static AlttypeToken token = new AlttypeToken();
	static EquipmentLoader loader = new EquipmentLoader();

	@Override
	public Class<Equipment> getCDOMClass()
	{
		return Equipment.class;
	}

	@Override
	public LstObjectFileLoader<Equipment> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Equipment> getToken()
	{
		return token;
	}

	@Override
	public Object getConstant(String string)
	{
		return Type.getConstant(string);
	}

	@Override
	public char getJoinCharacter()
	{
		return '.';
	}

	@Override
	public ListKey<?> getListKey()
	{
		return ListKey.ALT_TYPE;
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return true;
	}

	@Test
	public void testReplacementRemove() throws PersistenceLayerException
	{
		String[] unparsed;
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"REMOVE.TestWP1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertNull("Expected item to be equal", unparsed);

		assertTrue(getToken().parse(primaryContext, primaryProf, "TestWP1"));
		assertTrue(getToken().parse(primaryContext, primaryProf, "ADD.TestWP2"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "TestWP1"
			+ getJoinCharacter() + "TestWP2", unparsed[0]);
		if (isClearLegal())
		{
			assertTrue(getToken().parse(primaryContext, primaryProf, ".CLEAR"));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			assertNull("Expected item to be null", unparsed);
		}
	}

	@Test
	public void testReplacementRemoveTwo() throws PersistenceLayerException
	{
		String[] unparsed;
		assertTrue(getToken().parse(primaryContext, primaryProf, "TestWP1"));
		assertTrue(getToken().parse(primaryContext, primaryProf, "TestWP2"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "TestWP1"
			+ getJoinCharacter() + "TestWP2", unparsed[0]);
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"REMOVE.TestWP1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "TestWP2", unparsed[0]);
	}

	@Test
	public void testInputInvalidRemoveNoTrailing()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP1.REMOVE"));
	}

	@Test
	public void testInputInvalidAddNoTrailing()
		throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "TestWP1.ADD"));
	}

	@Test
	public void testInputInvalidAddRemove() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP1.ADD.REMOVE.TestWP2"));
	}

	@Test
	public void testInputInvalidRemoveAdd() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP1.REMOVE.ADD.TestWP2"));
	}

}
