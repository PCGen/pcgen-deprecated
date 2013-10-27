/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.cdom.enumeration;

public enum RuleConstants
{

	ABILRANGE, // Allow any range for ability scores
	AMMOSTACKSWITHWEAPON, // Do ammo enhancement bonus stack with those of the
	// weapon
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
	// SYS_CIP , // Improper tools incure a -2 circumstance penalty
	// SYS_DOMAIN , // Apply Casterlevel Bonuses from Domains to Spells
	SYS_LDPACSK, // Apply Load Penalty to AC and Skills
	SYS_WTPSK; // Apply Weight Penalty to Skills

}
