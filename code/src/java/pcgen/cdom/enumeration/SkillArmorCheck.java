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

public enum SkillArmorCheck
{

	NO() {
		@Override
		public Object getCheckPenalty()
		{
			return new Double(0);
		}
	},
	YES() {
		@Override
		public Object getCheckPenalty()
		{
			// FIXME This is wrong!!!
			return new Double(0);
		}
	},
	PROFICIENT() {
		@Override
		public Object getCheckPenalty()
		{
			// FIXME This is wrong!!!
			return new Double(0);
		}
	},
	DOUBLE() {
		@Override
		public Object getCheckPenalty()
		{
			// FIXME This is wrong!!!
			return new Double(0);
		}
	},
	WEIGHT() {
		@Override
		public Object getCheckPenalty()
		{
			// FIXME This is wrong!!!
			return new Double(0);
		}
	};

	// FIXME Need to make this more than Object!
	public abstract Object getCheckPenalty();

	// # NO - Armor check penalty does not apply (Default).
	// # YES - Armor check penalty applies to this skill.
	// # PROFICIENT - Armor check penalty applies if the character is not
	// proficient in it.
	// # DOUBLE - Armor check penalty of double normal applies to this skill.
	// # WEIGHT - Weight carried penalty applies to the skill check.

}
