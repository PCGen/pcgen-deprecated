/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Created on July 22, 2005.
 * 
 * Current Ver: $Revision: 1182 $ Last Editor: $Author: jujutsunerd $ Last
 * Edited: $Date: 2006-07-08 09:56:53 -0400 (Sat, 08 Jul 2006) $
 */
package pcgen.cdom.content;

import pcgen.cdom.base.ConcretePrereqObject;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 */
public class SimpleMovement extends ConcretePrereqObject
{

	private final String type;

	/**
	 * Contains the associated movement rate (in feet) for the movement type. A
	 * movement rate must be greater than or equal to zero.
	 */
	private double movement;

	/**
	 * Creates a Movement object
	 */
	public SimpleMovement(String moveType, int i)
	{
		super();
		if (i < 0)
		{
			throw new IllegalArgumentException(
				"Movement rate cannot be negative");
		}
		type = moveType;
		movement = i;
	}

	/**
	 * @return movement as a Double
	 */
	public double getMovement()
	{
		return movement;
	}

	/**
	 * Get the movement type
	 * 
	 * @return movement type
	 */
	public String getMovementType()
	{
		return type;
	}

	/**
	 * Converts this Movement object into a format suitable for storage in an
	 * LST or equivalent file. This method should be the complement of the
	 * static getMovementFrom() method.
	 * 
	 * @return a String in LST/PCC file format, suitable for persistent storage
	 */
	public String toLSTString()
	{
		StringBuilder txt = new StringBuilder();
		txt.append("\tMOVE:");
		txt.append(type);
		txt.append(',');
		txt.append(movement);
		return txt.toString();
	}
}
