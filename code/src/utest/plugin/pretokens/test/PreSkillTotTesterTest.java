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

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.inst.CDOMLanguage;
import pcgen.cdom.inst.CDOMSkill;
import pcgen.core.Skill;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreSkillTotTesterTest extends
		AbstractCDOMPreTestTestCase<CDOMSkill>
{

	PreSkillTotTester tester = new PreSkillTotTester();

	@Override
	public Class<CDOMSkill> getCDOMClass()
	{
		return CDOMSkill.class;
	}

	@Override
	public Class<? extends CDOMObject> getFalseClass()
	{
		return CDOMLanguage.class;
	}

	public String getKind()
	{
		return "SKILL.TOT";
	}

	public PrerequisiteTest getTest()
	{
		return tester;
	}

	private boolean isTypeLegal()
	{
		return true;
	}

	public Prerequisite getSimplePrereq(String stat)
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey(stat);
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	@Test
	public void testAssumptions() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq("STR");
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMSkill strStat = getObject("STR");
		grantObject(strStat);
		// no weight on edge implies weight == 1??
		// TODO Should this be one or zero??
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testSimple() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq("STR");
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMSkill strStat = getObject("STR");
		PCGraphGrantsEdge strEdge = grantObject(strStat);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		CDOMSkill intStat = getObject("INT");
		PCGraphGrantsEdge intEdge = grantObject(intStat);
		intEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(15));
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(13));
		assertEquals(13, getTest().passesCDOM(prereq, pc));
		strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(15));
		assertEquals(15, getTest().passesCDOM(prereq, pc));
		strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(16));
		assertEquals(16, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testMultiple() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq("STR");
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMSkill strStat = getObject("STR");
		PCGraphGrantsEdge strEdge = grantObject(strStat);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(13));
		// Fail, not enough weight on edge
		assertEquals(13, getTest().passesCDOM(prereq, pc));
		CDOMObject po = getFalseObject("FOO");
		grantObject(po);
		PCGraphGrantsEdge edge2 = grantObject(po, strStat);
		assertEquals(14, getTest().passesCDOM(prereq, pc));
		edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(1));
		// Fail, not enough weight on edges
		assertEquals(14, getTest().passesCDOM(prereq, pc));
		edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(2));
		assertEquals(15, getTest().passesCDOM(prereq, pc));
		edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(3));
		assertEquals(16, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testComplexMultipleImpliedNo() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq("STR");
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMSkill strStat = getObject("STR");
		PCGraphGrantsEdge strEdge = grantObject(strStat);
		// Fail, no weight on edge
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(13));
		assertEquals(13, getTest().passesCDOM(prereq, pc));
		CDOMObject foo = getFalseObject("FOO");
		CDOMObject bar = getFalseObject("BAR");
		grantObject(bar);
		grantObject(foo);
		grantObject(bar, foo);
		PCGraphGrantsEdge edge2 = grantObject(foo, strStat);
		assertEquals(14, getTest().passesCDOM(prereq, pc));
		edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(1));
		assertEquals(14, getTest().passesCDOM(prereq, pc));
		edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(2));
		assertEquals(15, getTest().passesCDOM(prereq, pc));
		edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(3));
		assertEquals(16, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testComplexMultipleNo() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq("STR");
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMSkill strStat = getObject("STR");
		PCGraphGrantsEdge strEdge = grantObject(strStat);
		// Fail, no weight on edge
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(13));
		// Fail, not enough weight on edge
		assertEquals(13, getTest().passesCDOM(prereq, pc));
		CDOMObject foo = getFalseObject("FOO");
		CDOMObject bar = getFalseObject("BAR");
		foo.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		grantObject(bar);
		grantObject(foo);
		grantObject(bar, foo);
		PCGraphGrantsEdge edge2 = grantObject(foo, strStat);
		// Fail, not enough weight on edge (second edge no weight)
		assertEquals(14, getTest().passesCDOM(prereq, pc));
		edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(1));
		// Fail, not enough weight on edges
		assertEquals(14, getTest().passesCDOM(prereq, pc));
		edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(2));
		assertEquals(15, getTest().passesCDOM(prereq, pc));
		edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(3));
		assertEquals(16, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testComplexMultipleYes() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq("STR");
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMSkill strStat = getObject("STR");
		PCGraphGrantsEdge strEdge = grantObject(strStat);
		// Fail, no weight on edge
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(11));
		// Fail, not enough weight on edge
		assertEquals(11, getTest().passesCDOM(prereq, pc));
		CDOMObject foo = getFalseObject("FOO");
		CDOMObject bar = getFalseObject("BAR");
		foo.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		grantObject(bar);
		grantObject(foo);
		grantObject(bar, foo);
		PCGraphGrantsEdge edge2 = grantObject(foo, strStat);
		// Fail, not enough weight on edge (second edge no weight)
		assertEquals(13, getTest().passesCDOM(prereq, pc));
		edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(1));
		// Pass, not enough weight on edges
		assertEquals(13, getTest().passesCDOM(prereq, pc));
		edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(2));
		assertEquals(15, getTest().passesCDOM(prereq, pc));
		edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(3));
		assertEquals(17, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testTypeSimple() throws PrerequisiteException
	{
		if (isTypeLegal())
		{
			Prerequisite prereq = getSimplePrereq("TYPE=Martial");
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			CDOMSkill strStat = getObject("STR");
			PCGraphGrantsEdge strEdge = grantObject(strStat);
			// Fail, no weight on edge, wrong type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			CDOMSkill intStat = getObject("INT");
			PCGraphGrantsEdge intEdge = grantObject(intStat);
			intEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(15));
			// Fail, no weight on str edge (still), no type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(13));
			// Fail, not enough weight on edge, no type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(15));
			// Fail, no type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(16));
			// Fail, no type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(13));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
			// Fail, not enough weight on edge, wrong type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
			// Fail, not enough weight on edge
			assertEquals(13, getTest().passesCDOM(prereq, pc));
			strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(15));
			// Pass, exact value
			assertEquals(15, getTest().passesCDOM(prereq, pc));
			strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(16));
			// Pass, more than enough
			assertEquals(16, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Weapon"));
			// Pass, more than enough
			assertEquals(16, getTest().passesCDOM(prereq, pc));
		}
	}

	@Test
	public void testTypeMultiple() throws PrerequisiteException
	{
		if (isTypeLegal())
		{
			Prerequisite prereq = getSimplePrereq("TYPE=Martial");
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			CDOMSkill strStat = getObject("STR");
			PCGraphGrantsEdge strEdge = grantObject(strStat);
			// Fail, no weight on edge, no type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(13));
			// Fail, not enough weight on edge, no type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			CDOMObject po = getFalseObject("FOO");
			grantObject(po);
			PCGraphGrantsEdge edge2 = grantObject(po, strStat);
			// Fail, not enough weight on edge (second edge no weight), no type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(1));
			// Fail, not enough weight on edges, no type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(2));
			// Fail, no type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
			// Fail, wrong type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(1));
			// Fail, not enough weight, wrong type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
			// Fail, not enough weight on edge
			assertEquals(14, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(2));
			// Pass, exact value
			assertEquals(15, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(3));
			// Pass, more than enough
			assertEquals(16, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Weapon"));
			// Pass, more than enough
			assertEquals(16, getTest().passesCDOM(prereq, pc));
		}
	}

	@Test
	public void testTypeComplexMultipleImpliedNo() throws PrerequisiteException
	{
		if (isTypeLegal())
		{
			Prerequisite prereq = getSimplePrereq("TYPE=Martial");
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			CDOMSkill strStat = getObject("STR");
			PCGraphGrantsEdge strEdge = grantObject(strStat);
			// Fail, no weight on edge
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(13));
			// Fail, not enough weight on edge
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			CDOMObject foo = getFalseObject("FOO");
			CDOMObject bar = getFalseObject("BAR");
			grantObject(bar);
			grantObject(foo);
			grantObject(bar, foo);
			PCGraphGrantsEdge edge2 = grantObject(foo, strStat);
			// Fail, not enough weight on edge (second edge no weight)
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(1));
			// Fail, not enough weight on edges
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(2));
			// Fail, no type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
			// Fail, wrong type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(1));
			// Fail, not enough weight, wrong type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
			// Fail, not enough weight on edge
			assertEquals(14, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(2));
			// Pass, exact value
			assertEquals(15, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(3));
			// Pass, more than enough
			assertEquals(16, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Weapon"));
			// Pass, more than enough
			assertEquals(16, getTest().passesCDOM(prereq, pc));
		}
	}

	@Test
	public void testTypeComplexMultipleNo() throws PrerequisiteException
	{
		if (isTypeLegal())
		{
			Prerequisite prereq = getSimplePrereq("TYPE=Martial");
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			CDOMSkill strStat = getObject("STR");
			PCGraphGrantsEdge strEdge = grantObject(strStat);
			// Fail, no weight on edge
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(13));
			// Fail, not enough weight on edge
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			CDOMObject foo = getFalseObject("FOO");
			CDOMObject bar = getFalseObject("BAR");
			foo.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
			grantObject(bar);
			grantObject(foo);
			grantObject(bar, foo);
			PCGraphGrantsEdge edge2 = grantObject(foo, strStat);
			// Fail, not enough weight on edge (second edge no weight)
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(1));
			// Fail, not enough weight on edges
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(2));
			// Fail, no type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
			// Fail, wrong type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(1));
			// Fail, not enough weight, wrong type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
			// Fail, not enough weight on edge
			assertEquals(14, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(2));
			// Pass, exact value
			assertEquals(15, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(3));
			// Pass, more than enough
			assertEquals(16, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Weapon"));
			// Pass, more than enough
			assertEquals(16, getTest().passesCDOM(prereq, pc));
		}
	}

	@Test
	public void testTypeComplexMultipleYes() throws PrerequisiteException
	{
		if (isTypeLegal())
		{
			Prerequisite prereq = getSimplePrereq("TYPE=Martial");
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			CDOMSkill strStat = getObject("STR");
			PCGraphGrantsEdge strEdge = grantObject(strStat);
			// Fail, no weight on edge
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(11));
			// Fail, not enough weight on edge
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			CDOMObject foo = getFalseObject("FOO");
			CDOMObject bar = getFalseObject("BAR");
			foo.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
			grantObject(bar);
			grantObject(foo);
			grantObject(bar, foo);
			PCGraphGrantsEdge edge2 = grantObject(foo, strStat);
			// Fail, not enough weight on edge (second edge no weight)
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(1));
			// Pass, not enough weight on edges
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(2));
			// Fail, no type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
			// Fail, wrong type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(1));
			// Fail, not enough weight, wrong type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
			// Fail, not enough weight on edge
			assertEquals(13, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(2));
			// Pass, exact value
			assertEquals(15, getTest().passesCDOM(prereq, pc));
			edge2.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(3));
			// Pass, more than enough
			assertEquals(17, getTest().passesCDOM(prereq, pc));
			strStat.addToListFor(ListKey.TYPE, Type.getConstant("Weapon"));
			// Pass, more than enough
			assertEquals(17, getTest().passesCDOM(prereq, pc));
		}
	}

	// TODO Complex Types (more than one type)
}
