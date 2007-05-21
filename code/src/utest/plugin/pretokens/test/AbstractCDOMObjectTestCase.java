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

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public abstract class AbstractCDOMObjectTestCase<T extends PObject> extends
		AbstractCDOMPreTestTestCase<T>
{

	public Prerequisite getAnyPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("ANY");
		p.setOperand("2");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
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

	public Prerequisite getWildcard()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Wild%");
		p.setOperand("2");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getCountTemplates()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("%");
		p.setOperand("2");
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

	public abstract String getKind();

	public abstract PrerequisiteTest getTest();

	public abstract boolean isWildcardLegal();

	public abstract boolean isTypeLegal();

	public abstract boolean isAnyLegal();

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
	}

	@Test
	public void testAny() throws PrerequisiteException
	{
		if (isAnyLegal())
		{
			Prerequisite prereq = getAnyPrereq();
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantCDOMObject("Wild Mage");
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantCDOMObject("Winged Mage");
			assertEquals(1, getTest().passesCDOM(prereq, pc));
			assertEquals(0, getTest().passesCDOM(getParenPrereq(), pc));
		}
	}

	@Test
	public void testFalseAny() throws PrerequisiteException
	{
		if (isAnyLegal())
		{
			Prerequisite prereq = getAnyPrereq();
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantCDOMObject("Wild Mage");
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantFalseObject("Winged Mage");
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			assertEquals(0, getTest().passesCDOM(getParenPrereq(), pc));
		}
	}

	@Test
	public void testSimple() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Wild Mage");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Winged Mage");
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		assertEquals(0, getTest().passesCDOM(getParenPrereq(), pc));
	}

	@Test
	public void testFalseObject() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Wild Mage");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantFalseObject("Winged Mage");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testParen() throws PrerequisiteException
	{
		Prerequisite prereq = getParenPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Crossbow (Heavy)");
		// Has Crossbow (Heavy)
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		// But not Katana
		assertEquals(0, getTest().passesCDOM(getSimplePrereq(), pc));
		// And not Generic Crossbow
		assertEquals(0, getTest().passesCDOM(getGenericPrereq(), pc));
	}

	@Test
	public void testWildcard() throws PrerequisiteException
	{
		if (isWildcardLegal())
		{
			Prerequisite prereq = getWildcard();
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantCDOMObject("Winged Creature");
			// Still at zero qualifying
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantCDOMObject("Wild Mage");
			// Not enough
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantCDOMObject("Wild Beast");
			assertEquals(1, getTest().passesCDOM(prereq, pc));
		}
	}

	@Test
	public void testFalseWildcard() throws PrerequisiteException
	{
		if (isWildcardLegal())
		{
			Prerequisite prereq = getWildcard();
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantCDOMObject("Winged Creature");
			// Still at zero qualifying
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantCDOMObject("Wild Mage");
			// Not enough
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantFalseObject("Wild Beast");
			assertEquals(0, getTest().passesCDOM(prereq, pc));
		}
	}

	@Test
	public void testCount() throws PrerequisiteException
	{
		if (isWildcardLegal())
		{
			Prerequisite prereq = getCountTemplates();
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantCDOMObject("Winged Creature");
			// Still at zero qualifying
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantCDOMObject("Wild Mage");
			// enough
			assertEquals(1, getTest().passesCDOM(prereq, pc));
			grantCDOMObject("Wild Beast");
			// more than enough
			assertEquals(1, getTest().passesCDOM(prereq, pc));
		}
	}

	@Test
	public void testFalseCount() throws PrerequisiteException
	{
		if (isWildcardLegal())
		{
			Prerequisite prereq = getCountTemplates();
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantCDOMObject("Winged Creature");
			// Still at zero qualifying
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			grantFalseObject("Wild Mage");
			assertEquals(0, getTest().passesCDOM(prereq, pc));
		}
	}

	@Test
	public void testTypeDot() throws PrerequisiteException
	{
		if (isTypeLegal())
		{
			Prerequisite prereq = getTypeDotPrereq();
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			PObject katana = grantCDOMObject("Katana");
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
	}

	@Test
	public void testFalseTypeDot() throws PrerequisiteException
	{
		if (isTypeLegal())
		{

			Prerequisite prereq = getTypeDotPrereq();
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			PObject katana = grantFalseObject("Katana");
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
	}

	@Test
	public void testTypeEqual() throws PrerequisiteException
	{
		if (isTypeLegal())
		{

			Prerequisite prereq = getTypeEqualsPrereq();
			// PC Should start without the WeaponProf
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			PObject katana = grantCDOMObject("Katana");
			// Not yet the proper type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
			// Fails because only one is present
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			PObject sword = grantCDOMObject("Longsword");
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
	}

	@Test
	public void testFalseTypeEqual() throws PrerequisiteException
	{
		if (isTypeLegal())
		{

			Prerequisite prereq = getTypeEqualsPrereq();
			// PC Should start without the WeaponProf
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			PObject katana = grantCDOMObject("Katana");
			// Not yet the proper type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
			// Fails because only one is present
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			PObject sword = grantFalseObject("Longsword");
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
	}
}
