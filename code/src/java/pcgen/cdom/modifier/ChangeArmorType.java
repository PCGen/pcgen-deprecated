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
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.Modifier;
import pcgen.cdom.enumeration.Type;

public class ChangeArmorType extends ConcretePrereqObject implements
		Modifier<Type>, LSTWriteable
{

	private final Type source;
	private final Type result;

	public ChangeArmorType(Type sourceType, Type resultType)
	{
		if (sourceType == null)
		{
			throw new IllegalArgumentException(
				"Source Type for ChangeArmorType cannot be null");
		}
		if (resultType == null)
		{
			throw new IllegalArgumentException(
				"Resulting Type for ChangeArmorType cannot be null");
		}
		result = resultType;
		source = sourceType;
	}

	public Type applyModifier(Type obj)
	{
		return source.equals(obj) ? result : obj;
	}

	public Class<Type> getModifiedClass()
	{
		return Type.class;
	}

	public Type getSourceType()
	{
		return source;
	}

	public Type getResultType()
	{
		return result;
	}

	@Override
	public int hashCode()
	{
		return 31 * source.hashCode() + result.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (!(o instanceof ChangeArmorType))
		{
			return false;
		}
		ChangeArmorType other = (ChangeArmorType) o;
		return source.equals(other.source) && result.equals(other.result);
	}

	public String getLSTformat()
	{
		//TODO Don't like this = fix Generics in GraphChanges
		return "";
	}
}
