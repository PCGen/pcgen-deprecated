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
 * 
 * Created on Dec 15, 2006
 */
package pcgen.base.enumeration;

/**
 * A SequencedType is an object that has a specific sequence in the instances of
 * that class.
 * 
 * It is possible that the sequence number is unique, but that is not required.
 * If you are looking for a unique identifier, a TypeSafeConstant should be
 * used.
 * 
 * It is possible that the sequence number is sequential (1, 2, 3, etc.), but
 * that is not required.
 * 
 * @author Tom Parker <thpr@users.sourceforge.net>
 */
public interface SequencedType
{
	/**
	 * Returns the sequence number for the SequencedType.
	 * 
	 * @return An integer sequence number
	 */
	public int getSequence();
}
