/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.util.ListKeyMapToList;
import pcgen.cdom.util.MapKeyMapToList;
import pcgen.core.SourceEntry;

public class CDOMObject extends ConcretePrereqObject implements LSTWriteable
{

	private final SourceEntry source = new SourceEntry();

	/*
	 * CONSIDER This should be a NumberMap - not Integer, but allow Double as
	 * well, in one HashMap... this will control the size of CDOMObject.
	 */
	/** A map to hold items keyed by Integers for the object */
	private final Map<IntegerKey, Integer> integerChar =
			new HashMap<IntegerKey, Integer>();

	/** A map to hold items keyed by Strings for the object */
	private final Map<StringKey, String> stringChar =
			new HashMap<StringKey, String>();

	/** A map to hold items keyed by Strings for the object */
	private final Map<FormulaKey, Formula> formulaChar =
			new HashMap<FormulaKey, Formula>();

	/** A map to hold items keyed by Strings for the object */
	private final Map<VariableKey, Formula> variableChar =
			new HashMap<VariableKey, Formula>();

	/** A map to hold items keyed by Strings for the object */
	private final Map<ObjectKey<?>, Object> objectChar =
			new HashMap<ObjectKey<?>, Object>();

	/** A map of Lists for the object */
	private final ListKeyMapToList listChar = new ListKeyMapToList();

	private final MapKeyMapToList mapChar = new MapKeyMapToList();

	private final DoubleKeyMap<CDOMReference<CDOMList<? extends CDOMObject>>, LSTWriteable, AssociatedPrereqObject> cdomListMods =
			new DoubleKeyMap<CDOMReference<CDOMList<? extends CDOMObject>>, LSTWriteable, AssociatedPrereqObject>();

	private Boolean namePI = null;

	private Boolean descPI = null;

	public SourceEntry getSourceEntry()
	{
		/*
		 * TODO This exposes the internal structure of CDOMObject - this needs
		 * to be restructured to delegate to SourceEntry's methods...
		 */
		return source;
	}

	public final boolean containsKey(IntegerKey arg0)
	{
		return integerChar.containsKey(arg0);
	}

	public final Integer get(IntegerKey arg0)
	{
		return integerChar.get(arg0);
	}

	public final Integer put(IntegerKey arg0, Integer arg1)
	{
		return integerChar.put(arg0, arg1);
	}

	public final Integer remove(IntegerKey arg0)
	{
		return integerChar.remove(arg0);
	}

	public final boolean containsKey(StringKey arg0)
	{
		return stringChar.containsKey(arg0);
	}

	public final String get(StringKey arg0)
	{
		return stringChar.get(arg0);
	}

	public final String put(StringKey arg0, String arg1)
	{
		return stringChar.put(arg0, arg1);
	}

	public final String remove(StringKey arg0)
	{
		return stringChar.remove(arg0);
	}

	public final boolean containsKey(FormulaKey arg0)
	{
		return formulaChar.containsKey(arg0);
	}

	public final Formula get(FormulaKey arg0)
	{
		return formulaChar.get(arg0);
	}

	public final Formula put(FormulaKey arg0, Formula arg1)
	{
		return formulaChar.put(arg0, arg1);
	}

	public final Formula remove(FormulaKey arg0)
	{
		return formulaChar.remove(arg0);
	}

	public final boolean containsKey(VariableKey arg0)
	{
		return variableChar.containsKey(arg0);
	}

	public final Formula get(VariableKey arg0)
	{
		return variableChar.get(arg0);
	}

	public final Set<VariableKey> getVariableKeys()
	{
		return new HashSet<VariableKey>(variableChar.keySet());
	}

	public final Formula put(VariableKey arg0, Formula arg1)
	{
		return variableChar.put(arg0, arg1);
	}

	public final Formula remove(VariableKey arg0)
	{
		return variableChar.remove(arg0);
	}

	public final boolean containsKey(ObjectKey<?> arg0)
	{
		return objectChar.containsKey(arg0);
	}

