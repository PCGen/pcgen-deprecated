/*
 * Copyright (c) Thomas Parker, 2005, 2007.
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 * 
 * Created on Apr 23, 2005
 *
 */
package pcgen.util;

/**
 * @author Thomas Parker (thpr@sourceforge.net)
 * 
 * An UnreachableError is an error caused by code that the programmer thought
 * was Unreachable. This is preferred to an InternalError, as an InternalError
 * should be reserved for VirtualMachine errors. This error, on the other hand,
 * indicates that the original developer did not consider certain situations (or
 * additional features were added to an object or the language that the
 * developer did not have available).
 * 
 * The use of an Error rather than an Exception is preferred, due to the (bad)
 * habit of certain code catching Exception or RuntimeException. The use of an
 * error prevents this from being caught in those situations, ensuring that the
 * problem will be exposed.
 */
public class UnreachableError extends Error
{

	/**
	 * For object serialization
	 */
	private static final long serialVersionUID = -5431319083877458886L;

	/**
	 * Create a new UnreachableError with no message and no cause.
	 */
	public UnreachableError()
	{
		super();
	}

	/**
	 * Create a new UnreachableError with the given message
	 * 
	 * @param arg0
	 *            The message indicating the cause of UnreachableError
	 */
	public UnreachableError(String arg0)
	{
		super(arg0);
	}

	/**
	 * Create a new UnreachableError with the given cause
	 * 
	 * @param arg0
	 *            The cause of the UnreachableError
	 */
	public UnreachableError(Throwable arg0)
	{
		super(arg0);
	}

	/**
	 * Create a new UnreachableError with the given message and cause
	 * 
	 * @param arg0
	 *            The message indicating the cause of UnreachableError
	 * @param arg1
	 *            The cause of the UnreachableError
	 */
	public UnreachableError(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
