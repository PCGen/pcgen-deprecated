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
import pcgen.cdom.enumeration.ObjectKey;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * ObjectKeyEdit is an UndoableEdit which represents the atomic deletion of a
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
public class ObjectKeyEdit<T> extends AbstractUndoableEdit implements
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
	 * The ObjectKey used to modify the object in this Command
	 */
	private final ObjectKey<T> objectKey;

	/**
	 * The old String value for the Edit
	 */
	private final T oldValue;

	/**
	 * The new String value for the Edit
	 */
	private final T newValue;

	/**
	 * Represents an Edit of a ObjectKey on a CDOMObject.
	 * 
	 * The CDOMObject is passed by Reference into ObjectKeyEdit, and
	 * ObjectKeyEdit therefore maintains a reference to this object.
	 * ObjectKeyEdit then agrees ("by contract") that it will only (a) call
	 * appropriate methods in CDOMObject interface.
	 * 
	 * @param editPresentationName
	 *            The presentation name for the Edit (designed to be shown to
	 *            the user of an application)
	 * @param cdomObject
	 *            The CDOMObject to be changed by this Edit
	 * @param key
	 *            The key used by this Edit to know which String to change in
	 *            the CDOMObject
	 * @param oldVal
	 *            The old value of the String before the edit was performed
	 *            (placed into the CDOMObject when undo is called).
	 * @param newVal
	 *            The new value of the String before the edit was performed
	 *            (placed into the CDOMObject when redo is called).
	 */
	public ObjectKeyEdit(String editPresentationName, CDOMObject cdomObject,
		ObjectKey<T> key, T oldVal, T newVal)
	{
		if (cdomObject == null)
		{
			throw new IllegalArgumentException("CDOMObject cannot be null");
		}
		if (key == null)
		{
			throw new IllegalArgumentException("ObjectKey cannot be null");
		}
		if (editPresentationName == null
			|| "".equals(editPresentationName.trim()))
		{
			name = "Edit String Key";
		}
		else
		{
			name = editPresentationName;
		}
		cdo = cdomObject;
		objectKey = key;
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

	/**
	 * Performs the 'redo' action for this UndoableEdit.
	 * 
	 * @see javax.swing.undo.AbstractUndoableEdit#redo()
	 */
	@Override
	public void redo()
	{
		super.redo();
		if (newValue == null)
		{
			cdo.remove(objectKey);
		}
		else
		{
			cdo.put(objectKey, newValue);
		}
	}

	/**
	 * Performs the 'undo' action for this UndoableEdit.
	 * 
	 * @see javax.swing.undo.AbstractUndoableEdit#undo()
	 */
	@Override
	public void undo()
	{
		super.undo();
		if (oldValue == null)
		{
			cdo.remove(objectKey);
		}
		else
		{
			cdo.put(objectKey, oldValue);
		}
	}
}
