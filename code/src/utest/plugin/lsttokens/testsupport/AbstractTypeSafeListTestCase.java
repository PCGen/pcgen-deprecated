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

import java.util.List;

import org.junit.Test;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public abstract class AbstractTypeSafeListTestCase<T extends PObject> extends
		AbstractTokenTestCase<T>
{

	public abstract Object getConstant(String string);

	public abstract char getJoinCharacter();

	public abstract ListKey<?> getListKey();

	@Test
	public void testValidInputSimple() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken().parse(primaryContext, primaryProf, "Rheinhessen"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Rheinhessen")));
	}

	@Test
	public void testValidInputNonEnglish() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Niederösterreich"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Niederösterreich")));
	}

	@Test
	public void testValidInputSpace() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, "Finger Lakes"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Finger Lakes")));
	}

	@Test
	public void testValidInputHyphen() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Languedoc-Roussillon"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
	}

	@Test
	public void testValidInputY() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, "Yarra Valley"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Yarra Valley")));
	}

	@Test
	public void testValidInputList() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Niederösterreich" + getJoinCharacter() + "Finger Lakes"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(2, coll.size());
		assertTrue(coll.contains(getConstant("Niederösterreich")));
		assertTrue(coll.contains(getConstant("Finger Lakes")));
	}

	@Test
	public void testValidInputMultList() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Niederösterreich" + getJoinCharacter() + "Finger Lakes"));
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Languedoc-Roussillon" + getJoinCharacter() + "Rheinhessen"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(4, coll.size());
		assertTrue(coll.contains(getConstant("Niederösterreich")));
		assertTrue(coll.contains(getConstant("Finger Lakes")));
		assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
		assertTrue(coll.contains(getConstant("Rheinhessen")));
	}

	// FIXME Someday, when PCGen doesn't write out crappy stuff into custom
	// items
	// @Test
	// public void testInvalidListEmpty() throws PersistenceLayerException
	// {
	// primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
	// assertFalse(getToken().parse(primaryContext, primaryProf, ""));
	// }

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP1" + getJoinCharacter()));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getJoinCharacter() + "TestWP1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP1");
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP2" + getJoinCharacter() + getJoinCharacter() + "TestWP1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "Rheinhessen");
		secondaryContext.ref.constructCDOMObject(getCDOMClass(), "Rheinhessen");
		runRoundRobin("Rheinhessen");
	}

	@Test
	public void testRoundRobinWithSpace() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "Finger Lakes");
		secondaryContext.ref
			.constructCDOMObject(getCDOMClass(), "Finger Lakes");
		runRoundRobin("Finger Lakes");
	}

	@Test
	public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Niederösterreich");
		secondaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Niederösterreich");
		runRoundRobin("Niederösterreich");
	}

	@Test
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Languedoc-Roussillon");
		secondaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Languedoc-Roussillon");
		runRoundRobin("Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinY() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "Yarra Valley");
		secondaryContext.ref
			.constructCDOMObject(getCDOMClass(), "Yarra Valley");
		runRoundRobin("Yarra Valley");
	}

	@Test
	public void testRoundRobinThree() throws PersistenceLayerException
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
			+ getJoinCharacter() + "Languedoc-Roussillon");
	}

	public static String[] getConstants()
	{
		return new String[]{"Niederösterreich", "Finger Lakes",
			"Languedoc-Roussillon", "Rheinhessen", "Yarra Valley"};
	}

}
