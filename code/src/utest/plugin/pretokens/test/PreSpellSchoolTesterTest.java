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

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SpellSchool;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.inst.CDOMLanguage;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreSpellSchoolTesterTest extends
		AbstractCDOMPreTestTestCase<CDOMSpell>
{

	PreSpellSchoolTester tester = new PreSpellSchoolTester();

	private SpellSchool mindAffecting;
	private SpellSchool mind;
	private SpellSchool fear;

	@Before
	@Override
	public void setUp()
	{
		super.setUp();
		mindAffecting = SpellSchool.getConstant("Mind-Affecting");
		mind = SpellSchool.getConstant("Mind");
		fear = SpellSchool.getConstant("Fear");
	}

	@Override
	public Class<CDOMSpell> getCDOMClass()
	{
		return CDOMSpell.class;
	}

	@Override
	public Class<? extends CDOMObject> getFalseClass()
	{
		return CDOMLanguage.class;
	}

	public String getKind()
	{
		return "SPELL.SCHOOL";
	}

	public PrerequisiteTest getTest()
	{
		return tester;
	}

	public Prerequisite getSimplePrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Fear");
		p.setOperand("1");
		p.setSubKey("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getLevelTwoPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Fear");
		p.setOperand("1");
		p.setSubKey("2");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getStartPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Mind-Affecting");
		p.setOperand("1");
		p.setSubKey("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	@Test
	public void testSimple() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject spell = getObject("Wild Mage");
		PCGraphGrantsEdge edge = grantObject(spell);
		edge.setAssociation(AssociationKey.SPELL_LEVEL, Integer.valueOf(1));
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		spell.addToListFor(ListKey.SPELL_SCHOOL, mindAffecting);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		spell.addToListFor(ListKey.SPELL_SCHOOL, fear);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		spell.addToListFor(ListKey.SPELL_SCHOOL, mind);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testLevelTwo() throws PrerequisiteException
	{
		Prerequisite prereq = getLevelTwoPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject spell = getObject("Wild Mage");
		PCGraphGrantsEdge edge = grantObject(spell);
		edge.setAssociation(AssociationKey.SPELL_LEVEL, Integer.valueOf(1));
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		spell.addToListFor(ListKey.SPELL_SCHOOL, mindAffecting);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		spell.addToListFor(ListKey.SPELL_SCHOOL, fear);
		// Insufficient Level
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		spell.addToListFor(ListKey.SPELL_SCHOOL, mind);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		edge.setAssociation(AssociationKey.SPELL_LEVEL, Integer.valueOf(2));
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		edge.setAssociation(AssociationKey.SPELL_LEVEL, Integer.valueOf(3));
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseObject() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject lang = grantFalseObject("Winged Mage");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		lang.addToListFor(ListKey.SPELL_SCHOOL, mindAffecting);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		lang.addToListFor(ListKey.SPELL_SCHOOL, fear);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		lang.addToListFor(ListKey.SPELL_SCHOOL, mind);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseStart() throws PrerequisiteException
	{
		Prerequisite prereq = getStartPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject spell = getObject("Wild Mage");
		PCGraphGrantsEdge edge = grantObject(spell);
		edge.setAssociation(AssociationKey.SPELL_LEVEL, Integer.valueOf(3));
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		spell.addToListFor(ListKey.SPELL_SCHOOL, fear);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		spell.addToListFor(ListKey.SPELL_SCHOOL, mind);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		spell.addToListFor(ListKey.SPELL_SCHOOL, mindAffecting);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}
}
