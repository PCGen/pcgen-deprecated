/*
 * Copyright (c) Thomas Parker, 2006-2007.
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
 * 
 * Created on Nov 10, 2006
 */
package pcgen.base.util;

import java.lang.reflect.Modifier;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import pcgen.base.enumeration.TypeSafeConstant;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * This is an implementation of the java.util.Map Interface which is designed to
 * be used with a TypeSafeConstant as the key to the Map. This is optimized for
 * speed, and may sacrifice memory in the case where the Map is sparse relative
 * to the total number of keys in the TypeSafeConstant class. TypeSafeMap
 * provides O(1) speed on a get.
 * 
 * **WARNING** This TypeSafeMap assumes that the TypeSafeConstant class used as
 * keys in this Graph all have ordinals which are greater than or equal to zero.
 * Also, the internal structure of this TypeSafeMap will become unreasonable if
 * the TypeSafeConstant does not have ordinals which begin at zero and are
 * incremented in steps of one. It is recommended that this TypeSafeMap not be
 * used with a TypeSafeConstant that does not meet these criteria
 * 
 * Note: When using an identity based Class which cannot be extended (such as
 * Class itself) as a key, one should use an IdentityHashMap rather than trying
 * to maintain a Class to TypeSafe entry and using TypeSafeMap. There is not
 * O(1) guaranteed behavior, but IdentityHashMap should outperform (in most
 * cases) requiring a lookup of a TypeSafe Entry and then extracting the
 * identity based Class from the TypeSafe Entry.
 */
public class TypeSafeMap<K extends TypeSafeConstant, V> implements Map<K, V>
{

	/**
	 * The Type Safe Constant class used as a key in this TypeSafeMap
	 */
	private final Class<K> keyClass;

	/**
	 * Storage of the value objects in this Map
	 */
	private Object[] array;

	/**
	 * Object to represent the value of null (must be distinct from a key not
	 * being present in the Map)
	 */
	private final Object nullValue = new Object();

