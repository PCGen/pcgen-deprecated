/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Created on June 18, 2005.
 *
 * Current Ver: $Revision: 1384 $
 * Last Editor: $Author: boomer70 $
 * Last Edited: $Date: 2006-09-12 00:29:17 -0400 (Tue, 12 Sep 2006) $
 */
package pcgen.cdom.enumeration;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 *
 * This is a Typesafe enumeration of legal Integer Characteristics of an object.
 */
public enum IntegerKey {
	    
	/** Key to a characteristic defining the number of levels to be added to a spell. */
	 ADD_SPELL_LEVEL ,
	/** Key to a characteristic defining the type of ability. */
//	 ABILITY_TYPE ,
	/** Key to a characteristic defining the format category of the object. */
	 FORMAT_CAT ,
	/** Key to a characteristic defining the number of hit dice the object has. */
	 HIT_DIE ,
	/** Key to a characteristic defining the level of the object. */
	 LEVEL ,
	/** Key to a characteristic defining the number of pages of the object. */
	 NUM_PAGES ,
	/** Key to a characteristic defining the loading rank of the object. */
	 RANK ,
	 UMULT ,
	 REACH ,
	 HANDS ,
	 LEGS ,
	 NONPP ,
	 INITIATIVE ,
	 STARTINGAC ,
	 LANGNUM ,
	 SKILL_POINTS_PER_LEVEL ,
	 INITIAL_SKILL_MULT ,
	 XTRAFEATS ,
	 MAXLEVEL ,
	 LEVELS_PER_FEAT ,
	 KNOWN_SPELLS_FROM_SPECIALTY ,
	 XP_COST ,
	 PP_COST ,
	 CASTING_THRESHOLD ,
	 PLUS ,
	 MIN_CHARGES ,
	 MAX_CHARGES ,
	 SPELL_FAILURE ,
	 SLOTS ,
	 REACH_MULT ,
	 RANGE ,
	 MAX_DEX_BONUS ,
	 CRIT_RANGE ,
	 AC_CHECK ,
	 BASE_QUANTITY ,
	 EDR,
	 CRIT_MULT, 
	 CONTAINER_REDUCE_WEIGHT,
	 OUTPUT_INDEX,
	 OUTPUT_SUBINDEX,
	 CONSECUTIVE,
	 MAX_LEVEL,
	 LEVEL_INCREMENT,
	 START_LEVEL,
	 LEVEL_LIMIT,
	 LEX_MIN_DONATING_LEVEL,
	 LEX_MAX_DONATING_LEVEL,
	 LEX_MIN_DONATING_REMAINING,
	 SEQUENCE_NUMBER,
	 MINIMUM,
	 MAXIMUM,
	 STATRANGE,
	 BONUSSTATSCORE,
	 BONUS_CLASS_SKILL_POINTS,
	 QUANTITY,
	 NUMBER_CARRIED,
	 START_FEATS;
}
