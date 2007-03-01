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
package pcgen.cdom.enumeration;

import java.util.Collection;

import pcgen.base.enumeration.AbstractSequencedConstantFactory;
import pcgen.base.enumeration.SequencedType;
import pcgen.base.enumeration.TypeSafeConstant;

public final class ArmorType implements SequencedType, TypeSafeConstant
{

	/**
	 * The name of this Constant
	 */
	private final String fieldName;

	/*
	 * FIXME TODO Why is this sequenced - what is the gap #s on Armor Type?
	 * (akin to the load weights?)
	 */
	private final int sequence;

	private static int ordinalCount = 0;

	private final transient int ordinal;

	private ArmorType(String name, int i)
	{
		ordinal = ordinalCount++;
		sequence = i;
		fieldName = name;
	}

	/**
	 * Converts this Constant to a String (returns the name of this Constant)
	 * 
	 * @return The string representatin (name) of this Constant
	 */
	@Override
	public String toString()
	{
		return fieldName;
	}

	public int getOrdinal()
	{
		return ordinal;
	}

	public int getSequence()
	{
		return sequence;
	}

	public static void clearConstants()
	{
		FACTORY.clearConstants();
	}

	public static Collection<ArmorType> getAllConstants()
	{
		return FACTORY.getAllConstants();
	}

	public static ArmorType getConstant(String s, int i)
	{
		return FACTORY.constructConstant(s, i);
	}

	public static ArmorType valueOf(String s)
	{
		return FACTORY.valueOf(s);
	}

	private static final TypeFactory FACTORY = new TypeFactory();

	public static class TypeFactory extends
			AbstractSequencedConstantFactory<ArmorType>
	{

		@Override
		protected Class<ArmorType> getConstantClass()
		{
			return ArmorType.class;
		}

		@Override
		protected ArmorType getConstantInstance(String name, int i)
		{
			return new ArmorType(name, i);
		}
	}
}
