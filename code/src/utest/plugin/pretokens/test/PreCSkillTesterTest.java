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

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.CDOMLanguage;
import pcgen.cdom.inst.CDOMSkill;
import pcgen.cdom.inst.SimpleAssociatedObject;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.list.LanguageList;
import pcgen.character.CharacterDataStore;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import plugin.pretokens.testsupport.SimpleRulesDataStore;

public class PreCSkillTesterTest extends TestCase
{

	PreCSkillTester tester = new PreCSkillTester();
	AssociatedPrereqObject eAPO = new SimpleAssociatedObject();
	AssociatedPrereqObject clAPO = new SimpleAssociatedObject();
	AssociatedPrereqObject crAPO = new SimpleAssociatedObject();

	CharacterDataStore pc;
	SimpleRulesDataStore rules;

	@Override
	@Before
	public void setUp()
	{
		rules = new SimpleRulesDataStore();
		pc = new CharacterDataStore(rules);
		rules.create(ClassSkillList.class, "*Allowed");
		rules.create(LanguageList.class, "*Allowed");
		make("Wild Mage");
		make("Winged Mage");
		make("Crossbow");
		make("Katana");
		make("Longsword");
		make("Crossbow (Light)");
		make("Crossbow (Heavy)");
		makeFalse("Katana");
		makeFalse("Longsword");
		makeFalse("Winged Mage");
		clAPO.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);
		crAPO.setAssociation(AssociationKey.SKILL_COST, SkillCost.CROSS_CLASS);
	}

	private void make(String string)
	{
		rules.create(getCDOMClass(), string);
	}

	private void makeFalse(String string)
	{
		rules.create(getFalseClass(), string);
	}

	public Class<CDOMSkill> getCDOMClass()
	{
		return CDOMSkill.class;
	}

	public Class<CDOMLanguage> getFalseClass()
	{
		return CDOMLanguage.class;
	}

	public String getKind()
	{
		return "CSKILL";
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
		p.setKey("Winged Mage");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getGenericPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Crossbow");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getSubKeyPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Crossbow");
		p.setSubKey("Heavy");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getParenPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Crossbow (Heavy)");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getTypeDotPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("TYPE.Exotic");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getTypeEqualsPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("TYPE=Martial");
		p.setOperand("2");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public CDOMObject grantCDOMObject(String s, AssociatedPrereqObject apo)
	{
		CDOMSkill sk = rules.getObject(getCDOMClass(), s);
		CDOMSimpleSingleRef<CDOMSkill> ref = new CDOMSimpleSingleRef<CDOMSkill>(
				getCDOMClass(), s);
		ref.addResolution(sk);
		ClassSkillList list = rules.getObject(ClassSkillList.class, "*Allowed");
		pc.getActiveLists().addToList(list, ref, apo);
		return sk;
	}

	public CDOMObject grantFalseObject(String s, AssociatedPrereqObject apo)
	{
		CDOMLanguage lan = rules.getObject(getFalseClass(), s);
		CDOMSimpleSingleRef<CDOMLanguage> ref = new CDOMSimpleSingleRef<CDOMLanguage>(
				getFalseClass(), s);
		ref.addResolution(lan);
		LanguageList list = rules.getObject(LanguageList.class, "*Allowed");
		pc.getActiveLists().addToList(list, ref, apo);
		return lan;
	}

	@Test
	public void testInvalidCount()
	{
		Prerequisite prereq = getSimplePrereq();
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

	@Test
	public void testSimple() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Wild Mage", clAPO);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Winged Mage", clAPO);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseObject() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Wild Mage", clAPO);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantFalseObject("Winged Mage", clAPO);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseSubKey() throws PrerequisiteException
	{
		Prerequisite prereq = getSubKeyPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow", clAPO);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseParen() throws PrerequisiteException
	{
		Prerequisite prereq = getParenPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow", clAPO);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testParen() throws PrerequisiteException
	{
		Prerequisite prereq = getParenPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow (Light)", clAPO);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow (Heavy)", clAPO);
		// Has Crossbow (Heavy)
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		// But not Katana
		assertEquals(0, getTest().passesCDOM(getSimplePrereq(), pc));
		// And maybe Generic Crossbow
		assertEquals(0, getTest().passesCDOM(getGenericPrereq(), pc));
	}

	@Test
	public void testSubKey() throws PrerequisiteException
	{
		Prerequisite prereq = getSubKeyPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow (Light)", clAPO);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow (Heavy)", clAPO);
		// Has Crossbow (Heavy)
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		// But not Katana
		assertEquals(0, getTest().passesCDOM(getSimplePrereq(), pc));
		// And maybe Generic Crossbow
		assertEquals(0, getTest().passesCDOM(getGenericPrereq(), pc));
	}

	@Test
	public void testTypeDot() throws PrerequisiteException
	{
		Prerequisite prereq = getTypeDotPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject katana = grantCDOMObject("Katana", clAPO);
		// Not yet the proper type
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		katana.removeListFor(ListKey.TYPE);
		// Isn't the proper type anymore
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Cool"));
		// Test having multiple types
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseTypeDot() throws PrerequisiteException
	{
		Prerequisite prereq = getTypeDotPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject katana = grantFalseObject("Katana", clAPO);
		// Not yet the proper type
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		// Would be 1 if true
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.removeListFor(ListKey.TYPE);
		// Isn't the proper type anymore
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Cool"));
		// Would be 1 if true
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testTypeEqual() throws PrerequisiteException
	{
		Prerequisite prereq = getTypeEqualsPrereq();
		// PC Should start without the WeaponProf
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject katana = grantCDOMObject("Katana", clAPO);
		// Not yet the proper type
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		// Fails because only one is present
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject sword = grantCDOMObject("Longsword", clAPO);
		sword.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		katana.removeListFor(ListKey.TYPE);
		// Isn't the proper type anymore
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Cool"));
		// Test with WP having multiple types
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseTypeEqual() throws PrerequisiteException
	{
		Prerequisite prereq = getTypeEqualsPrereq();
		// PC Should start without the WeaponProf
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject katana = grantCDOMObject("Katana", clAPO);
		// Not yet the proper type
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		// Fails because only one is present
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject sword = grantFalseObject("Longsword", clAPO);
		sword.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		// Would be 1 if true
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.removeListFor(ListKey.TYPE);
		// Isn't the proper type anymore
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Cool"));
		// Would be 1 if true
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testCrossSimple() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Wild Mage", crAPO);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Winged Mage", crAPO);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testCrossFalseSubKey() throws PrerequisiteException
	{
		Prerequisite prereq = getSubKeyPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow", crAPO);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testCrossFalseParen() throws PrerequisiteException
	{
		Prerequisite prereq = getParenPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow", crAPO);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testCrossParen() throws PrerequisiteException
	{
		Prerequisite prereq = getParenPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow (Light)", crAPO);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow (Heavy)", crAPO);
		// Has Crossbow (Heavy)
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		// But not Katana
		assertEquals(0, getTest().passesCDOM(getSimplePrereq(), pc));
		// And maybe Generic Crossbow
		assertEquals(0, getTest().passesCDOM(getGenericPrereq(), pc));
	}

	@Test
	public void testCrossSubKey() throws PrerequisiteException
	{
		Prerequisite prereq = getSubKeyPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow (Light)", crAPO);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow (Heavy)", crAPO);
		// Has Crossbow (Heavy)
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		// But not Katana
		assertEquals(0, getTest().passesCDOM(getSimplePrereq(), pc));
		// And maybe Generic Crossbow
		assertEquals(0, getTest().passesCDOM(getGenericPrereq(), pc));
	}

	@Test
	public void testCrossTypeDot() throws PrerequisiteException
	{
		Prerequisite prereq = getTypeDotPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject katana = grantCDOMObject("Katana", crAPO);
		// Not yet the proper type
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.removeListFor(ListKey.TYPE);
		// Isn't the proper type anymore
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Cool"));
		// Test having multiple types
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testCrossTypeEqual() throws PrerequisiteException
	{
		Prerequisite prereq = getTypeEqualsPrereq();
		// PC Should start without the WeaponProf
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject katana = grantCDOMObject("Katana", crAPO);
		// Not yet the proper type
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		// Fails because only one is present
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject sword = grantCDOMObject("Longsword", crAPO);
		sword.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.removeListFor(ListKey.TYPE);
		// Isn't the proper type anymore
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Cool"));
		// Test with WP having multiple types
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testNullSimple() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Wild Mage", null);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Winged Mage", null);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testNullFalseSubKey() throws PrerequisiteException
	{
		Prerequisite prereq = getSubKeyPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow", null);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testNullFalseParen() throws PrerequisiteException
	{
		Prerequisite prereq = getParenPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow", null);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testNullParen() throws PrerequisiteException
	{
		Prerequisite prereq = getParenPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow (Light)", null);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow (Heavy)", null);
		// Has Crossbow (Heavy)
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		// But not Katana
		assertEquals(0, getTest().passesCDOM(getSimplePrereq(), pc));
		// And maybe Generic Crossbow
		assertEquals(0, getTest().passesCDOM(getGenericPrereq(), pc));
	}

	@Test
	public void testNullSubKey() throws PrerequisiteException
	{
		Prerequisite prereq = getSubKeyPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow (Light)", null);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow (Heavy)", null);
		// Has Crossbow (Heavy)
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		// But not Katana
		assertEquals(0, getTest().passesCDOM(getSimplePrereq(), pc));
		// And maybe Generic Crossbow
		assertEquals(0, getTest().passesCDOM(getGenericPrereq(), pc));
	}

	@Test
	public void testNullTypeDot() throws PrerequisiteException
	{
		Prerequisite prereq = getTypeDotPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject katana = grantCDOMObject("Katana", null);
		// Not yet the proper type
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.removeListFor(ListKey.TYPE);
		// Isn't the proper type anymore
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Cool"));
		// Test having multiple types
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testNullTypeEqual() throws PrerequisiteException
	{
		Prerequisite prereq = getTypeEqualsPrereq();
		// PC Should start without the WeaponProf
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject katana = grantCDOMObject("Katana", null);
		// Not yet the proper type
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		// Fails because only one is present
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject sword = grantCDOMObject("Longsword", null);
		sword.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.removeListFor(ListKey.TYPE);
		// Isn't the proper type anymore
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		katana.addToListFor(ListKey.TYPE, Type.getConstant("Cool"));
		// Test with WP having multiple types
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	// TODO Complex Types (more than one type)

}
