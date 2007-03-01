/*
 * SpellProhibitor.java Copyright 2005 (c) Stefan Raderamcher
 * <radermacher@netcologne.de>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on March 3, 2005, 16:30 AM
 * 
 * Current Ver: $Revision: 1522 $ Last Editor: $Author: thpr $ Last Edited:
 * $Date: 2006-10-24 18:40:09 -0400 (Tue, 24 Oct 2006) $
 * 
 */
package pcgen.cdom.available;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.enumeration.TypeSafeConstant;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.enumeration.ProhibitedSpellType;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;

/**
 * @author stefan
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SpellProhibitor<T extends TypeSafeConstant> extends
		ConcretePrereqObject
{

	private ProhibitedSpellType<T> type = null;

	private List<T> valueList = null;

	public SpellProhibitor()
	{
		super();
	}

	public ProhibitedSpellType<T> getType()
	{
		return type;
	}

	public List<T> getValueList()
	{
		return valueList;
	}

	public void setType(ProhibitedSpellType<T> prohibitedType)
	{
		type = prohibitedType;
	}

	public void addValue(T value)
	{
		if (valueList == null)
		{
			valueList = new ArrayList<T>();
		}
		valueList.add(value);
	}

	public boolean isProhibited(Spell s, PlayerCharacter aPC)
	{
		/*
		 * Note the rule is only "Prohibit Cleric/Druid spells based on
		 * Alignment" - thus this Globals check is only relevant to the
		 * Alignment type
		 */
		if (type.equals(ProhibitedSpellType.ALIGNMENT)
			&& !Globals.checkRule(RuleConstants.PROHIBITSPELLS))
		{
			return false;
		}

		List<Prerequisite> prereqList = getPrerequisiteList();
		if (prereqList != null
			&& !PrereqHandler.passesAll(prereqList, aPC, null))
		{
			return false;
		}

		int hits = 0;

		for (T typeDesc : type.getCheckList(s))
		{
			for (T prohib : valueList)
			{
				if (prohib.equals(typeDesc))
				{
					hits++;
				}
			}
		}

		return hits == valueList.size();
	}
}
