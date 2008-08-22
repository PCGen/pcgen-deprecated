/*
 * PCGenUIManager.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jul 14, 2008, 8:43:48 PM
 */
package pcgen.gui;

import java.io.File;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import pcgen.gui.facade.AbilityCatagoryFacade;
import pcgen.gui.facade.AbilityFacade;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.filter.NamedFilter;
import pcgen.gui.generator.Generator;
import pcgen.gui.util.DefaultGenericListModel;
import pcgen.gui.util.GenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class PCGenUIManager
{

    private static final Map<HouseRule, Boolean> rulesMap = new EnumMap<HouseRule, Boolean>(HouseRule.class);
    private static File pcgenPreviewDir;

    private PCGenUIManager()
    {
    }

    public static boolean isQualified(CharacterFacade character, ClassFacade c)
    {
        return false;
    }

    public static <T> GenericListModel<NamedFilter<? super T>> getDisplayedFilters(Class<T> c)
    {
        return null;
    }

    public static DefaultGenericListModel<AbilityCatagoryFacade> getRegisteredAbilityCatagories(CharacterFacade character)
    {
        return null;
    }

    public static DefaultGenericListModel<AbilityFacade> getRegisteredAbilities(CharacterFacade character,
                                                                                  AbilityCatagoryFacade catagory)
    {
        return null;
    }

    public static GenericListModel<SkillFacade> getRegisteredSkills(CharacterFacade character)
    {
        return null;
    }

    public static List<Generator<String>> getRegisteredNameGenerators()
    {
        return null;
    }

    public static Collection<File> getCharacterSheets()
    {
        return null;
    }

    public static boolean isHouseRuleSelected(HouseRule rule)
    {
        Boolean b = rulesMap.get(rule);
        return b != null && b;
    }

    public static String getDefaultCharacterName()
    {
        return null;
    }

    public static void setPcgenPreviewDir(final File aFile)
    {
        pcgenPreviewDir = aFile;
    }

    public static File getPcgenPreviewDir()
    {
        return pcgenPreviewDir;
    }

    public static enum HouseRule
    {

        ABILRANGE, //Allow any range for ability scores
        AMMOSTACKSWITHWEAPON, // Do ammunition enhancement bonus stack with those of the weapon
        BONUSSPELLKNOWN, // Add stat bonus to Spells Known
        CLASSPRE, // Bypass Class Prerequisites
        EQUIPATTACK, // Treat Weapons In Hand As Equipped For Attacks
        FEATPRE, // Bypass Feat Prerequisites
        FREECLOTHES, // Ask For Free Clothing at First Level
        INTBEFORE, // Increment STAT before calculating skill points when leveling
        INTBONUSLANG, // Allow Selection of Int bonus Languages after 1st level
        LEVELCAP, // Ignore Level Cap
        PROHIBITSPELLS, // Restict Cleric/Druid spells based on alignment
        SIZECAT, // Use 3.5 Weapon Categories
        SIZEOBJ, // Use 3.0 Weapon Size
        SKILLMAX, // Bypass Max Skill Ranks
        SYS_35WP, // Apply 3.5 Size Category Penalty to Attacks
        //	SYS_CIP, //Improper tools incure a -2 circumstance penalty
        //	SYS_DOMAIN, // Apply Casterlevel Bonuses from Domains to Spells
        SYS_LDPACSK, // Apply Load Penalty to AC and Skills
        SYS_WTPSK; // Apply Weight Penalty to Skills
    }
}