	/*
	 * CONSIDER Should there be some way to initialize the size here? Can there
	 * be a method of looking up how many constants there are? What about
	 * initializing the constants as well? Could this be done by reflection if
	 * certain methods are present as static methods?
	 * 
	 * Or do we force the passing of AbstractConstantFactory rather than the
	 * Class of the Constant??
	 */
	/**
	 * Constructs a new TypeSafeMap using objects of the given Class as keys in
	 * the TypeSafeMap. The given class must be final and must implement the
	 * TypeSafeConstant interface.
	 * 
	 * @param cl
	 *            The Class to be used as the key in this TypeSafeMap
	 */
	public TypeSafeMap(Class<K> cl)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException(
				"Class for TypeSafeMap must not be null");
		}
		if ((cl.getModifiers() & Modifier.FINAL) == 0)
		{
			throw new IllegalArgumentException(
				"Class for TypeSafeMap must be a final class");
		}
		if (!TypeSafeConstant.class.isAssignableFrom(cl))
		{
			throw new IllegalArgumentException(
				"Class for TypeSafeMap must implement TypeSafeConstant");
		}
		keyClass = cl;
		array = new Object[0];
	}

	/**
	 * Constructs a new TypeSafeMap using the contents of the given TypeSafeMap
	 * to initilize the TypeSafeMap. The Class used as keys in the TypeSafeMap
	 * will match the class in the given TypeSafeMap.
	 * 
	 * @param value
	 *            The TypeSafeMap to be used as to initialize this TypeSafeMap
	 */
	public TypeSafeMap(TypeSafeMap<K, V> value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("TypeSafeMap must not be null");
		}
		keyClass = value.keyClass;
		System.arraycopy(value.array, 0, array, 0, value.array.length);
	}

	/**
	 * Clears the TypeSafeMap
	 */
	public void clear()
	{
		Arrays.fill(array, null);
	}

	/**
	 * Returns true if this TypeSafeMap contains the given Key.
	 * 
	 * @return true if this TypeSafeMap contains the given Key; false otherwise
	 */
	public boolean containsKey(Object arg0)
	{
		if (arg0 instanceof TypeSafeConstant)
		{
			TypeSafeConstant tsc = (TypeSafeConstant) arg0;
			int loc = tsc.getOrdinal() * 2;
			/*
			 * Since this expands ONLY on a put, we need to be prepared to have
			 * an "out of bounds" ordinal.
			 */
			if (loc < array.length && tsc.equals(array[loc])
				&& array[loc + 1] != null)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if this TypeSafeMap contains the given Value for any Key.
	 * 
	 * @return true if this TypeSafeMap contains the given Value; false
	 *         otherwise
	 */
	public boolean containsValue(Object arg0)
	{
		Object compareValue = arg0 == null ? nullValue : arg0;
		for (int i = 1; i < array.length; i += 2)
		{
			Object value = array[i];
			if (value != null && value.equals(compareValue))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a Set of the Entries within this TypeSafeMap.
	 * 
	 * @see java.util.Map#entrySet()
	 */
	public Set<Entry<K, V>> entrySet()
	{
		return new EntrySet<K, V>();
	}

	/**
	 * Returns the Value in this TypeSafeMap for the given Key. Returns null if
	 * the given Key is not present in this TypeSafeMap.
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public V get(Object arg0)
	{
		// Ugh, why is get not "Generic" :(
		if (arg0 instanceof TypeSafeConstant)
		{
			TypeSafeConstant tsc = (TypeSafeConstant) arg0;
			int loc = tsc.getOrdinal() * 2;
			/*
			 * Since this expands ONLY on a put, we need to be prepared to have
			 * an "out of bounds" ordinal.
			 */
			if (loc < array.length && tsc.equals(array[loc]))
			{
				Object obj = array[loc + 1];
				return (V) (obj == nullValue ? null : obj);
			}
		}
		return null;
	}

	/**
	 * Returns true if this Map is empty
	 * 
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty()
	{
		for (int i = 1; i < array.length; i += 2)
		{
			/*
			 * Note this correctly handles a map to null, as nullValue is used
			 * when there is a valid map to null.
			 */
			if (array[i] != null)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the Set of Keys contained in this TypeSafeMap. This Set is backed
	 * by the Map, so changes in the Map are reflected in the Set (and changes
	 * to the Set impact the Map). This set supports removal (which will remove
	 * the key/value pair from the underlying Map). This Set does not support
	 * add or addAll operations.
	 * 
	 * @see java.util.Map#keySet()
	 */
	public Set<K> keySet()
	{
		return new KeySet<K>();
	}

	@SuppressWarnings("unchecked")
	public V put(K arg0, V arg1)
	{
		int loc = arg0.getOrdinal() * 2;
		/*
		 * Since this expands on a put, we need to be prepared to have an "out
		 * of bounds" ordinal... and expand the array if necessary
		 */
		if (loc >= array.length && keyClass.isInstance(arg0))
		{
			// Expand the Arrays
			Object[] newKeyArray = new Object[loc + 2];
			System.arraycopy(array, 0, newKeyArray, 0, array.length);
			array = newKeyArray;
		}
		else if (!keyClass.isInstance(arg0))
		{
			throw new IllegalArgumentException(arg0 + " is not a " + keyClass);
		}
		Object returnValue = array[loc + 1];
		array[loc] = arg0;
		array[loc + 1] = arg1 == null ? nullValue : arg1;
		return (V) returnValue;
	}

	public void putAll(Map<? extends K, ? extends V> map)
	{
		/*
		 * CONSIDER In the future a map of TypeSafeMap may be able to be
		 * accelerated here, but there is a huge challenge from the lazy
		 * extension of the arrays that is occurring today. :/
		 */
		for (Entry<? extends K, ? extends V> me : map.entrySet())
		{
			put(me.getKey(), me.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	public V remove(Object arg0)
	{
		// Ugh, why is remove not "Generic" :(
		if (arg0 instanceof TypeSafeConstant)
		{
			TypeSafeConstant tsc = (TypeSafeConstant) arg0;
			int loc = tsc.getOrdinal() * 2;
			if (loc < array.length && arg0.equals(array[loc]))
			{
				Object returnValue = array[loc + 1];
				array[loc + 1] = null;
				return (V) (returnValue == nullValue ? null : returnValue);
			}
		}
		return null;
	}

	public int size()
	{
		int size = 0;
		for (int i = 1; i < array.length; i += 2)
		{
			/*
			 * Note this correctly interprets for null values in the map, as
			 * those have nullValue as a placeholder
			 */
			if (array[i] != null)
			{
				size++;
			}
		}
		return size;
	}

	public Collection<V> values()
	{
		return new ValueCollection<V>();
	}

	private class KeySet<KT> extends AbstractSet<KT>
	{

		public KeySet()
		{
			super();
		}

		@Override
		public void clear()
		{
			TypeSafeMap.this.clear();
		}

		@Override
		public int size()
		{
			return TypeSafeMap.this.size();
		}

		@Override
		public boolean contains(Object arg0)
		{
			return TypeSafeMap.this.containsKey(arg0);
		}

		@Override
		public Iterator<KT> iterator()
		{
			return new KeyIterator<KT>();
		}
	}

	private abstract class AbstractTSIterator<KT> implements Iterator<KT>
	{

		protected int index = 0;

		protected int lastIndex = -1;

		public boolean hasNext()
		{
			while (index < array.length)
			{
				if (array[index + 1] != null)
				{
					return true;
				}
				index += 2;
			}
			return false;
		}

		public void remove()
		{
			if (lastIndex < 0)
			{
				throw new IllegalStateException();
			}
			array[lastIndex + 1] = null;
		}
	}

	private class KeyIterator<KT> extends AbstractTSIterator<KT>
	{

		@SuppressWarnings("unchecked")
		public KT next()
		{
			/*
			 * Must do this while loop, just in case someone didn't test with
			 * hasNext(). It's dumb, but possible...
			 */
			while (index < array.length)
			{
				if (array[index + 1] != null)
				{
					lastIndex = index;
					index += 2;
					return (KT) array[lastIndex];
				}
				index += 2;
			}
			throw new NoSuchElementException();
		}
	}

	private class ValueCollection<VT> extends AbstractCollection<VT>
	{

		public ValueCollection()
		{
			super();
		}

		@Override
		public void clear()
		{
			TypeSafeMap.this.clear();
		}

		@Override
		public int size()
		{
			return TypeSafeMap.this.size();
		}

		@Override
		public boolean contains(Object arg0)
		{
			return TypeSafeMap.this.containsValue(arg0);
		}

		@Override
		public Iterator<VT> iterator()
		{
			return new ValueIterator<VT>();
		}
	}

	private class ValueIterator<VT> extends AbstractTSIterator<VT>
	{

		@SuppressWarnings("unchecked")
		public VT next()
		{
			/*
			 * Must do this while loop, just in case someone didn't test with
			 * hasNext(). It's dumb, but possible...
			 */
			while (index < array.length)
			{
				Object obj = array[index + 1];
				if (obj != null)
				{
					lastIndex = index;
					index += 2;
					return (VT) (obj.equals(nullValue) ? null : obj);
				}
				index += 2;
			}
			throw new NoSuchElementException();
		}
	}

	private class EntrySet<KT, VT> extends AbstractSet<Entry<KT, VT>>
	{

		public EntrySet()
		{
			super();
		}

		@Override
		public void clear()
		{
			TypeSafeMap.this.clear();
		}

		@Override
		public int size()
		{
			return TypeSafeMap.this.size();
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean contains(Object arg0)
		{
			if (!(arg0 instanceof Map.Entry))
			{
				return false;
			}
			Map.Entry me = (Entry) arg0;
			Object key = me.getKey();
			if (!(key instanceof TypeSafeConstant))
			{
				return false;
			}
			int index = ((TypeSafeConstant) key).getOrdinal() * 2;
			Object mapKey = array[index];
			if (mapKey == null || !mapKey.equals(key))
			{
				return false;
			}
			Object value = array[index + 1];
			if (value == null)
			{
				return false;
			}
			return (me.getValue() == null && value.equals(nullValue))
				|| value.equals(me.getValue());
		}

		@Override
		public Iterator<Entry<KT, VT>> iterator()
		{
			return new EntryIterator<KT, VT>();
		}
	}

	private class EntryIterator<KT, VT> extends
			AbstractTSIterator<Entry<KT, VT>>
	{

		public java.util.Map.Entry<KT, VT> next()
		{
			/*
			 * Must do this while loop, just in case someone didn't test with
			 * hasNext(). It's dumb, but possible...
			 */
			while (index < array.length)
			{
				Object obj = array[index + 1];
				if (obj != null)
				{
					lastIndex = index;
					index += 2;
					return new TypeSafeEntry<KT, VT>(lastIndex);
				}
				index += 2;
			}
			throw new NoSuchElementException();
		}
	}

	private class TypeSafeEntry<KT, VT> implements Entry<KT, VT>
	{

		private final int index;

		public TypeSafeEntry(int idx)
		{
			index = idx;
		}

		@SuppressWarnings("unchecked")
		public KT getKey()
		{
			return (KT) array[index];
		}

		@SuppressWarnings("unchecked")
		public VT getValue()
		{
			Object obj = array[index + 1];
			return (VT) (obj.equals(nullValue) ? null : obj);
		}

		@SuppressWarnings("unchecked")
		public VT setValue(VT value)
		{
			Object obj = array[index + 1];
			array[index + 1] = value;
			return (VT) (obj.equals(nullValue) ? null : obj);
		}
	}
}
