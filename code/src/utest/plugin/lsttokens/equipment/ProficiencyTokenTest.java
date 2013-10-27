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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMArmorProf;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMShieldProf;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class ProficiencyTokenTest extends AbstractTokenTestCase<CDOMEquipment>
{
	static ProficiencyToken token = new ProficiencyToken();
	static CDOMTokenLoader<CDOMEquipment> loader = new CDOMTokenLoader<CDOMEquipment>(
			CDOMEquipment.class);

	@Override
	public Class<CDOMEquipment> getCDOMClass()
	{
		return CDOMEquipment.class;
	}

	@Override
	public CDOMLoader<CDOMEquipment> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMEquipment> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
		assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
		assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertFalse(parse("String"));
		assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
		assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
		assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputJoinedComma() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("TestWP1,TestWP2"));
		assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
		assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
		assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputJoinedPipe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("TestWP1|TestWP2"));
		assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
		assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
		assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("TestWP1.TestWP2"));
		assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
		assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
		assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyWeapon() throws PersistenceLayerException
	{
		assertFalse(parse("WEAPON|"));
		assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
		assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
		assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputWeaponString() throws PersistenceLayerException
	{
		assertTrue(parse("WEAPON|String"));
		assertFalse(primaryContext.ref.validate());
	}

	// FIXME These are invalid due to RC being overly protective at the moment
	// @Test
	// public void testInvalidInputWeaponType() throws PersistenceLayerException
	// {
	// assertTrue(parse("WEAPON|TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }

	@Test
	public void testInvalidInputWeaponJoinedComma()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		boolean ret = parse("WEAPON|TestWP1,TestWP2");
		if (ret)
		{
			assertFalse(primaryContext.ref.validate());
		}
		else
		{
			assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
			assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
			assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputWeaponJoinedPipe()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		boolean ret = parse("WEAPON|TestWP1|TestWP2");
		if (ret)
		{
			assertFalse(primaryContext.ref.validate());
		}
		else
		{
			assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
			assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
			assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputWeaponJoinedDot()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		boolean ret = parse("WEAPON|TestWP1.TestWP2");
		if (ret)
		{
			assertFalse(primaryContext.ref.validate());
		}
		else
		{
			assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
			assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
			assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
			assertNoSideEffects();
		}
	}

	// FIXME These are invalid due to RC being overly protective at the moment
	// @Test
	// public void testInvalidInputAll()
	// {
	// assertTrue(parse( "ALL"));
	// assertFalse(primaryContext.ref.validate());
	// }
	//
	// @Test
	// public void testInvalidInputAny()
	// {
	// assertTrue(parse( "ANY"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// @Test
	// public void testInvalidInputCheckType()
	// {
	// if (!isTypeLegal())
	// {
	// assertTrue(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// }
	//

	@Test
	public void testReplacementInputsWeapon() throws PersistenceLayerException
	{
		String[] unparsed;
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		if (isClearLegal())
		{
			assertTrue(parse(".CLEAR"));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			assertNull("Expected item to be equal", unparsed);
		}
		assertTrue(parse("WEAPON|TestWP1"));
		assertTrue(parse("WEAPON|TestWP2"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "WEAPON|TestWP2", unparsed[0]);
		if (isClearLegal())
		{
			assertTrue(parse(".CLEAR"));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			assertNull("Expected item to be equal", unparsed);
		}
	}

	@Test
	public void testInvalidInputEmptyArmor() throws PersistenceLayerException
	{
		assertFalse(parse("ARMOR|"));
		assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
		assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
		assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputArmorString() throws PersistenceLayerException
	{
		assertTrue(parse("ARMOR|String"));
		assertFalse(primaryContext.ref.validate());
	}

	// FIXME These are invalid due to RC being overly protective at the moment
	// @Test
	// public void testInvalidInputArmorType() throws PersistenceLayerException
	// {
	// assertTrue(parse("ARMOR|TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }

	@Test
	public void testInvalidInputArmorJoinedComma()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		boolean ret = parse("ARMOR|TestWP1,TestWP2");
		if (ret)
		{
			assertFalse(primaryContext.ref.validate());
		}
		else
		{
			assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
			assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
			assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputArmorJoinedPipe()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		boolean ret = parse("ARMOR|TestWP1|TestWP2");
		if (ret)
		{
			assertFalse(primaryContext.ref.validate());
		}
		else
		{
			assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
			assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
			assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputArmorJoinedDot()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		boolean ret = parse("ARMOR|TestWP1.TestWP2");
		if (ret)
		{
			assertFalse(primaryContext.ref.validate());
		}
		else
		{
			assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
			assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
			assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
			assertNoSideEffects();
		}
	}

	// FIXME These are invalid due to RC being overly protective at the moment
	// @Test
	// public void testInvalidInputAll()
	// {
	// assertTrue(parse( "ALL"));
	// assertFalse(primaryContext.ref.validate());
	// }
	//
	// @Test
	// public void testInvalidInputAny()
	// {
	// assertTrue(parse( "ANY"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// @Test
	// public void testInvalidInputCheckType()
	// {
	// if (!isTypeLegal())
	// {
	// assertTrue(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// }
	//

	@Test
	public void testReplacementInputsArmor() throws PersistenceLayerException
	{
		String[] unparsed;
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		if (isClearLegal())
		{
			assertTrue(parse(".CLEAR"));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			assertNull("Expected item to be equal", unparsed);
		}
		assertTrue(parse("ARMOR|TestWP1"));
		assertTrue(parse("ARMOR|TestWP2"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "ARMOR|TestWP2", unparsed[0]);
		if (isClearLegal())
		{
			assertTrue(parse(".CLEAR"));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			assertNull("Expected item to be equal", unparsed);
		}
	}

	@Test
	public void testInvalidInputEmptyShield() throws PersistenceLayerException
	{
		assertFalse(parse("SHIELD|"));
		assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
		assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
		assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputShieldString() throws PersistenceLayerException
	{
		assertTrue(parse("SHIELD|String"));
		assertFalse(primaryContext.ref.validate());
	}

	// FIXME These are invalid due to RC being overly protective at the moment
	// @Test
	// public void testInvalidInputShieldType() throws PersistenceLayerException
	// {
	// assertTrue(parse("SHIELD|TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }

	@Test
	public void testInvalidInputShieldJoinedComma()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		boolean ret = parse("SHIELD|TestWP1,TestWP2");
		if (ret)
		{
			assertFalse(primaryContext.ref.validate());
		}
		else
		{
			assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
			assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
			assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputShieldJoinedPipe()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		boolean ret = parse("SHIELD|TestWP1|TestWP2");
		if (ret)
		{
			assertFalse(primaryContext.ref.validate());
		}
		else
		{
			assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
			assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
			assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputShieldJoinedDot()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		boolean ret = parse("SHIELD|TestWP1.TestWP2");
		if (ret)
		{
			assertFalse(primaryContext.ref.validate());
		}
		else
		{
			assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
			assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
			assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
			assertNoSideEffects();
		}
	}

	// FIXME These are invalid due to RC being overly protective at the moment
	// @Test
	// public void testInvalidInputAll()
	// {
	// assertTrue(parse( "ALL"));
	// assertFalse(primaryContext.ref.validate());
	// }
	//
	// @Test
	// public void testInvalidInputAny()
	// {
	// assertTrue(parse( "ANY"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// @Test
	// public void testInvalidInputCheckType()
	// {
	// if (!isTypeLegal())
	// {
	// assertTrue(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// }
	//

	@Test
	public void testReplacementInputsShield() throws PersistenceLayerException
	{
		String[] unparsed;
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		if (isClearLegal())
		{
			assertTrue(parse(".CLEAR"));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			assertNull("Expected item to be equal", unparsed);
		}
		assertTrue(parse("SHIELD|TestWP1"));
		assertTrue(parse("SHIELD|TestWP2"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "SHIELD|TestWP2", unparsed[0]);
		if (isClearLegal())
		{
			assertTrue(parse(".CLEAR"));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			assertNull("Expected item to be equal", unparsed);
		}
	}

	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(CDOMWeaponProf.class, one);
		loadContext.ref.constructCDOMObject(CDOMShieldProf.class, one);
		loadContext.ref.constructCDOMObject(CDOMArmorProf.class, one);
	}

	private boolean isClearLegal()
	{
		return false;
	}

}
