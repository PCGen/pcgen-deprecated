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
package pcgen.cdom.mode;

import java.util.Collection;
import java.util.List;

import pcgen.base.enumeration.AbstractSequencedConstantFactory;
import pcgen.base.enumeration.SequencedType;
import pcgen.base.enumeration.TypeSafeConstant;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.PrereqObject;
import pcgen.core.prereq.Prerequisite;

public final class Size implements SequencedType, TypeSafeConstant,
		PrereqObject
{

	/**
	 * The name of this Constant
	 */
	private final String fieldName;

	/*
	 * FIXME TODO Why is this sequenced - what is the gap #s on Size? (akin to
	 * the load weights?)
	 */
	private final int sequence;

	private static int ordinalCount = 0;

	private final transient int ordinal;

	private Size(String name, int i)
	{
		ordinal = ordinalCount++;
		sequence = i;
		fieldName = name;
	}

	public int getOrdinal()
	{
		return ordinal;
	}

	public int getSequence()
	{
		return sequence;
	}

	public Size getNextSize()
	{
		// TODO What if null (if this is last?)
		return FACTORY.getNextConstant(this);
	}

	@Override
	public String toString()
	{
		return fieldName;
	}

	public static void clearConstants()
	{
		FACTORY.clearConstants();
	}

	public static Collection<Size> getAllConstants()
	{
		return FACTORY.getAllConstants();
	}

	public static Size constructConstant(String s, int i)
	{
		return FACTORY.constructConstant(s, i);
	}

	public static Size valueOf(String s)
	{
		return FACTORY.valueOf(s);
	}

	private static final TypeFactory FACTORY = new TypeFactory();

	public static class TypeFactory extends
			AbstractSequencedConstantFactory<Size>
	{

		@Override
		protected Class<Size> getConstantClass()
		{
			return Size.class;
		}

		@Override
		protected Size getConstantInstance(String name, int i)
		{
			return new Size(name, i);
		}

	}

	private PrereqObject pro = new ConcretePrereqObject();

	public void addAllPrerequisites(Prerequisite... prereqs)
	{
		pro.addAllPrerequisites(prereqs);
	}

	public void addPrerequisite(Prerequisite preReq)
	{
		pro.addPrerequisite(preReq);
	}

	public void clearPrerequisiteList()
	{
		pro.clearPrerequisiteList();
	}

	public int getPrerequisiteCount()
	{
		return pro.getPrerequisiteCount();
	}

	public List<Prerequisite> getPrerequisiteList()
	{
		return pro.getPrerequisiteList();
	}

	public boolean hasPrerequisiteOfType(String matchType)
	{
		return pro.hasPrerequisiteOfType(matchType);
	}

	public boolean hasPrerequisites()
	{
		return pro.hasPrerequisites();
	}

	public void setPrerequisiteListFrom(PrereqObject prereqObject)
	{
		pro.setPrerequisiteListFrom(prereqObject);
	}

}
