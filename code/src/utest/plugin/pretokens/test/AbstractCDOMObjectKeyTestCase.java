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
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public abstract class AbstractCDOMObjectKeyTestCase<T extends CDOMObject, OT>
		extends AbstractCDOMPreTestTestCase<T>
{

	public Prerequisite getSimplePrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey(getPassObject().toString());
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public abstract String getKind();

	public abstract PrerequisiteTest getTest();

	public abstract ObjectKey<OT> getObjectKey();

	public abstract OT getPassObject();

	public abstract OT getFailObject();

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
		CDOMObject obj1 = grantCDOMObject("Wild Mage");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		obj1.put(getObjectKey(), getFailObject());
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject obj2 = grantCDOMObject("Winged Mage");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		obj2.put(getObjectKey(), getFailObject());
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		obj2.put(getObjectKey(), getPassObject());
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseObject() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject obj1 = grantCDOMObject("Wild Mage");
		obj1.put(getObjectKey(), getFailObject());
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject obj2 = grantFalseObject("Winged Mage");
		obj2.put(getObjectKey(), getFailObject());
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		obj2.put(getObjectKey(), getPassObject());
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	// TODO Need to consider inverted? !PRE?

}
