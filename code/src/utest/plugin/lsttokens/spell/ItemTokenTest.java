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
package plugin.lsttokens.spell;

import java.util.List;

import org.junit.Test;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTypeSafeListTestCase;

public class ItemTokenTest extends AbstractTypeSafeListTestCase<CDOMSpell>
{

	static ItemToken token = new ItemToken();
	static CDOMTokenLoader<CDOMSpell> loader = new CDOMTokenLoader<CDOMSpell>(
			CDOMSpell.class);

	@Override
	public Class<CDOMSpell> getCDOMClass()
	{
		return CDOMSpell.class;
	}

	@Override
	public CDOMLoader<CDOMSpell> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMSpell> getToken()
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
		return ',';
	}

	@Override
	public ListKey<?> getListKey()
	{
		return ListKey.ITEM;
	}

	public ListKey<?> getNegativeListKey()
	{
		return ListKey.PROHIBITED_ITEM;
	}

	@Test
	public void dummyTest()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return false;
	}

	@Test
	public void testValidInputNegativeSimple() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(parse("[Rheinhessen]"));
		coll = primaryProf.getListFor(getNegativeListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Rheinhessen")));
	}

	@Test
	public void testValidInputNegativeNonEnglish()
		throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(parse("[Nieder�sterreich]"));
		coll = primaryProf.getListFor(getNegativeListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Nieder�sterreich")));
	}

	@Test
	public void testValidInputNegativeSpace() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(parse("[Finger Lakes]"));
		coll = primaryProf.getListFor(getNegativeListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Finger Lakes")));
	}

	@Test
	public void testValidInputNegativeHyphen() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(parse("[Languedoc-Roussillon]"));
		coll = primaryProf.getListFor(getNegativeListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
	}

	@Test
	public void testValidInputNegativeList() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(parse("[Nieder�sterreich]" + getJoinCharacter()
			+ "[Finger Lakes]"));
		coll = primaryProf.getListFor(getNegativeListKey());
		assertEquals(2, coll.size());
		assertTrue(coll.contains(getConstant("Nieder�sterreich")));
		assertTrue(coll.contains(getConstant("Finger Lakes")));
	}

	@Test
	public void testValidInputMultNegativeList()
		throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(parse("[Nieder�sterreich]" + getJoinCharacter()
			+ "[Finger Lakes]"));
		assertTrue(parse("[Languedoc-Roussillon]" + getJoinCharacter()
			+ "[Rheinhessen]"));
		coll = primaryProf.getListFor(getNegativeListKey());
		assertEquals(4, coll.size());
		assertTrue(coll.contains(getConstant("Nieder�sterreich")));
		assertTrue(coll.contains(getConstant("Finger Lakes")));
		assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
		assertTrue(coll.contains(getConstant("Rheinhessen")));
	}

	@Test
	public void testInvalidNegativeEmpty() throws PersistenceLayerException
	{
		assertFalse(parse("[]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativePrefix() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP2");
		assertFalse(parse("TestWP2[TestWP1]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeSuffix() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP2");
		assertFalse(parse("[TestWP1]TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeStart() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
		assertFalse(parse("TestWP1]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeEnd() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
		assertFalse(parse("[TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeListEnd() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
		assertFalse(parse("[TestWP1]" + getJoinCharacter()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeListStart() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
		assertFalse(parse(getJoinCharacter() + "[TestWP1]"));
		assertNoSideEffects();
	}

	// FUTURE This is a subtle set of errors, catch in the future
	// @Test
	// public void testInvalidAddRemove() throws PersistenceLayerException
	// {
	// primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
	// assertFalse(parse("TestWP1" + getJoinCharacter() + "[TestWP1]"));
	// assertNoSideEffects();
	// }
	//
	// @Test
	// public void testInvalidRemoveAdd() throws PersistenceLayerException
	// {
	// primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
	// assertFalse(parse("[TestWP1]" + getJoinCharacter() + "TestWP1"));
	// assertNoSideEffects();
	// }

	@Test
	public void testInvalidNegativeListDoubleJoin()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP2");
		assertFalse(parse("[TestWP2]" + getJoinCharacter() + getJoinCharacter()
			+ "[TestWP1]"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinNegativeBase() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "Rheinhessen");
		secondaryContext.ref.constructCDOMObject(getCDOMClass(), "Rheinhessen");
		runRoundRobin("[Rheinhessen]");
	}

	@Test
	public void testRoundRobinNegativeWithSpace()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "Finger Lakes");
		secondaryContext.ref
			.constructCDOMObject(getCDOMClass(), "Finger Lakes");
		runRoundRobin("[Finger Lakes]");
	}

	@Test
	public void testRoundRobinNegativeNonEnglishAndN()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Nieder�sterreich");
		secondaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Nieder�sterreich");
		runRoundRobin("[Nieder�sterreich]");
	}

	@Test
	public void testRoundRobinNegativeThree() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "Rheinhessen");
		secondaryContext.ref.constructCDOMObject(getCDOMClass(), "Rheinhessen");
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "Yarra Valley");
		secondaryContext.ref
			.constructCDOMObject(getCDOMClass(), "Yarra Valley");
		primaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Languedoc-Roussillon");
		secondaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Languedoc-Roussillon");
		runRoundRobin("[Rheinhessen]" + getJoinCharacter() + "[Yarra Valley]"
			+ getJoinCharacter() + "[Languedoc-Roussillon]");
	}

	@Test
	public void testRoundRobinMixed() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "Rheinhessen");
		secondaryContext.ref.constructCDOMObject(getCDOMClass(), "Rheinhessen");
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "Yarra Valley");
		secondaryContext.ref
			.constructCDOMObject(getCDOMClass(), "Yarra Valley");
		primaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Languedoc-Roussillon");
		secondaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Languedoc-Roussillon");
		runRoundRobin("Rheinhessen" + getJoinCharacter() + "Yarra Valley"
			+ getJoinCharacter() + "[Languedoc-Roussillon]");
	}
}
