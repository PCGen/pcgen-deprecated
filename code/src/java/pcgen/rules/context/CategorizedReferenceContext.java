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
package pcgen.rules.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.reference.CDOMCategorizedSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.CategorizedReferenceManufacturer;
import pcgen.util.Logging;

public class CategorizedReferenceContext
{

	private DoubleKeyMap<Class<?>, Category<?>, ReferenceSupport<?, ?>> refMap = new DoubleKeyMap<Class<?>, Category<?>, ReferenceSupport<?, ?>>();

	private <T extends CDOMObject & CategorizedCDOMObject<T>> ReferenceSupport<T, CDOMCategorizedSingleRef<T>> getRefSupport(
			Class<T> cl, Category<T> cat)
	{
		ReferenceSupport<T, CDOMCategorizedSingleRef<T>> ref = (ReferenceSupport<T, CDOMCategorizedSingleRef<T>>) refMap
				.get(cl, cat);
		if (ref == null)
		{
			ref = new ReferenceSupport<T, CDOMCategorizedSingleRef<T>>(
					new CategorizedReferenceManufacturer<T>(cl, cat));
			refMap.put(cl, cat, ref);
		}
		return ref;
	}

	public <T extends CDOMObject> void reassociateKey(T obj, String key)
	{
		Class cl = obj.getClass();
		Category cat = ((CategorizedCDOMObject) obj).getCDOMCategory();
		getRefSupport(cl, cat).reassociateKey(key, obj);
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> T silentlyGetConstructedCDOMObject(
			Class<T> c, Category<T> cat, String val)
	{
		return getRefSupport(c, cat).silentlyGetConstructedCDOMObject(val);
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> T getConstructedCDOMObject(
			Class<T> c, Category<T> cat, String val)
	{
		return getRefSupport(c, cat).getConstructedCDOMObject(val);
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> T constructCDOMObject(
			Class<T> c, Category<T> cat, String val)
	{
		return getRefSupport(c, cat).constructCDOMObject(val);
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> void forgetCDOMObjectKeyed(
			Class<T> cl, Category<T> cat, String forgetKey)
	{
		getRefSupport(cl, cat).forgetCDOMObjectKeyed(forgetKey);
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> Collection<T> getConstructedCDOMObjects(
			Class<T> name, Category<T> cat)
	{
		return getRefSupport(name, cat).getConstructedCDOMObjects();
	}

	public <U extends CDOMObject & CategorizedCDOMObject<U>> Collection<? extends CDOMObject> getAllConstructedCDOMObjects(
			Class<U> name)
	{
		List<CDOMObject> list = new ArrayList<CDOMObject>();
		for (Category<?> cat : refMap.getSecondaryKeySet(name))
		{
			Collection<U> constructedCDOMObjects = getRefSupport(name,
					(Category<U>) cat).getConstructedCDOMObjects();
			list.addAll(constructedCDOMObjects);
		}
		return list;
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> boolean containsConstructedCDOMObject(
			Class<T> name, Category<T> cat, String key)
	{
		return getRefSupport(name, cat).containsConstructedCDOMObject(key);
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMSingleRef<T> getCDOMReference(
			Class<T> c, Category<T> cat, String val)
	{
		return getRefSupport(c, cat).getCDOMReference(val);
	}

	public boolean validate()
	{
		boolean returnGood = true;
		for (Class<?> cl : refMap.getKeySet())
		{
			for (ReferenceSupport<?, ?> ref : refMap.values(cl))
			{
				returnGood &= ref.validate();
			}
			/*
			 * TODO Make sure nothing is in the null category
			 */
		}
		return returnGood;
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> void constructIfNecessary(
			Class<T> cl, Category<T> cat, String value)
	{
		getRefSupport(cl, cat).constructIfNecessary(value);
	}

	public void clear()
	{
		refMap.clear();
	}

	public Collection<CDOMObject> getAllConstructedCDOMObjects()
	{
		Set<CDOMObject> set = new HashSet<CDOMObject>();
		for (Class<?> cl : refMap.getKeySet())
		{
			for (ReferenceSupport<?, ?> ref : refMap.values(cl))
			{
				set.addAll(ref.getAllConstructedCDOMObjects());
			}
		}
		return set;
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> void reassociateCategory(
			Category<T> cat, T obj)
	{
		Category<T> oldCat = obj.getCDOMCategory();
		if (oldCat == null && cat == null || oldCat != null
				&& oldCat.equals(cat))
		{
			Logging.errorPrint("Worthless Category change encountered: "
					+ obj.getDisplayName() + " " + oldCat);
		}
		Class<T> cl = (Class<T>) obj.getClass();
		ReferenceSupport<T, CDOMCategorizedSingleRef<T>> oldSupt = getRefSupport(
				cl, oldCat);
		oldSupt.forgetObject(obj);
		obj.setCDOMCategory(cat);
		ReferenceSupport<T, CDOMCategorizedSingleRef<T>> newSupt = getRefSupport(
				cl, cat);
		newSupt.registerWithKey(obj, obj.getKeyName());
	}

	private DoubleKeyMap<Class<?>, Category<?>, CategorizedReferenceManufacturer<?>> map = 
		new DoubleKeyMap<Class<?>, Category<?>, CategorizedReferenceManufacturer<?>>();
	
	public <T extends CDOMObject & CategorizedCDOMObject<T>> CategorizedReferenceManufacturer<T> getManufacturer(Class<T> cl, Category<T> cat)
	{
		CategorizedReferenceManufacturer<T> mfg = (CategorizedReferenceManufacturer<T>) map.get(cl, cat);
		if (mfg == null)
		{
			mfg = new CategorizedReferenceManufacturer<T>(cl, cat);
			map.put(cl, cat, mfg);
		}
		return mfg;
	}

}