	public final <OT> OT get(ObjectKey<OT> arg0)
	{
		return arg0.cast(objectChar.get(arg0));
	}

	public final <OT> OT put(ObjectKey<OT> arg0, OT arg1)
	{
		return arg0.cast(objectChar.put(arg0, arg1));
	}

	public final <OT> OT remove(ObjectKey<OT> arg0)
	{
		return arg0.cast(objectChar.remove(arg0));
	}

	public final boolean containsListFor(ListKey<?> key)
	{
		return listChar.containsListFor(key);
	}

	public final <T> void addToListFor(ListKey<T> key, T value)
	{
		listChar.addToListFor(key, value);
	}

	public final <T> List<T> getListFor(ListKey<T> key)
	{
		return listChar.getListFor(key);
	}

	public final <T> List<T> getSafeListFor(ListKey<T> key)
	{
		return listChar.containsListFor(key) ? listChar.getListFor(key)
			: new ArrayList<T>();
	}

	public final int getSizeOfListFor(ListKey<?> key)
	{
		return listChar.sizeOfListFor(key);
	}

	public final int getSafeSizeOfListFor(ListKey<?> key)
	{
		return listChar.containsListFor(key) ? listChar.sizeOfListFor(key) : 0;
	}

	public final <T> boolean containsInList(ListKey<T> key, T value)
	{
		return listChar.containsInList(key, value);
	}

	public final <T> T getElementInList(ListKey<T> key, int i)
	{
		return listChar.getElementInList(key, i);
	}

	public final <T> List<T> removeListFor(ListKey<T> key)
	{
		return listChar.removeListFor(key);
	}

	public final <T> boolean removeFromListFor(ListKey<T> key, T obj)
	{
		return listChar.removeFromListFor(key, obj);
	}

	/*
	 * TODO would be REALLY nice to remove these MapKey items as unnecessary
	 */
	public final <SK, SV> boolean addToListFor(MapKey<SK, SV> key1, SK key2,
		SV value)
	{
		return mapChar.addToListFor(key1, key2, value);
	}

	public final boolean containsKey(MapKey<?, ?> key1)
	{
		return mapChar.containsKey(key1);
	}

	public final <SK> boolean containsKey(MapKey<SK, ?> key1, SK key2)
	{
		return mapChar.containsKey(key1, key2);
	}

	public final <SK, SV> List<SV> getListFor(MapKey<SK, SV> key1, SK key2)
	{
		return mapChar.getListFor(key1, key2);
	}

	public final <SK, SV> List<SV> removeListFor(MapKey<SK, SV> key1, SK key2)
	{
		return mapChar.removeListFor(key1, key2);
	}

	public final <SK> Set<SK> getKeySet(MapKey<SK, ?> key1)
	{
		return mapChar.getKeySet(key1);
	}

	public final Boolean getDescPIObject()
	{
		return descPI;
	}

	public final boolean isDescPI()
	{
		return descPI == null ? false : descPI.booleanValue();
	}

	public final void setDescPI(Boolean descIsPI)
	{
		this.descPI = descIsPI;
	}

	public final Boolean getNamePIObject()
	{
		return namePI;
	}

	public final boolean isNamePI()
	{
		return namePI == null ? false : namePI.booleanValue();
	}

	public final void setNamePI(Boolean nameIsPI)
	{
		this.namePI = nameIsPI;
	}

	public String getKey()
	{
		// TODO This is going to have to be special cased, because the key could
		// be the key or the name :/
		return null;
	}

	public String getKeyName()
	{
		// FIXME TODO Patched for now to avoid NPEs, but this is wrong
		// TODO Auto-generated method stub
		return this.get(StringKey.NAME);
	}

	public String getDisplayName()
	{
		return this.get(StringKey.NAME);
		// TODO Auto-generated method stub
	}

	public void setName(String name)
	{
		this.put(StringKey.NAME, name);
	}

