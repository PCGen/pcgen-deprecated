/*
 * RaceTest.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 8/12/2007
 *
 * $Id$
 */
package pcgen.core;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.analysis.RaceStat;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.StatLock;

/**
 * <code>RaceTest</code> tests the function of the Race class.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class RaceTest extends AbstractCharacterTestCase
{
	
	/**
	 * Test the isUnlocked method of Race.
	 */
	public void testIsUnlocked()
	{
		Race race = new Race();
		race.setName("Test Race");
		StatList statList = getCharacter().getStatList();
		int index = statList.getIndexOfStatFor("STR");
		PCStat str = statList.getStatAt(index);
		assertEquals("Template has not been unlocked", false, RaceStat.isUnlocked(index, race));
		race.addToListFor(ListKey.STAT_LOCKS, new StatLock(str, FormulaFactory.getFormulaFor(12)));
		assertEquals("Template has not been unlocked", false, RaceStat.isUnlocked(index, race));
		race.addToListFor(ListKey.UNLOCKED_STATS, str);
		assertEquals("Template has been unlocked", true, RaceStat.isUnlocked(index, race));
	}
	
	/**
	 * Test the isNonAbility method of Race.
	 */
	public void testIsNonAbility()
	{
		Race race = new Race();
		race.setName("Test Race");
		StatList statList = getCharacter().getStatList();
		int index = statList.getIndexOfStatFor("STR");
		PCStat str = statList.getStatAt(index);
		assertEquals("Template has not been locked to a nonability", false, RaceStat.isNonAbility(index, race));
		race.addToListFor(ListKey.STAT_LOCKS, new StatLock(str, FormulaFactory.getFormulaFor(12)));
		assertEquals("Template has been locked to an ability", false, RaceStat.isNonAbility(index, race));
		race.addToListFor(ListKey.STAT_LOCKS, new StatLock(str, FormulaFactory.getFormulaFor(10)));
		assertEquals("Template has been locked to a nonability", true, RaceStat.isNonAbility(index, race));
		race.addToListFor(ListKey.UNLOCKED_STATS, str);
		assertEquals("Template has been unlocked", false, RaceStat.isNonAbility(index, race));
	}
	
	/**
	 * Verify the function of the sizesAdvanced method.
	 */
	public void testSizesAdvanced()
	{
		Race race = new Race();
		race.setName("Test Race");
		
		// Validate that there are no size changes if no advancement is specified
		assertEquals("Size increase where none specified wrong", 0, race.sizesAdvanced(1));
		assertEquals("Size increase where none specified wrong", 0, race.sizesAdvanced(2));
		assertEquals("Size increase where none specified wrong", 0, race.sizesAdvanced(3));
		assertEquals("Size increase where none specified wrong", 0, race.sizesAdvanced(4));
		assertEquals("Size increase where none specified wrong", 0, race.sizesAdvanced(5));

		// Validate that size changes occur when needed and no extra happen if advancement is specified
		race.addToListFor(ListKey.HITDICE_ADVANCEMENT, 2);
		race.addToListFor(ListKey.HITDICE_ADVANCEMENT, 4);
		assertEquals("Size increase pre first change wrong", 0, race.sizesAdvanced(1));
		assertEquals("Size increase pre first change wrong", 0, race.sizesAdvanced(2));
		assertEquals("Size increase pre last change wrong", 1, race.sizesAdvanced(3));
		assertEquals("Size increase pre last change wrong", 1, race.sizesAdvanced(4));
		assertEquals("Size increase post last change wrong", 1, race.sizesAdvanced(5));
		assertEquals("Size increase post last change wrong", 1, race.sizesAdvanced(6));
	}
}
