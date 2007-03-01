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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.enumeration.TypeSafeConstant;
import pcgen.core.spell.Spell;

public abstract class ProhibitedSpellType<T extends TypeSafeConstant>
{

	private static final Class<ProhibitedSpellType> PST_CLASS =
			ProhibitedSpellType.class;

	public static final ProhibitedSpellType<SpellDescriptor> ALIGNMENT =
			new ProhibitedSpellType<SpellDescriptor>("Alignment")
			{
				@Override
				public Collection<SpellDescriptor> getCheckList(Spell s)
				{
					return s.getListFor(ListKey.SPELL_DESCRIPTOR);
				}

				@Override
				public Class<SpellDescriptor> getReferencedClass()
				{
					return SpellDescriptor.class;
				}
			};

	public static final ProhibitedSpellType<SpellDescriptor> DESCRIPTOR =
			new ProhibitedSpellType<SpellDescriptor>("Descriptor")
			{
				@Override
				public Collection<SpellDescriptor> getCheckList(Spell s)
				{
					return s.getListFor(ListKey.SPELL_DESCRIPTOR);
				}

				@Override
				public Class<SpellDescriptor> getReferencedClass()
				{
					return SpellDescriptor.class;
				}
			};

	public static final ProhibitedSpellType<SpellSchool> SCHOOL =
			new ProhibitedSpellType<SpellSchool>("School")
			{
				@Override
				public Collection<SpellSchool> getCheckList(Spell s)
				{
					return s.getListFor(ListKey.SPELL_SCHOOL);
				}

				@Override
				public Class<SpellSchool> getReferencedClass()
				{
					return SpellSchool.class;
				}
			};

	public static final ProhibitedSpellType<SpellSubSchool> SUBSCHOOL =
			new ProhibitedSpellType<SpellSubSchool>("SubSchool")
			{
				@Override
				public Collection<SpellSubSchool> getCheckList(Spell s)
				{
					return s.getListFor(ListKey.SPELL_SUBSCHOOL);
				}

				@Override
				public Class<SpellSubSchool> getReferencedClass()
				{
					return SpellSubSchool.class;
				}
			};

	private final String text;

	private ProhibitedSpellType(String s)
	{
		text = s;
	}

	public abstract Collection<T> getCheckList(Spell s);

	public abstract Class<T> getReferencedClass();

	@Override
	public String toString()
	{
		return text;
	}

	public static ProhibitedSpellType getReference(String s)
	{
		for (ProhibitedSpellType type : values())
		{
			if (type.text.equalsIgnoreCase(s))
			{
				return type;
			}
		}
		throw new IllegalArgumentException(s);
	}

	public static List<ProhibitedSpellType> values()
	{
		// TODO Cache this list
		List<ProhibitedSpellType> list = new ArrayList<ProhibitedSpellType>();
		Field[] fields = PST_CLASS.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			Field field = fields[i];
			int mod = field.getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
				&& Modifier.isPublic(mod))
			{
				try
				{
					Object o = field.get(null);
					if (PST_CLASS.isAssignableFrom(o.getClass()))
					{
						list.add(PST_CLASS.cast(o));
					}
				}
				catch (IllegalArgumentException e)
				{
					throw new InternalError();
				}
				catch (IllegalAccessException e)
				{
					throw new InternalError();
				}
			}
		}
		return list;
	}
}
