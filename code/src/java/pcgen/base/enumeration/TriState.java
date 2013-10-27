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
 * A TriState is a class that represents an object that has three states: Yes,
 * No, and Undetermined. This is useful in situations where the Default state
 * cannot be represented by the null value when using a Boolean object.
 * 
 * @author Tom Parker <thpr@users.sourceforge.net>
 */
public enum TriState
{
	YES {
		@Override
		public boolean booleanValue()
		{
			return true;
		}
	},

	NO {
		@Override
		public boolean booleanValue()
		{
			return false;
		}
	},

	UNDETERMINED {
		@Override
		public boolean booleanValue()
		{
			throw new IllegalResolutionException();
		}
	};

	public abstract boolean booleanValue();

	public static class IllegalResolutionException extends RuntimeException
	{

		public IllegalResolutionException()
		{
			super();
		}

	}
}
