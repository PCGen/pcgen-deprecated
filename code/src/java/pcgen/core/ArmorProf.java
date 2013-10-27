/*
 * ArmorProf.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.core;


/**
 * <code>ArmorProf</code>.
 *
 * @author Thomas Clegg <arknight@swbell.net>
 * @version $Revision$
 * DO NOT DELETE (waiting for use)
 */
public final class ArmorProf extends PObject implements Comparable<Object>
{
	/**
	 * Compares keyName only
	 *
	 * @param o1 Object
	 * @return int
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(final Object o1)
	{
		return keyName.compareToIgnoreCase(((ArmorProf) o1).keyName);
	}

	/**
	 * Compares keyName only
	 *
	 * @param o1 Object
	 * @return boolean
	 */
	@Override
	public boolean equals(final Object o1)
	{
		return o1 instanceof ArmorProf && keyName.equals(((ArmorProf) o1).keyName);
	}

	/**
	 * Hashcode of the keyName
	 *
	 * @return int
	 */
	@Override
	public int hashCode()
	{
		return keyName.hashCode();
	}
}
