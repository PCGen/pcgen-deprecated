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

import org.junit.Test;

import pcgen.base.util.DefaultMap;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class CostTokenTest extends AbstractTokenTestCase<CDOMSpell>
{
	static CostToken token = new CostToken();
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

	@Test
	public void testBadDefaultNegative() throws PersistenceLayerException
	{
		assertFalse(parse("-5"));
		assertNoSideEffects();
	}

	@Test
	public void testBadDefaultNaN() throws PersistenceLayerException
	{
		assertFalse(parse("YES"));
		assertNoSideEffects();
	}

	@Test
	public void testBadDefaultTailingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("5|"));
		assertNoSideEffects();
	}

	@Test
	public void testBadTailingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("5|Wizard,10|"));
		assertNoSideEffects();
	}

	@Test
	public void testBadNoDefault() throws PersistenceLayerException
	{
		assertFalse(parse("Wizard,10"));
		assertNoSideEffects();
	}

	@Test
	public void testBadEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testBadLeadingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("|5"));
		assertNoSideEffects();
	}

	@Test
	public void testBadDoublePipeToStart() throws PersistenceLayerException
	{
		assertFalse(parse("5||Sorcerer,50|Wizard,10"));
		assertNoSideEffects();
	}

	@Test
	public void testBadDoublePipe() throws PersistenceLayerException
	{
		assertFalse(parse("5|Sorcerer,50||Wizard,10"));
		assertNoSideEffects();
	}

	@Test
	public void testBadDoubleComma() throws PersistenceLayerException
	{
		assertFalse(parse("5|Sorcerer,50|Wizard,,10"));
		assertNoSideEffects();
	}

	@Test
	public void testBadSameClass() throws PersistenceLayerException
	{
		assertFalse(parse("5|Wizard,50|Wizard,10"));
		assertNoSideEffects();
	}

	@Test
	public void testBadNegativeClassCost() throws PersistenceLayerException
	{
		assertFalse(parse("5|Wizard,-10"));
		assertNoSideEffects();
	}

	@Test
	public void testBadTrailingComma() throws PersistenceLayerException
	{
		assertFalse(parse("5|Sorcerer,50|Wizard,"));
		assertNoSideEffects();
	}

	@Test
	public void testBadLeadingComma() throws PersistenceLayerException
	{
		try
		{
			boolean parse = parse("5|Sorcerer,50|,25");
			if (parse)
			{
				fail();
			}
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		assertNoSideEffects();
	}

	@Test
	public void testBadTwoDefaults() throws PersistenceLayerException
	{
		assertFalse(parse("5|50"));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		DefaultMap<CDOMSingleRef<CDOMPCClass>, Integer> dm;
		assertTrue(parse("5"));
		dm = primaryProf.get(ObjectKey.COMPONENT_COST);
		assertTrue(dm.isEmpty());
		assertEquals(Integer.valueOf(5), dm.getDefaultValue());
		assertTrue(parse("1"));
		dm = primaryProf.get(ObjectKey.COMPONENT_COST);
		assertTrue(dm.isEmpty());
		assertEquals(Integer.valueOf(1), dm.getDefaultValue());
		assertTrue(parse("0"));
		dm = primaryProf.get(ObjectKey.COMPONENT_COST);
		assertTrue(dm.isEmpty());
		assertEquals(Integer.valueOf(0), dm.getDefaultValue());
	}

	@Test
	public void testOutputOne() throws PersistenceLayerException
	{
		assertTrue(0 == primaryContext.getWriteMessageCount());
		DefaultMap<CDOMSingleRef<CDOMPCClass>, Integer> dm = new DefaultMap<CDOMSingleRef<CDOMPCClass>, Integer>();
		dm.setDefaultValue(Integer.valueOf(1));
		primaryProf.put(ObjectKey.COMPONENT_COST, dm);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals(1, unparsed.length);
		assertEquals("1", unparsed[0]);
	}

	@Test
	public void testOutputZero() throws PersistenceLayerException
	{
		assertTrue(0 == primaryContext.getWriteMessageCount());
		DefaultMap<CDOMSingleRef<CDOMPCClass>, Integer> dm = new DefaultMap<CDOMSingleRef<CDOMPCClass>, Integer>();
		dm.setDefaultValue(Integer.valueOf(0));
		primaryProf.put(ObjectKey.COMPONENT_COST, dm);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals(1, unparsed.length);
		assertEquals("0", unparsed[0]);
	}

	@Test
	public void testBadOutputMinusTwo() throws PersistenceLayerException
	{
		assertTrue(0 == primaryContext.getWriteMessageCount());
		DefaultMap<CDOMSingleRef<CDOMPCClass>, Integer> dm = new DefaultMap<CDOMSingleRef<CDOMPCClass>, Integer>();
		dm.setDefaultValue(Integer.valueOf(-2));
		primaryProf.put(ObjectKey.COMPONENT_COST, dm);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		assertNull(unparsed);
		assertTrue(0 != primaryContext.getWriteMessageCount());
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		runRoundRobin("1");
	}

	@Test
	public void testRoundRobinZero() throws PersistenceLayerException
	{
		runRoundRobin("0");
	}

	@Test
	public void testRoundRobinFive() throws PersistenceLayerException
	{
		runRoundRobin("5");
	}

	@Test
	public void testRoundRobinOneClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		runRoundRobin("5|Wizard,10");
	}

	@Test
	public void testRoundRobinTwoClassSortTest()
			throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Sorcerer");
		runRoundRobin("5|Sorcerer,50|Wizard,35");
	}

	@Test
	public void testRoundRobinTwoClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Sorcerer");
		runRoundRobin("5|Sorcerer,25|Wizard,25");
	}
}
