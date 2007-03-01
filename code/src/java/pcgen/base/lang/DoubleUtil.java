/*
 * Copyright (c) Thomas Parker 2007
 * derived from CoreUtility.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 */
package pcgen.base.lang;

public final class DoubleUtil
{

	private DoubleUtil()
	{
		// Can't instantiate
	}

	/**
	 * Compare two doubles within a given epsilon, using a default epsilon of
	 * 0.0001.
	 * 
	 * @param a
	 * @param b
	 * @return TRUE if equal, else FALSE
	 */
	public static boolean doublesEqual(double a, double b)
	{
		// If the difference is less than epsilon, treat as equal.
		return compareDouble(a, b, 0.0001);
	}

	/**
	 * Compare two doubles within a given epsilon.
	 * 
	 * @param a
	 * @param b
	 * @param eps
	 * @return TRUE if equal, else FALSE
	 */
	public static boolean compareDouble(double a, double b, double eps)
	{
		// If the difference is less than epsilon, treat as equal.
		return Math.abs(a - b) < eps;
	}

}
