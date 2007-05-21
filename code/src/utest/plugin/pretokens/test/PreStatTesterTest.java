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

import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.core.Language;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreStatTesterTest extends AbstractCDOMPreTestTestCase<PCStat>
{

	PreStatTester tester = new PreStatTester();

	@Override
	public Class<PCStat> getCDOMClass()
	{
		return PCStat.class;
	}

	@Override
	public Class<? extends PObject> getFalseClass()
	{
		return Language.class;
	}

	public String getKind()
	{
		return "STAT";
	}

	public PrerequisiteTest getTest()
	{
		return tester;
	}

	public Prerequisite getSimplePrereq(String stat, int val)
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey(stat);
		p.setOperand(Integer.toString(val));
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getNoOperatorPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("INT");
		p.setOperand("15");
		return p;
	}

	@Test
	public void testSimple() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq("STR", 15);
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		PCStat strStat = getObject("STR");
		PCGraphGrantsEdge strEdge = grantObject(strStat);
		// Fail, no weight on edge
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		PCStat intStat = getObject("INT");
		PCGraphGrantsEdge intEdge = grantObject(intStat);
		intEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(15));
		// Fail, no weight on str edge (still)
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(13));
		// Fail, not enough weight on edge
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(15));
		// Pass, exact value
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		strEdge.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(16));
		// Pass, more than enough
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}
}
