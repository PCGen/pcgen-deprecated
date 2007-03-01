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
package pcgen.cdom.helper;

import pcgen.cdom.enumeration.Type;

public class Capacity
{

	/*
	 * CONSIDER Need to flesh out how this works; depends on how Capacity
	 * interacts with the core... - Tom Parker 3/1/07
	 */
	public static final Capacity ANY = new Capacity(null, -1);

	private final Type type;

	private final double limit;

	public Capacity(Type typ, double cap)
	{
		type = typ;
		limit = cap;
	}

	public double getCapacity()
	{
		return limit;
	}

	public Type getType()
	{
		return type;
	}

	public static Capacity getTotalCapacity(double d)
	{
		return new Capacity(null, d);
	}

}
