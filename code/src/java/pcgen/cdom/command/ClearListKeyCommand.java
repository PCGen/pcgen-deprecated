/*
 * Copyright (c) Thomas Parker, 2007.
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
 */
package pcgen.cdom.command;

import java.net.URI;

import javax.swing.undo.UndoableEdit;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * This is a Command which can edit a ListKey on a CDOMObject. In order to
 * produce the ListKeyEdit, the execute() method of this command should be
 * invoked.
 */
public class ClearListKeyCommand<T> implements CDOMCommand
{

	/**
	 * The presentation name of this Command
	 */
	private final String name;

	/**
	 * The CDOMObject to be modified, resulting in the Edit
	 */
	private final CDOMObject cdo;

	/**
	 * The ListKey used to modify the object in this Command
	 */
	private final ListKey<T> listKey;

	private URI sourceURI;

	/**
	 * Creates a new Command which can edit a ListKey on a CDOMObject.
	 * 
	 * The CDOMObject is passed by Reference into SetListKeyCommand, and
	 * SetListKeyCommand therefore maintains a reference to this object.
	 * SetListKeyCommand then agrees ("by contract") that it will only (a) call
	 * appropriate methods in CDOMObject interface and (b) return a ListKeyEdit
	 * edit which also references the CDOMObject.
	 * 
	 * @param editPresentationName
	 *            The presentation name for the Edit (designed to be shown to
	 *            the user of an application)
	 * @param cdomObject
	 *            The CDOMObject to be changed by this Command
	 * @param key
	 *            The key used by this Command to know which String to change in
	 *            the CDOMObject
	 * @param value
	 *            The value of the String to be set for the given ListKey in the
	 *            CDOMObject.
	 */
	public ClearListKeyCommand(String editPresentationName,
		CDOMObject cdomObject, ListKey<T> key)
	{
		if (editPresentationName == null
			|| "".equals(editPresentationName.trim()))
		{
			throw new IllegalArgumentException(
				"Presentation Name cannot be null or empty");
		}
		if (cdomObject == null)
		{
			throw new IllegalArgumentException("CDOMObject cannot be null");
		}
		if (key == null)
		{
			throw new IllegalArgumentException("ListKey cannot be null");
		}
		name = editPresentationName;
		cdo = cdomObject;
		listKey = key;
	}

	/**
	 * Performs the Edit defined by this Command and returns an UndoableEdit
	 * which can be used to undo this Command.
	 * 
	 * @return An UndoableEdit whcih can be used to undo the effects of this
	 *         Command.
	 */
	public UndoableEdit execute()
	{
		return null;
	}

	/**
	 * Returns the presentation name of this Command. The presentation name (a
	 * String designed to be interpreted by the user of the application) of the
	 * Command.
	 * 
	 * @return The presentation name of the Command
	 * 
	 * @see rpgmapgen.edit.Command#getPresentationName()
	 */
	public String getPresentationName()
	{
		return name;
	}

	public URI getSourceURI()
	{
		return sourceURI;
	}

	public void setSourceURI(URI uri)
	{
		sourceURI = uri;
	}
}
