/*
 * Copyright (c) Thomas Parker, 2005.
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
 */
package pcgen.base.util;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * This class represents an insignificant UndoableEdit which performs no action.
 * It is designed to be used as a placeholder for a Command which does not
 * perform an undo-able activity (such as a Command that displays an information
 * window).
 */
public class InsignificantUndoableEdit extends AbstractUndoableEdit
{
	/**
	 * Construct a new InsignificantUndoableEdit
	 */
	public InsignificantUndoableEdit()
	{
		super();
	}

	/**
	 * Return false (as the InsignificantUndoableEdit is never significant)
	 * 
	 * @return false
	 */
	@Override
	public final boolean isSignificant()
	{
		return false;
	}
}
