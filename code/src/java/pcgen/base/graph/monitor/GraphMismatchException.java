/*
 * Copyright (c) Thomas Parker, 2005-2007.
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
 * Created on May 15, 2005
 */
package pcgen.base.graph.monitor;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A GraphMismatchException is thrown when an object is expecting method calls
 * to only relate to a specific Graph and an event or other method call is made
 * which originates from or is associated with a different Graph.
 */
public class GraphMismatchException extends RuntimeException
{

	/**
	 * Constructs a new GraphMismatchException
	 */
	public GraphMismatchException()
	{
		super();
	}

	/**
	 * Constructs a new GraphMismatchException with the given Message indicating
	 * the cause of the GraphMismatchException
	 * 
	 * @param arg0
	 *            A Message indicating the cause of the GraphMismatchException
	 */
	public GraphMismatchException(String arg0)
	{
		super(arg0);
	}
}
