/*
 * SkillTokenUtilities.java
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
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
 * Created on Mar 2, 2008, 10:10:04 PM
 */
package plugin.qualifier.skill;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.AssociatedObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.cdom.inst.CDOMSkill;
import pcgen.cdom.inst.ClassSkillList;
import pcgen.cdom.lists.PCGenLists;
import pcgen.character.CharacterDataStore;
import pcgen.rules.persistence.token.ChooseLstQualifierToken;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
class SkillTokenUtilities
{

    public static Set<CDOMSkill> getSet(CharacterDataStore pc, boolean not,
                                          SkillCost cost)
    {
        Set<CDOMSkill> skillSet = new HashSet<CDOMSkill>();
        PCGenLists activeLists = pc.getActiveLists();
        Set<ClassSkillList> lists = activeLists.getLists(ClassSkillList.class);
        if (lists != null)
        {
            for (ClassSkillList csl : lists)
            {
                Collection<CDOMSkill> contents = activeLists.getListContents(csl);
                for (CDOMSkill sk : contents)
                {
                    AssociatedObject assoc =
                            activeLists.getListAssociation(csl, sk);
                    if (not ^
                            cost.equals(assoc.getAssociation(AssociationKey.SKILL_COST)))
                    {
                        skillSet.add(sk);
                    }
                }
            }
        }
        return skillSet;
    }

}
