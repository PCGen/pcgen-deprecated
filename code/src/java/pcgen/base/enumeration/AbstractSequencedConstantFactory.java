/*
 * Copyright (c) 2006 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.enumeration;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.base.lang.UnreachableError;

/**
 * @author Tom Parker <thpr@users.sourceforge.net>
 * 
 * An AbstractSequencedConstantFactory is a framework used to develop non-enum
 * (meaning expandable) Type Safe Constants that have additional sequencing
 * information beyond the ordinal of the Type Safe Constant. Typically, this
 * would be used to create a sequence of objects where the sequence numbers are
 * not sequential (but using this class makes the ordinals sequential)
 * 
 * @param <T>
 *            The sequenced type (This should be the class that extends the
 *            Factory)
 */
public abstract class AbstractSequencedConstantFactory<T extends TypeSafeConstant & SequencedType>
		implements Serializable
{

	/**
	 * The Map of Sequenced Constants for this Factory
	 */
	private SortedMap<Integer, SCFValue<T>> typeMap;

	/**
	 * Creates a new AbstractSequencedConstantFactory - class must be extended
	 */
	protected AbstractSequencedConstantFactory()
	{
		super();
	}

	/**
	 * Constructs a new SequencedConstant with the given String as the Constant
	 * name and the given integer as the Sequence
	 * 
	 * @param s
	 *            The String to be used as the SequencedConstants name
	 * @param i
	 *            The sequence of the SequencedConstant
	 * @return The new SequencedConstant built with the given name and sequence
	 *         number
	 */
	public T constructConstant(String s, int i)
	{
		if (typeMap == null)
		{
			buildMap();
		}
		Integer in = Integer.valueOf(i);
		/*
		 * CONSIDER Now this is CASE INSENSITIVE. Should this really be the
		 * case? - thpr 10/28/06
		 */
		SCFValue<T> o = typeMap.get(in);
		T obj;
		if (o == null)
		{
			obj = getConstantInstance(s, i);
			typeMap.put(in, new SCFValue<T>(s, obj));
		}
		else
		{
			if (!o.string.equals(new CaseInsensitiveString(s)))
			{
				throw new IllegalArgumentException(
					"Attempt to redefine constant value " + i + " to " + s
						+ ", value was " + o.string);
			}
			obj = o.constant;
		}
		return obj;
	}

	/**
	 * Returns the appropriate Constant for the given (case insensitive) String
	 * value
	 * 
	 * @param s
	 *            The String for which the Constant should be returned.
	 * @return The Constant for the given String
	 */
	public T valueOf(String s)
	{
		if (typeMap != null)
		{
			CaseInsensitiveString cis = new CaseInsensitiveString(s);
			for (SCFValue<T> he : typeMap.values())
			{
				if (he.string.equals(cis))
				{
					return he.constant;
				}
			}
		}
		throw new IllegalArgumentException(s);
	}

	/**
	 * Returns the appropriate Constant for the given sequence number. Returns
	 * null if there is no constant for the given sequence number
	 * 
	 * @param i
	 *            The sequence number of the Constant to be returned.
	 * @return The Constant with the given sequence number, or null if no
	 *         constant has the given sequence number
	 */
	public T getConstant(int i)
	{
		if (typeMap != null)
		{
			SCFValue<T> o = typeMap.get(Integer.valueOf(i));
			if (o != null)
			{
				return o.constant;
			}
		}
		return null;
	}

	/**
	 * Returns the Constant with the largest sequence number that is less than
	 * or equal to the given sequence number. Returns null if there is no
	 * Constant with a sequence number less than the given value.
	 * 
	 * @param i
	 *            The upper bound sequence number
	 * @return The Constant with the largest sequence number that is less than
	 *         or equal to the given sequence number, or null if there is no
	 *         Constant with a sequence number less than the given value.
	 */
	public T getLessThanEqualConstant(int i)
	{
		if (typeMap != null)
		{
			/*
			 * +1 is required here because headMap is exclusive of the given
			 * key... note tailMap (below) is NOT...
			 */
			SortedMap<Integer, SCFValue<T>> subMap =
					typeMap.headMap(Integer.valueOf(i + 1));
			if (subMap != null)
			{
				SCFValue<T> o = subMap.get(subMap.lastKey());
				if (o != null)
				{
					return o.constant;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the Constant with the smallest sequence number that is greater
	 * than or equal to the given sequence number. Returns null if there is no
	 * Constant with a sequence number greater than the given value.
	 * 
	 * @param i
	 *            The lower bound sequence number
	 * @return The Constant with the smallest sequence number that is greater
	 *         than or equal to the given sequence number, or null if there is
	 *         no Constant with a sequence number greater than the given value.
	 */
	public T getGreaterThanEqualConstant(int i)
	{
		SortedMap<Integer, SCFValue<T>> subMap =
				typeMap.tailMap(Integer.valueOf(i));
		if (subMap != null)
		{
			SCFValue<T> o = subMap.get(subMap.firstKey());
			if (o != null)
			{
				return o.constant;
			}
		}
		return null;
	}

	/**
	 * Returns the Constant with the largest sequence number that is less than
	 * the sequence number of the given Constant. Returns null if there is no
	 * Constant with a sequence number smaller than the sequence number of the
	 * given Constant.
	 * 
	 * @param current
	 *            The Constant for which the previous Constant will be returned.
	 * @return The Constant with the largest sequence number that is less than
	 *         the sequence number of the given Constant, or null if there is no
	 *         Constant with a sequence number smaller than the sequence number
	 *         of the given Constant.
	 */
	public T getPreviousConstant(T current)
	{
		if (typeMap != null)
		{
			SortedMap<Integer, SCFValue<T>> subMap =
					typeMap.headMap(Integer.valueOf(current.getOrdinal()));
			if (subMap != null)
			{
				SCFValue<T> o = subMap.get(subMap.lastKey());
				if (o != null)
				{
					return o.constant;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the Constant with the smallest sequence number that is greater
	 * than the sequence number of the given Constant. Returns null if there is
	 * no Constant with a sequence number greater than the sequence number of
	 * the given Constant.
	 * 
	 * @param current
	 *            The Constant for which the next Constant will be returned.
	 * @return The Constant with the smallest sequence number that is greater
	 *         than the sequence number of the given Constant, or null if there
	 *         is no Constant with a sequence number greater than the sequence
	 *         number of the given Constant.
	 */
	public T getNextConstant(T current)
	{
		if (typeMap != null)
		{
			SortedMap<Integer, SCFValue<T>> subMap =
					typeMap.tailMap(Integer.valueOf(current.getOrdinal() + 1));
			if (subMap != null)
			{
				SCFValue<T> o = subMap.get(subMap.firstKey());
				if (o != null)
				{
					return o.constant;
				}
			}
		}
		return null;
	}

	/**
	 * Returns a new Constant of the given class.
	 * 
	 * The extending class is responsible for ensuring that the Constant is
	 * given a unique ordinal.
	 * 
	 * @param name
	 *            The String identifier for the Constant to be built
	 * @param i
	 *            The sequence number of the constant to be built.
	 * @return A new Constant, with an ordinal that is unique to the child
	 *         class.
	 */
	protected abstract T getConstantInstance(String name, int i);

	/**
	 * Must return the class that extends the AbstractSequencedConstantFactory.
	 * This is done in order to allow any constants declared as "public static
	 * final" constants within the class that extends the
	 * AbstractSequencedConstantFactory to be integrated into the Constant pool.
	 * 
	 * @return the class that extends the AbstractSequencedConstantFactory
	 */
	protected abstract Class<T> getConstantClass();

	/**
	 * Actually build the set of Constants, using any "public static final"
	 * constants within the child (extending) class as initial values in the
	 * Constant pool.
	 */
	private void buildMap()
	{
		typeMap = new TreeMap<Integer, SCFValue<T>>();
		Class<T> cl = getConstantClass();
		Field[] fields = cl.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			int mod = fields[i].getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
				&& Modifier.isPublic(mod))
			{
				try
				{
					Object o = fields[i].get(null);
					if (cl.equals(o.getClass()))
					{
						T tObj = getConstantClass().cast(o);
						Integer in = Integer.valueOf(tObj.getSequence());
						if (typeMap.containsKey(in))
						{
							throw new UnreachableError(
								"Attempt to redefine constant value " + i
									+ " to " + fields[i].getName()
									+ ", value was " + typeMap.get(in));
						}
						typeMap.put(in, new SCFValue<T>(fields[i].getName(),
							tObj));
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
	}

	/**
	 * Returns the String representation of the given Constant
	 * 
	 * @param obj
	 *            The constant for which the String representation should be
	 *            returned
	 * @return the String representation of the given Constant
	 */
	public String toString(T obj)
	{
		for (Map.Entry<Integer, SCFValue<T>> me : typeMap.entrySet())
		{
			if (me.getValue().constant.equals(obj))
			{
				return me.getKey().toString();
			}
		}
		throw new UnreachableError();
	}

	/**
	 * Clears all of the Constants defined by this class. Note that this does
	 * not remove any Constants declared in the Constant class (as those are
	 * considered 'permanent' members of the Sequenced Constant collection.
	 * 
	 * Note that this *will not* reset the ordinal count, because that is a
	 * dangerous operation. As there could be outstanding references to
	 * constants that would be removed from the Constant pool, no reuse of
	 * ordinals is driven by this method. As a result, calling this method may
	 * result in a Constant Pool which does not have sequentially numbered
	 * ordinal values.
	 */
	public void clearConstants()
	{
		buildMap();
	}

	/**
	 * Returns a Collection of all of the Constants for this class. The returned
	 * Collection is unmodifiable.
	 * 
	 * @return an unmodifiable Collection of all of the Constants for this class
	 */
	public Collection<T> getAllConstants()
	{
		List<T> l = new ArrayList<T>(typeMap.size());
		for (SCFValue<T> he : typeMap.values())
		{
			l.add(he.constant);
		}
		return Collections.unmodifiableList(l);
	}

	/**
	 * This is simply used as a holder of values in the Constant pool. This is
	 * required in order to store both the String value and the Constant itself,
	 * as the Constants are stored in the AbstractSequencedConstantFactory by
	 * their sequence number.
	 * 
	 * @param <HET>
	 *            The constant type
	 */
	protected static class SCFValue<HET extends SequencedType>
	{
		public final CaseInsensitiveString string;

		public final HET constant;

		public SCFValue(String s, HET obj)
		{
			string = new CaseInsensitiveString(s);
			constant = obj;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object o)
		{
			if (o instanceof SCFValue)
			{
				SCFValue en = (SCFValue) o;
				return string.equals(en.string) && constant.equals(en.constant);
			}
			return false;
		}

		@Override
		public int hashCode()
		{
			return string.hashCode() ^ constant.hashCode();
		}
	}
}