/*
 * Copyright (c) Thomas Parker, 2004, 2005.
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
 * Created on Sep 11, 2004
 * 
 */
package pcgen.base.lang;

import javax.swing.undo.UndoableEdit;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A Command is an executable item. A Command possesses a presentation name, in
 * order that the command can be presented to the user (for example, in a menu).
 * A Command also returns an UndoableEdit from the execute method, in order that
 * the Command which was performed can be reversed.
 */
public interface Command
{
	/**
	 * Execute the command, returning an UndoableEdit capable of undoing the
	 * changes performed by the Command. The returned value must not be null; if
	 * no undoable action is performed by the Command (such as a Command that
	 * displays information) then an InsignifcantUndoableEdit should be
	 * returned.
	 * 
	 * @return An UndoableEdit capable of undoing the changes performed by the
	 *         Command.
	 */
	public UndoableEdit execute();

	/**
	 * The presentation name (a String designed to be interpreted by the user of
	 * the application) of the Command.
	 * 
	 * @return The presentation name of the Command
	 */
	public String getPresentationName();
}
