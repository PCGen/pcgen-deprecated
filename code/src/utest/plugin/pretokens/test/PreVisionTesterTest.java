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

import org.junit.Test;

import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.character.CharacterDataStore;
import pcgen.core.Vision;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;

public class PreVisionTesterTest extends TestCase
{

	PreVisionTester tester = new PreVisionTester();

	public Prerequisite getSimplePrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind("VISION");
		p.setKey("Normal");
		p.setOperand("30");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getSpecialPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind("VISION");
		p.setKey("Darkvision");
		p.setOperand("20");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	@Test
	public void testSimpleEqual() throws PrerequisiteException
	{
		CharacterDataStore pc = new CharacterDataStore(new SimpleRulesDataStore());
		// PC Should start without
		assertEquals(0, tester.passesCDOM(this.getSimplePrereq(), pc));
		PCGenGraph graph = pc.getActiveGraph();
		PrereqObject root = graph.getRoot();
		Vision v1 = Vision.getVision("Normal (30')");
		graph.addNode(v1);
		graph.addEdge(new PCGraphGrantsEdge(root, v1, "TestCase"));
		assertEquals(1, tester.passesCDOM(this.getSimplePrereq(), pc));
		assertEquals(0, tester.passesCDOM(this.getSpecialPrereq(), pc));
	}

	@Test
	public void testSimpleLess() throws PrerequisiteException
	{
		CharacterDataStore pc = new CharacterDataStore(new SimpleRulesDataStore());
		// PC Should start without
		assertEquals(0, tester.passesCDOM(this.getSimplePrereq(), pc));
		PCGenGraph graph = pc.getActiveGraph();
		PrereqObject root = graph.getRoot();
		Vision v1 = Vision.getVision("Normal (20')");
		graph.addNode(v1);
		graph.addEdge(new PCGraphGrantsEdge(root, v1, "TestCase"));
		// Has, but not sufficient distance
		assertEquals(0, tester.passesCDOM(this.getSimplePrereq(), pc));
		// No Darkvision
		assertEquals(0, tester.passesCDOM(this.getSpecialPrereq(), pc));
	}

	@Test
	public void testSimpleGreater() throws PrerequisiteException
	{
		CharacterDataStore pc = new CharacterDataStore(new SimpleRulesDataStore());
		// PC Should start without
		assertEquals(0, tester.passesCDOM(this.getSimplePrereq(), pc));
		PCGenGraph graph = pc.getActiveGraph();
		PrereqObject root = graph.getRoot();
		Vision v1 = Vision.getVision("Normal (40')");
		graph.addNode(v1);
		graph.addEdge(new PCGraphGrantsEdge(root, v1, "TestCase"));
		// Has
		assertEquals(1, tester.passesCDOM(this.getSimplePrereq(), pc));
		// No Darkvision
		assertEquals(0, tester.passesCDOM(this.getSpecialPrereq(), pc));
	}

	@Test
	public void testTwoVisionEqual() throws PrerequisiteException
	{
		CharacterDataStore pc = new CharacterDataStore(new SimpleRulesDataStore());
		// PC Should start without
		assertEquals(0, tester.passesCDOM(this.getSimplePrereq(), pc));
		PCGenGraph graph = pc.getActiveGraph();
		PrereqObject root = graph.getRoot();
		Vision v2 = Vision.getVision("Darkvision (30')");
		graph.addNode(v2);
		graph.addEdge(new PCGraphGrantsEdge(root, v2, "TestCase"));
		assertEquals(0, tester.passesCDOM(this.getSimplePrereq(), pc));
		Vision v1 = Vision.getVision("Normal (30')");
		graph.addNode(v1);
		graph.addEdge(new PCGraphGrantsEdge(root, v1, "TestCase"));
		assertEquals(1, tester.passesCDOM(this.getSimplePrereq(), pc));
	}

	@Test
	public void testVisionTwiceLess() throws PrerequisiteException
	{
		CharacterDataStore pc = new CharacterDataStore(new SimpleRulesDataStore());
		// PC Should start without
		assertEquals(0, tester.passesCDOM(this.getSimplePrereq(), pc));
		PCGenGraph graph = pc.getActiveGraph();
		PrereqObject root = graph.getRoot();
		Vision v1 = Vision.getVision("Normal (10')");
		graph.addNode(v1);
		graph.addEdge(new PCGraphGrantsEdge(root, v1, "TestCase"));
		assertEquals(0, tester.passesCDOM(this.getSimplePrereq(), pc));
		Vision v2 = Vision.getVision("Normal (20')");
		graph.addNode(v2);
		graph.addEdge(new PCGraphGrantsEdge(root, v2, "TestCase"));
		// Make sure it's not being additive (e.g. 10 + 20)
		assertEquals(0, tester.passesCDOM(this.getSimplePrereq(), pc));
		Vision v3 = Vision.getVision("Normal (30')");
		graph.addNode(v3);
		graph.addEdge(new PCGraphGrantsEdge(root, v3, "TestCase"));
		assertEquals(1, tester.passesCDOM(this.getSimplePrereq(), pc));
		Vision v4 = Vision.getVision("Normal (15')");
		graph.addNode(v4);
		graph.addEdge(new PCGraphGrantsEdge(root, v4, "TestCase"));
		// Make sure it's not grabbing the last one
		assertEquals(1, tester.passesCDOM(this.getSimplePrereq(), pc));
	}
}
