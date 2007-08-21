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
package pcgen.persistence;

import java.net.URI;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.base.lang.UnreachableError;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;

public class EditorObjectContext implements ObjectContext
{

	private DoubleKeyMap<CDOMObject, URI, CDOMObject> positiveMap =
			new DoubleKeyMap<CDOMObject, URI, CDOMObject>();

	private DoubleKeyMap<URI, CDOMObject, CDOMObject> negativeMap =
			new DoubleKeyMap<URI, CDOMObject, CDOMObject>();

	private DoubleKeyMapToList<URI, CDOMObject, ListKey<?>> globalClearSet =
			new DoubleKeyMapToList<URI, CDOMObject, ListKey<?>>();

	private URI sourceURI;

	private URI extractURI;

	private CDOMObject getNegative(URI source, CDOMObject cdo)
	{
		CDOMObject negative = negativeMap.get(source, cdo);
		if (negative == null)
		{
			try
			{
				negative = cdo.getClass().newInstance();
			}
			catch (InstantiationException e)
			{
				throw new UnreachableError(
					"CDOM Objects must have a zero argument constructor", e);
			}
			catch (IllegalAccessException e)
			{
				throw new UnreachableError(
					"CDOM Objects must have a public zero argument constructor",
					e);
			}
			negativeMap.put(source, cdo, negative);
		}
		return negative;
	}

	public int getPositiveCount(CDOMObject cdo)
	{
		return positiveMap.secondaryKeyCount(cdo);
	}

	private CDOMObject getPositive(URI source, CDOMObject cdo)
	{
		CDOMObject positive = positiveMap.get(cdo, source);
		if (positive == null)
		{
			try
			{
				positive = CDOMObject.class.newInstance();//cdo.getClass().newInstance();
			}
			catch (InstantiationException e)
			{
				throw new UnreachableError(
					"CDOM Objects must have a zero argument constructor", e);
			}
			catch (IllegalAccessException e)
			{
				throw new UnreachableError(
					"CDOM Objects must have a public zero argument constructor",
					e);
			}
			positiveMap.put(cdo, source, positive);
		}
		return positive;
	}

	public void put(CDOMObject cdo, StringKey sk, String s)
	{
		if (s == null)
		{
			getNegative(sourceURI, cdo).put(sk, Constants.LST_DOT_CLEAR);
			cdo.remove(sk);
		}
		else if (s.startsWith(Constants.LST_DOT_CLEAR))
		{
			throw new IllegalArgumentException("Cannot set a value to " + s);
		}
		else
		{
			getPositive(sourceURI, cdo).put(sk, s);
		}
	}

	public <T> void put(CDOMObject cdo, ObjectKey<T> sk, T s)
	{
		getPositive(sourceURI, cdo).put(sk, s);
	}

	public void put(CDOMObject cdo, IntegerKey ik, Integer i)
	{
		getPositive(sourceURI, cdo).put(ik, i);
	}

	public void put(CDOMObject cdo, FormulaKey fk, Formula f)
	{
		getPositive(sourceURI, cdo).put(fk, f);
	}

	public void put(CDOMObject cdo, VariableKey vk, Formula f)
	{
		getPositive(sourceURI, cdo).put(vk, f);
	}

	public boolean containsListFor(CDOMObject cdo, ListKey<?> key)
	{
		return cdo.containsListFor(key);
	}

	public <T> void addToList(CDOMObject cdo, ListKey<T> key, T value)
	{
		getPositive(sourceURI, cdo).addToListFor(key, value);
	}

	public void removeList(CDOMObject cdo, ListKey<?> lk)
	{
		globalClearSet.addToListFor(sourceURI, cdo, lk);
	}

	public <T> void removeFromList(CDOMObject cdo, ListKey<T> lk, T val)
	{
		getNegative(sourceURI, cdo).addToListFor(lk, val);
	}

	public String getString(CDOMObject cdo, StringKey sk)
	{
		return getPositive(extractURI, cdo).get(sk);
	}

	public Integer getInteger(CDOMObject cdo, IntegerKey ik)
	{
		return getPositive(extractURI, cdo).get(ik);
	}

	public Formula getFormula(CDOMObject cdo, FormulaKey fk)
	{
		return getPositive(extractURI, cdo).get(fk);
	}

	public Formula getVariable(CDOMObject cdo, VariableKey key)
	{
		return getPositive(extractURI, cdo).get(key);
	}

	public Set<VariableKey> getVariableKeys(CDOMObject cdo)
	{
		return getPositive(extractURI, cdo).getVariableKeys();
	}

	public <T> T getObject(CDOMObject cdo, ObjectKey<T> ik)
	{
		return getPositive(extractURI, cdo).get(ik);
	}

	public <T> Changes<T> getListChanges(CDOMObject cdo, ListKey<T> lk)
	{
		return new CollectionChanges<T>(getPositive(extractURI, cdo)
			.getListFor(lk), getNegative(extractURI, cdo).getListFor(lk),
			globalClearSet.containsInList(extractURI, cdo, lk));
	}

	public URI getExtractURI()
	{
		return extractURI;
	}

	public void setExtractURI(URI extractURI)
	{
		this.extractURI = extractURI;
	}

	public URI getSourceURI()
	{
		return sourceURI;
	}

	public void setSourceURI(URI sourceURI)
	{
		this.sourceURI = sourceURI;
	}
}