	public boolean isCDOMEqual(CDOMObject cdo)
	{
		if (cdo == this)
		{
			return true;
		}
		if (namePI != cdo.namePI || descPI != cdo.descPI)
		{
			System.err.println("CDOM Inequality PI");
			return false;
		}
		/*
		 * FIXME Test source here
		 * 
		 * private final SourceEntry source = new SourceEntry();
		 */
		if (!integerChar.equals(cdo.integerChar))
		{
			System.err.println("CDOM Inequality Integer");
			System.err.println(integerChar + " " + cdo.integerChar);
			return false;
		}
		if (!stringChar.equals(cdo.stringChar))
		{
			System.err.println("CDOM Inequality String");
			System.err.println(stringChar + " " + cdo.stringChar);
			return false;
		}
		if (!formulaChar.equals(cdo.formulaChar))
		{
			System.err.println("CDOM Inequality Formula");
			System.err.println(formulaChar + " " + cdo.formulaChar);
			return false;
		}
		if (!variableChar.equals(cdo.variableChar))
		{
			System.err.println("CDOM Inequality Variable");
			System.err.println(variableChar + " " + cdo.variableChar);
			return false;
		}
		if (!objectChar.equals(cdo.objectChar))
		{
			System.err.println("CDOM Inequality Object");
			System.err.println(objectChar + " " + cdo.objectChar);
			return false;
		}
		/*
		 * FIXME Test these items
		 * 
		 * private final ListKeyMapToList listChar = new ListKeyMapToList();
		 * 
		 * private final MapKeyMapToList mapChar = new MapKeyMapToList();
		 */
		return true;
	}

	public CDOMObject getRetiredReference()
	{
		Boolean retired = get(ObjectKey.RETIRED);
		if (retired != null && retired.booleanValue() == true)
		{
			throw new IllegalStateException(
				"Cannot get retired reference for an object that is already retired");
		}
		CDOMObject obj = getRawReplacement();
		obj.integerChar.putAll(integerChar);
		obj.stringChar.putAll(stringChar);
		obj.formulaChar.putAll(formulaChar);
		obj.variableChar.putAll(variableChar);
		obj.objectChar.putAll(objectChar);
		obj.listChar.addAllLists(listChar);
		obj.mapChar.addAll(mapChar);
		obj.addAllPrerequisites(getPrerequisiteList());
		obj.objectChar.put(ObjectKey.RETIRED, Boolean.TRUE);
		Integer seq = integerChar.get(IntegerKey.SEQUENCE_NUMBER);
		if (seq == null)
		{
			seq = Integer.valueOf(1);
			obj.integerChar.put(IntegerKey.SEQUENCE_NUMBER, seq);
		}
		seq = Integer.valueOf(seq.intValue() + 1);
		integerChar.put(IntegerKey.SEQUENCE_NUMBER, seq);
		return obj;
	}

	protected CDOMObject getRawReplacement()
	{
		CDOMObject obj;
		try
		{
			obj = this.getClass().newInstance();
		}
		catch (InstantiationException e)
		{
			throw new InternalError(e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			throw new InternalError(e.getMessage());
		}
		return obj;
	}

	// TODO Generic Type Safety rather than CDOMObject
	public <T extends CDOMObject> AssociatedPrereqObject putToList(
		CDOMReference list, CDOMReference<T> granted,
		AssociatedPrereqObject associations)
	{
		return cdomListMods.put(list, granted, associations);
	}

	public boolean hasListMods(CDOMReference list)
	{
		return cdomListMods.containsKey(list);
	}

	public Collection<LSTWriteable> getListMods(CDOMReference list)
	{
		Set<LSTWriteable> set = cdomListMods.getSecondaryKeySet(list);
		if (set == null || set.isEmpty())
		{
			return null;
		}
		return set;
	}

	public AssociatedPrereqObject getListAssociation(CDOMReference list,
		LSTWriteable key)
	{
		return cdomListMods.get(list, key);
	}

	public Collection<CDOMReference<CDOMList<? extends CDOMObject>>> getModifiedLists()
	{
		return cdomListMods.getKeySet();
	}

	public String getLSTformat()
	{
		return getKeyName();
	}
}
