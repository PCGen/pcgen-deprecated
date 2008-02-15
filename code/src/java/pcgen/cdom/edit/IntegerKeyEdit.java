/*
 * Copyright (c) Thomas Parker, 2007.
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
package pcgen.cdom.edit;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.IntegerKey;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * DeleteGraphNode is an UndoableEdit which represents the atomic deletion of a
 * Node from a Graph.
 * 
 * 'Atomic' indicates that this UndoableEdit does not capture or reflect any
 * side-effects that took place during the removal of the Node from the Graph.
 * (This class does not actually perform the removal of the Node). In order to
 * capture the side-effects and have an UndoableEdit which reflects all of the
 * changes that took place to a Graph as a result of the removal of a Node, see
 * DeleteNodeCommand.
 * 
 * @see rpgmapgen.util.graph.command.DeleteNodeCommand
 */
public class IntegerKeyEdit extends AbstractUndoableEdit implements
		UndoableEdit
{

	/**
	 * The presentation name of this Edit
	 */
	private final String name;

	/**
	 * The CDOMObject to be modified, resulting in the Edit
	 */
	private final CDOMObject cdo;

	/**
	 * The IntegerKey used to modify the object in this Command
	 */
	private final IntegerKey integerKey;

	/**
	 * The old Integer value for the Edit
	 */
	private final Integer oldValue;

	/**
	 * The new Integer value for the Edit
	 */
	private final Integer newValue;

	/**
	 * Represents an Edit of a IntegerKey on a CDOMObject.
	 * 
	 * The CDOMObject is passed by Reference into IntegerKeyEdit, and
	 * IntegerKeyEdit therefore maintains a reference to this object.
	 * IntegerKeyEdit then agrees ("by contract") that it will only (a) call
	 * appropriate methods in CDOMObject interface.
	 * 
	 * @param editPresentationName
	 *            The presentation name for the Edit (designed to be shown to
	 *            the user of an application)
	 * @param cdomObject
	 *            The CDOMObject to be changed by this Edit
	 * @param key
	 *            The key used by this Edit to know which Integer to change in
	 *            the CDOMObject
	 * @param oldVal
	 *            The old value of the Integer before the edit was performed
	 *            (placed into the CDOMObject when undo is called).
	 * @param newVal
	 *            The new value of the Integer before the edit was performed
	 *            (placed into the CDOMObject when redo is called).
	 */
	public IntegerKeyEdit(String editPresentationName, CDOMObject cdomObject,
		IntegerKey key, Integer oldVal, Integer newVal)
	{
		if (cdomObject == null)
		{
			throw new IllegalArgumentException("CDOMObject cannot be null");
		}
		if (key == null)
		{
			throw new IllegalArgumentException("IntegerKey cannot be null");
		}
		if (editPresentationName == null
			|| "".equals(editPresentationName.trim()))
		{
			name = "Edit Integer Key";
		}
		else
		{
			name = editPresentationName;
		}
		cdo = cdomObject;
		integerKey = key;
		oldValue = oldVal;
		newValue = newVal;
	}

	/**
	 * Returns the user-presentable name for this UndoableEdit
	 * 
	 * @see javax.swing.undo.AbstractUndoableEdit#getPresentationName()
	 */
	@Override
	public String getPresentationName()
	{
		return name;
	}
}
