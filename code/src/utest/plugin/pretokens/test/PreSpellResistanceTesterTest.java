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
package plugin.pretokens.test;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.content.SpellResistance;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.character.CharacterDataStore;
import pcgen.core.Equipment;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import plugin.pretokens.testsupport.SimpleRulesDataStore;

public class PreSpellResistanceTesterTest extends TestCase
{

	PreSpellResistanceTester tester = new PreSpellResistanceTester();

	CharacterDataStore pc;

	@Override
	@Before
	public void setUp()
	{
		pc = new CharacterDataStore(new SimpleRulesDataStore());
	}

	public String getKind()
	{
		return "SR";
	}

	public PrerequisiteTest getTest()
	{
		return tester;
	}

	public Prerequisite getOnePrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setOperand("15");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	@Test
	public void testInvalidCount()
	{
		Prerequisite prereq = getOnePrereq();
		prereq.setOperand("x");
		try
		{
			getTest().passesCDOM(prereq, pc);
			fail();
		}
		catch (PrerequisiteException pe)
		{
			// OK (operand should be a number)
		}
		catch (NumberFormatException pe)
		{
			// OK (operand should be a number)
		}
	}

	public PCGraphGrantsEdge grantObject(PrereqObject obj)
	{
		return grantObject(pc.getActiveGraph().getRoot(), obj);
	}

	public PCGraphGrantsEdge grantObject(PrereqObject parent, PrereqObject child)
	{
		PCGenGraph graph = pc.getActiveGraph();
		graph.addNode(child);
		PCGraphGrantsEdge edge =
				new PCGraphGrantsEdge(parent, child, "TestCase");
		graph.addEdge(edge);
		return edge;
	}

//	@Test
//	public void testInsufficient() throws PrerequisiteException
//	{
//		Prerequisite prereq = getOnePrereq();
//		// PC Should start without
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//		SpellResistance five =
//				new SpellResistance(FormulaFactory.getFormulaFor(5));
//		grantObject(five);
//		// Insufficient
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//		SpellResistance ten =
//				new SpellResistance(FormulaFactory.getFormulaFor(10));
//		grantObject(ten);
//		// Insufficient
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//	}
//
//	@Test
//	public void testExact() throws PrerequisiteException
//	{
//		Prerequisite prereq = getOnePrereq();
//		// PC Should start without
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//		SpellResistance five =
//				new SpellResistance(FormulaFactory.getFormulaFor(5));
//		grantObject(five);
//		// Insufficient
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//		SpellResistance fifteen =
//				new SpellResistance(FormulaFactory.getFormulaFor(15));
//		grantObject(fifteen);
//		// Okay
//		assertEquals(1, getTest().passesCDOM(prereq, pc));
//		SpellResistance six =
//				new SpellResistance(FormulaFactory.getFormulaFor(6));
//		grantObject(six);
//		// Okay, testing max not last
//		assertEquals(1, getTest().passesCDOM(prereq, pc));
//	}
//
//	@Test
//	public void testSurplus() throws PrerequisiteException
//	{
//		Prerequisite prereq = getOnePrereq();
//		// PC Should start without
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//		SpellResistance twenty =
//				new SpellResistance(FormulaFactory.getFormulaFor(20));
//		grantObject(twenty);
//		assertEquals(1, getTest().passesCDOM(prereq, pc));
//	}
//
//	@Test
//	public void testInsufficientEquipment() throws PrerequisiteException
//	{
//		Prerequisite prereq = getOnePrereq();
//		// PC Should start without
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//		SpellResistance five =
//				new SpellResistance(FormulaFactory.getFormulaFor(5));
//		grantObject(five);
//		// Insufficient
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//		SpellResistance fifteen =
//				new SpellResistance(FormulaFactory.getFormulaFor(15));
//		Equipment eq = new Equipment();
//		grantObject(eq);
//		grantObject(eq, fifteen);
//		// Insufficient (on equipment)
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//	}

	// TODO Need to consider inverted? !PRE?

	// TODO Need to test with BONUSES

	// TODO Need to test SRs that use variables...

}
