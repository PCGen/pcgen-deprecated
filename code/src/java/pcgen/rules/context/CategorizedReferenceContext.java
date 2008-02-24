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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.CDOMCategorizedSingleRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.core.PCClass;
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

	public <T extends CDOMObject & CategorizedCDOMObject<T>> void registerWithKey(
			Class<T> cl, Category<T> cat, T obj, String key)
	{
		getRefSupport(cl, cat).registerWithKey(obj, key);
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

	public <T extends CDOMObject & CategorizedCDOMObject<T>> ReferenceManufacturer<T, CDOMCategorizedSingleRef<T>> getReferenceManufacturer(
			final Class<T> c, Category<T> cat)
	{
		return getRefSupport(c, cat).getReferenceManufacturer();
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

	public class CategorizedReferenceManufacturer<T extends CDOMObject & CategorizedCDOMObject<T>>
			implements ReferenceManufacturer<T, CDOMCategorizedSingleRef<T>>
	{
		private final Class<T> refClass;
		private final Category<T> category;

		public CategorizedReferenceManufacturer(Class<T> cl, Category<T> cat)
		{
			refClass = cl;
			category = cat;
		}

		public CDOMCategorizedSingleRef<T> getReference(String val)
		{
			// TODO Auto-generated method stub
			// TODO This is incorrect, but a hack for now :)
			if (val.equals(""))
			{
				throw new IllegalArgumentException(val);
			}
			try
			{
				Integer.parseInt(val);
				throw new IllegalArgumentException(val);
			}
			catch (NumberFormatException nfe)
			{
				// ok
			}
			if (val.startsWith("TYPE"))
			{
				throw new IllegalArgumentException(val);
			}
			if (val.equalsIgnoreCase("ANY"))
			{
				throw new IllegalArgumentException(val);
			}
			if (val.equalsIgnoreCase("ALL"))
			{
				throw new IllegalArgumentException(val);
			}
			if (val.startsWith("PRE"))
			{
				throw new IllegalArgumentException(val);
			}
			if (val.startsWith("CHOOSE"))
			{
				throw new IllegalArgumentException(val);
			}
			if (val.startsWith("TIMES="))
			{
				throw new IllegalArgumentException(val);
			}
			if (refClass.equals(PCClass.class))
			{
				if (val.startsWith("CLASS"))
				{
					throw new IllegalArgumentException(val);
				}
				else if (val.startsWith("SUB"))
				{
					throw new IllegalArgumentException(val);
				}
				else
				{
					try
					{
						Integer.parseInt(val);
						throw new IllegalArgumentException(val);
					}
					catch (NumberFormatException nfe)
					{
						// Want this!
					}
				}
			}

			return new CDOMCategorizedSingleRef<T>(refClass, category, val);
		}

		public Class<T> getCDOMClass()
		{
			return refClass;
		}

	}
}
