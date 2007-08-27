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
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.util.ListKeyMapToList;
import pcgen.core.PCTemplateChooseList;
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

	private final DoubleKeyMapToList<CDOMReference<CDOMList<? extends CDOMObject>>, CDOMReference<?>, AssociatedPrereqObject> cdomListMods =
			new DoubleKeyMapToList<CDOMReference<CDOMList<? extends CDOMObject>>, CDOMReference<?>, AssociatedPrereqObject>();

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

	public final Set<IntegerKey> getIntegerKeys()
	{
		return new HashSet<IntegerKey>(integerChar.keySet());
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

	public final Set<StringKey> getStringKeys()
	{
		return new HashSet<StringKey>(stringChar.keySet());
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

	public final Set<FormulaKey> getFormulaKeys()
	{
		return new HashSet<FormulaKey>(formulaChar.keySet());
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

	public final Set<ObjectKey<?>> getObjectKeys()
	{
		return new HashSet<ObjectKey<?>>(objectChar.keySet());
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
	
	public final Set<ListKey<?>> getListKeys()
	{
		return listChar.getKeySet();
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
		if (!listChar.equals(cdo.listChar))
		{
			System.err.println("CDOM Inequality List");
			System.err.println(listChar + " " + cdo.listChar);
			return false;
		}
		if (!cdomListMods.equals(cdo.cdomListMods))
		{
			System.err.println("CDOM Inequality ListMods");
			System.err.println(cdomListMods + " " + cdo.cdomListMods);
			System.err.println(cdomListMods.getKeySet() + " "
				+ cdo.cdomListMods.getKeySet());
			return false;
		}
		return true;
	}

	// TODO Generic Type Safety rather than CDOMObject
	public <T extends CDOMObject> void putToList(CDOMReference list,
		CDOMReference<T> granted, AssociatedPrereqObject associations)
	{
		cdomListMods.addToListFor(list, granted, associations);
	}

	public boolean hasListMods(CDOMReference list)
	{
		return cdomListMods.containsListFor(list);
	}

	public <BT extends CDOMObject> Collection<CDOMReference<BT>> getListMods(
		CDOMReference<? extends CDOMList<BT>> list)
	{
		CDOMReference listref = list;
		Set set = cdomListMods.getSecondaryKeySet(listref);
		if (set == null || set.isEmpty())
		{
			return null;
		}
		return set;
	}

	public Collection<AssociatedPrereqObject> getListAssociations(
		CDOMReference list, CDOMReference key)
	{
		return cdomListMods.getListFor(list, key);
	}

	public Collection<CDOMReference<CDOMList<? extends CDOMObject>>> getModifiedLists()
	{
		return cdomListMods.getKeySet();
	}

	public String getLSTformat()
	{
		return getKeyName();
	}

	private PCTemplateChooseList tcl = null;

	public boolean hasCDOMTemplateChooseList()
	{
		return tcl == null;
	}

	public PCTemplateChooseList getCDOMTemplateChooseList()
	{
		if (tcl == null)
		{
			tcl = new PCTemplateChooseList();
		}
		return tcl;
	}

	private ChooseActionContainer chooseContainer = null;

	public boolean hasChooseContainer()
	{
		return chooseContainer == null;
	}

	public ChooseActionContainer getChooseContainer()
	{
		if (chooseContainer == null)
		{
			chooseContainer = new ChooseActionContainer("CHOOSE");
		}
		return chooseContainer;
	}

}
