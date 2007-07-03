/*
 * Copyright (c) 2006 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula;

/**
 * A Resolver is an object which is capable of returning a primitive object of a
 * given type. This will typically be done when a specific instance cannot be
 * resolved at the time data is loaded (but rather must be processed at
 * runtime).
 */
public interface Resolver<T>
{

	/**
	 * Resolves this "reference" to the underlying primitive object.
	 * 
	 * @return The underlying object this "reference" resolves to.
	 */
	public T resolve();

	/**
	 * Converts the reference to the underlying primitive object to a format
	 * that is compatible with LST files.
	 * 
	 * @return The LST format for the reference to the underlying object.
	 */
	public String toLSTFormat();
}
