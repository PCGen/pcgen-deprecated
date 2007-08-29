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
package pcgen.cdom.constructor;

import java.math.BigDecimal;

import pcgen.base.formula.ReferenceFormula;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.Constructor;
import pcgen.cdom.content.SimpleMovement;

public class MovementFormulaConstructor extends ConcretePrereqObject implements
		Constructor<SimpleMovement>, LSTWriteable
{

	private final ReferenceFormula<Integer> f;

	private final String baseType;
	private final String type;

	public MovementFormulaConstructor(String oldType, String newType,
		ReferenceFormula<Integer> formula)
	{
		super();
		baseType = oldType;
		type = newType;
		f = formula;
	}

	public SimpleMovement constructFrom(SimpleMovement move)
	{
		if (baseType.equals(move.getMovementType()))
		{
			Integer resolve = f.resolve(move.getMovement());
			return new SimpleMovement(type, new BigDecimal(resolve.intValue()));
		}
		return null;
	}

	public Class<SimpleMovement> getConstructedClass()
	{
		return SimpleMovement.class;
	}

	public String getLSTformat()
	{
		return f.toString();
	}

	public String getBaseType()
	{
		return baseType;
	}

	public String getNewType()
	{
		return type;
	}

	@Override
	public int hashCode()
	{
		return baseType.hashCode() + 23 * type.hashCode() + 31 * f.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof MovementFormulaConstructor)
		{
			MovementFormulaConstructor other = (MovementFormulaConstructor) o;
			return type.equals(other.type) && baseType.equals(other.baseType)
				&& f.equals(other.f);
		}
		return false;
	}

}
