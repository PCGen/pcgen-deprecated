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
import pcgen.cdom.enumeration.StringKey;

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
public class StringKeyEdit extends AbstractUndoableEdit implements UndoableEdit
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
	 * The StringKey used to modify the object in this Command
	 */
	private final StringKey stringKey;

	/**
	 * The old String value for the Edit
	 */
	private final String oldValue;

	/**
	 * The new String value for the Edit
	 */
	private final String newValue;

	/**
	 * Represents an Edit of a StringKey on a CDOMObject.
	 * 
	 * The CDOMObject is passed by Reference into StringKeyEdit, and
	 * StringKeyEdit therefore maintains a reference to this object.
	 * StringKeyEdit then agrees ("by contract") that it will only (a) call
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
	public StringKeyEdit(String editPresentationName, CDOMObject cdomObject,
		StringKey key, String oldVal, String newVal)
	{
		if (cdomObject == null)
		{
			throw new IllegalArgumentException("CDOMObject cannot be null");
		}
		if (key == null)
		{
			throw new IllegalArgumentException("StringKey cannot be null");
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
		stringKey = key;
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
			cdo.remove(stringKey);
		}
		else
		{
			cdo.put(stringKey, newValue);
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
			cdo.remove(stringKey);
		}
		else
		{
			cdo.put(stringKey, oldValue);
		}
	}
}
