/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.modifier;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.content.Modifier;
import pcgen.cdom.content.SimpleMovement;

public class MovementLock extends ConcretePrereqObject implements
		Modifier<SimpleMovement>
{

	private final SimpleMovement move;

	public MovementLock(SimpleMovement movement)
	{
		move = movement;
	}

	public SimpleMovement applyModifier(SimpleMovement obj)
	{
		return move.getMovementType().equals(obj.getMovementType()) ? move
			: obj;
	}

	public Class<SimpleMovement> getModifiedClass()
	{
		return SimpleMovement.class;
	}

}
