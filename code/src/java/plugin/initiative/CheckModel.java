/*
 *  pcgen - DESCRIPTION OF PACKAGE
 *  Copyright (C) 2004 Ross M. Lodge
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  StatModel.java
 *
 *  Created on Jan 16, 2004, 1:57:35 PM
 */
package plugin.initiative;

/**
 * <p>
 * Models a generic 1d20+/-X check.
 * </p>
 * 
 * @author Ross M. Lodge
 *
 */
public class CheckModel extends DiceRollModel
{

	/**
	 * <p>
	 * Constructs a new Check model based on a string.  The string should
	 * have the following tokens, in the following order, separated by
	 * backslashes:
	 * </p>
	 * 
	 * <ol>
	 * <li>Check name</li>
	 * <li>1d20+/-Whatever</li>
	 * </ol>
	 * @param objectString String description of stat
	 */
	public CheckModel(String objectString)
	{
		super(objectString);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "Check: " + super.toString();
	}

}
