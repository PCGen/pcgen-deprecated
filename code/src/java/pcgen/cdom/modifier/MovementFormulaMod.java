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

import java.math.BigDecimal;

import pcgen.base.formula.ReferenceFormula;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.content.Modifier;
import pcgen.cdom.content.SimpleMovement;

public class MovementFormulaMod extends ConcretePrereqObject implements
		Modifier<SimpleMovement>
{

	private final ReferenceFormula<Integer> f;

	private final String type;

	public MovementFormulaMod(String moveType, ReferenceFormula<Integer> formula)
	{
		super();
		type = moveType;
		f = formula;
	}

	public SimpleMovement applyModifier(SimpleMovement move)
	{
		if (type.equals(move.getMovementType()))
		{
			Integer resolve = f.resolve(move.getMovement());
			return new SimpleMovement(type, new BigDecimal(resolve.intValue()));
		}
		return move;
	}

	public Class<SimpleMovement> getModifiedClass()
	{
		return SimpleMovement.class;
	}

}
