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
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.SpellLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class CostTokenTest extends AbstractTokenTestCase<Spell>
{
	static CostToken token = new CostToken();
	static SpellLoader loader = new SpellLoader();

	@Override
	public Class<Spell> getCDOMClass()
	{
		return Spell.class;
	}

	@Override
	public LstObjectFileLoader<Spell> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Spell> getToken()
	{
		return token;
	}

	@Test
	public void testBadDefaultNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "-5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadDefaultNaN() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "YES"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadDefaultTailingPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "5|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadTailingPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"5|Wizard,10|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadNoDefault() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Wizard,10"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadLeadingPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadDoublePipeToStart() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"5||Sorcerer,50|Wizard,10"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadDoublePipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"5|Sorcerer,50||Wizard,10"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadDoubleComma() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"5|Sorcerer,50|Wizard,,10"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadSameClass() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"5|Wizard,50|Wizard,10"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadNegativeClassCost() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"5|Wizard,-10"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadTrailingComma() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"5|Sorcerer,50|Wizard,"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadLeadingComma() throws PersistenceLayerException
	{
		try
		{
			boolean parse =
					getToken().parse(primaryContext, primaryProf,
						"5|Sorcerer,50|,25");
			if (parse)
			{
				fail();
			}
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadTwoDefaults() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "5|50"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		DefaultMap<CDOMSimpleSingleRef<PCClass>, Integer> dm;
		assertTrue(getToken().parse(primaryContext, primaryProf, "5"));
		dm = primaryProf.get(ObjectKey.COMPONENT_COST);
		assertTrue(dm.isEmpty());
		assertEquals(Integer.valueOf(5), dm.getDefaultValue());
		assertTrue(getToken().parse(primaryContext, primaryProf, "1"));
		dm = primaryProf.get(ObjectKey.COMPONENT_COST);
		assertTrue(dm.isEmpty());
		assertEquals(Integer.valueOf(1), dm.getDefaultValue());
		assertTrue(getToken().parse(primaryContext, primaryProf, "0"));
		dm = primaryProf.get(ObjectKey.COMPONENT_COST);
		assertTrue(dm.isEmpty());
		assertEquals(Integer.valueOf(0), dm.getDefaultValue());
	}

	@Test
	public void testOutputOne() throws PersistenceLayerException
	{
		assertTrue(0 == primaryContext.getWriteMessageCount());
		DefaultMap<CDOMSimpleSingleRef<PCClass>, Integer> dm =
				new DefaultMap<CDOMSimpleSingleRef<PCClass>, Integer>();
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
		DefaultMap<CDOMSimpleSingleRef<PCClass>, Integer> dm =
				new DefaultMap<CDOMSimpleSingleRef<PCClass>, Integer>();
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
		DefaultMap<CDOMSimpleSingleRef<PCClass>, Integer> dm =
				new DefaultMap<CDOMSimpleSingleRef<PCClass>, Integer>();
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
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		runRoundRobin("5|Wizard,10");
	}

	@Test
	public void testRoundRobinTwoClassSortTest()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		primaryContext.ref.constructCDOMObject(PCClass.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Sorcerer");
		runRoundRobin("5|Sorcerer,50|Wizard,35");
	}

	@Test
	public void testRoundRobinTwoClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		primaryContext.ref.constructCDOMObject(PCClass.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Sorcerer");
		runRoundRobin("5|Sorcerer,25|Wizard,25");
	}
}
