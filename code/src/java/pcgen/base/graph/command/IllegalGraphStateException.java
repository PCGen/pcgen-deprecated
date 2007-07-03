/*
 * Copyright (c) Thomas Parker, 2004-2007.
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
 * Created on Oct 2, 2004
 */
package pcgen.base.graph.command;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * An IllegalGraphStateException is thrown when a Graph is in a state which is
 * illegal relative to the execution of a given command. For example, it may
 * only be possible to insert an Edge after all of the nodes to which the edge
 * is connected have already been added to the Graph. Attempt to add the edge
 * before all of the nodes are present in the graph could result in an
 * IllegalGraphStateException being thrown.
 */
public class IllegalGraphStateException extends IllegalStateException
{

	/**
	 * Constructs a new IllegalGraphStateException with the given Message.
	 * 
	 * @param message
	 *            Message indicating the cause of the
	 *            IllegalGraphStateException.
	 */
	public IllegalGraphStateException(String message)
	{
		super(message);
	}
}