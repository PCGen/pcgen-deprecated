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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.base.util.TripleKeyMap;
import pcgen.base.util.TripleKeyMapToInstanceList;
import pcgen.cdom.base.CDOMCategorizedSingleRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.core.Ability;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

public class CategorizedReferenceContext
{

	private TripleKeyMapToInstanceList<Class, Category, CaseInsensitiveString, CategorizedCDOMObject> duplicates =
			new TripleKeyMapToInstanceList<Class, Category, CaseInsensitiveString, CategorizedCDOMObject>();

	private TripleKeyMap<Class, Category, CaseInsensitiveString, CategorizedCDOMObject> active =
			new TripleKeyMap<Class, Category, CaseInsensitiveString, CategorizedCDOMObject>();

	private TripleKeyMap<Class, Category, CaseInsensitiveString, CDOMCategorizedSingleRef> referenced =
			new TripleKeyMap<Class, Category, CaseInsensitiveString, CDOMCategorizedSingleRef>();

	public <T extends CategorizedCDOMObject<T>> void registerWithKey(
		Class<T> cl, Category<T> cat, T obj, String key)
	{
		CaseInsensitiveString cis = new CaseInsensitiveString(key);
		if (active.containsKey(cl, cat, cis))
		{
			duplicates.addToListFor(cl, cat, cis, obj);
		}
		else
		{
			active.put(cl, cat, cis, obj);
		}
	}

	public <T extends PObject & CategorizedCDOMObject<T>> T silentlyGetConstructedCDOMObject(
		Class<T> c, Category<T> cat, String val)
	{
		CaseInsensitiveString cis = new CaseInsensitiveString(val);
		CategorizedCDOMObject<?> po = active.get(c, cat, cis);
		if (po != null)
		{
			if (duplicates.containsListFor(c, cat, cis))
			{
				Logging.errorPrint("Reference to Constructed "
					+ c.getSimpleName() + " " + val + " is ambiguous");
			}
			return (T) po;
		}
		return null;
	}

	public <T extends PObject & CategorizedCDOMObject<T>> T getConstructedCDOMObject(
		Class<T> c, Category<T> cat, String val)
	{
		T obj = silentlyGetConstructedCDOMObject(c, cat, val);
		if (obj == null)
		{
			Logging.errorPrint("Someone expected " + c.getSimpleName() + " "
				+ cat + " " + val + " to exist.");
			Thread.dumpStack();
		}
		return obj;
	}

	public <T extends PObject & CategorizedCDOMObject<T>> T constructCDOMObject(
		Class<T> c, String val)
	{
		if (val.equals(""))
		{
			throw new IllegalArgumentException("Cannot build Empty Name");
		}
		try
		{
			if (!CategorizedCDOMObject.class.isAssignableFrom(c))
			{
				throw new IllegalArgumentException(c.getSimpleName() + " "
					+ val);
			}
			T obj = c.newInstance();
			obj.setName(val);
			registerWithKey(c, obj.getCDOMCategory(), obj, val);
			return obj;
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new IllegalArgumentException(c + " " + val);
	}

	public <T extends CategorizedCDOMObject<T>> void reassociateReference(
		String value, T obj)
	{
		String oldKey = obj.getKeyName();
		if (oldKey.equalsIgnoreCase(value))
		{
			Logging.errorPrint("Worthless Key change encountered: "
				+ obj.getDisplayName() + " " + oldKey);
		}
		CaseInsensitiveString cis = new CaseInsensitiveString(oldKey);
		Class<T> cl = (Class<T>) obj.getClass();
		Category<T> cat = obj.getCDOMCategory();
		if (active.get(cl, cat, cis).equals(obj))
		{
			List<CategorizedCDOMObject> list =
					duplicates.getListFor(cl, cat, cis);
			if (list == null)
			{
				// No replacement
				active.remove(cl, cat, cis);
			}
			else
			{
				CategorizedCDOMObject newActive =
						duplicates.getItemFor(cl, cat, cis, 0);
				duplicates.removeFromListFor(cl, cat, cis, newActive);
				active.put(cl, cat, cis, newActive);
			}
		}
		else
		{
			duplicates.removeFromListFor(cl, cat, cis, obj);
		}
		obj.setKeyName(value);
		registerWithKey(cl, cat, obj, value);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> void reassociateReference(
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
		String key = obj.getKeyName();
		CaseInsensitiveString cis = new CaseInsensitiveString(key);
		if (active.get(cl, oldCat, cis).equals(obj))
		{
			List<CategorizedCDOMObject> list =
					duplicates.getListFor(cl, oldCat, cis);
			if (list == null)
			{
				// No replacement
				active.remove(cl, oldCat, cis);
			}
			else
			{
				CategorizedCDOMObject newActive =
						duplicates.getItemFor(cl, oldCat, cis, 0);
				duplicates.removeFromListFor(cl, oldCat, cis, newActive);
				active.put(cl, oldCat, cis, newActive);
			}
		}
		else
		{
			duplicates.removeFromListFor(cl, oldCat, cis, obj);
		}
		obj.setCDOMCategory(cat);
		registerWithKey(cl, cat, obj, key);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> void forgetCDOMObjectKeyed(
		Class<T> cl, Category<T> cat, String forgetKey)
	{
		CaseInsensitiveString cis = new CaseInsensitiveString(forgetKey);
		active.remove(cl, cat, cis);
		duplicates.removeListFor(cl, cat, cis);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> Set<T> getConstructedCDOMObjects(
		Class<T> name, Category<T> cat)
	{
		Set values = active.values(name, cat);
		return values;
	}

	public <T extends PObject & CategorizedCDOMObject<T>> boolean containsConstructedCDOMObject(
		Class<T> name, Category<T> cat, String key)
	{
		return active.containsKey(name, cat, new CaseInsensitiveString(key));
	}

	public <T extends CategorizedCDOMObject<T>> T cloneConstructedCDOMObject(
		Class<T> cl, T orig, String newKey)
	{
		try
		{
			T clone = cl.cast(((CDOMObject) orig).clone());
			clone.setName(newKey);
			clone.setKeyName(newKey);
			clone.setCDOMCategory(orig.getCDOMCategory());
			registerWithKey(cl, orig.getCDOMCategory(), clone, newKey);
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			Logging.errorPrint(PropertyFactory.getFormattedString(
				"Errors.LstFileLoader.CopyNotSupported", //$NON-NLS-1$
				cl.getName(), orig.getKeyName(), newKey));
		}
		return null;
	}

	public <T extends PObject & CategorizedCDOMObject<T>> CDOMCategorizedSingleRef<T> getCDOMReference(
		Class<T> c, Category<T> cat, String val)
	{
		// TODO Auto-generated method stub
		// TODO This is incorrect, but a hack for now :)
		if (val.equals(""))
		{
			throw new IllegalArgumentException("Cannot reference Empty Name");
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
		if (c.equals(Ability.class))
		{
			// FIXME This destroys ASSOCIATION information, so need to figure
			// out how to preserve it - needs to be stripped before it gets here
			// :(
			int parenLoc = val.indexOf("(");
			if (parenLoc != -1 && val.charAt(parenLoc - 1) != ' ')
			{
				val = val.substring(0, parenLoc);
			}
		}
		if (c.equals(PCClass.class))
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

		CaseInsensitiveString cis = new CaseInsensitiveString(val);
		CDOMCategorizedSingleRef<T> ref = referenced.get(c, cat, cis);
		if (ref == null)
		{
			ref = new CDOMCategorizedSingleRef<T>(c, cat, val);
			referenced.put(c, cat, cis, ref);
		}
		return ref;
	}

	public boolean validate()
	{
		boolean returnGood = true;
		for (Class key1 : duplicates.getKeySet())
		{
			for (Category key2 : duplicates.getSecondaryKeySet(key1))
			{
				for (CaseInsensitiveString second : duplicates
					.getTertiaryKeySet(key1, key2))
				{
					if (SettingsHandler.isAllowOverride())
					{
						List<CategorizedCDOMObject> list =
								duplicates.getListFor(key1, key2, second);
						CategorizedCDOMObject good =
								active.get(key1, key2, second);
						for (int i = 0; i < list.size(); i++)
						{
							CategorizedCDOMObject dupe = list.get(i);
							// If the new object is more recent than the current
							// one, use the new object
							final Date origDate =
									good.getSourceEntry().getSourceBook()
										.getDate();
							final Date dupeDate =
									dupe.getSourceEntry().getSourceBook()
										.getDate();
							if ((dupeDate != null)
								&& ((origDate == null) || ((dupeDate
									.compareTo(origDate) > 0))))
							{
								duplicates.removeFromListFor(key1, key2,
									second, good);
								good = dupe;
							}
							else
							{
								duplicates.removeFromListFor(key1, key2,
									second, dupe);
							}
						}
						if (!good.equals(active.get(key1, key2, second)))
						{
							active.put(key1, key2, second, good);
						}
					}
					else
					{
						Logging.errorPrint("More than one "
							+ key1.getSimpleName() + " with key/name " + second
							+ " and category " + key2 + " was built");
						returnGood = false;
					}
				}
			}
		}
		for (Class key1 : active.getKeySet())
		{
			for (Category cat : active.getSecondaryKeySet(key1))
			{
				for (CaseInsensitiveString s : active.getTertiaryKeySet(key1,
					cat))
				{
					String keyName = active.get(key1, cat, s).getKeyName();
					if (!keyName.equals(s.toString()))
					{
						Logging.errorPrint("Magical Key Change: " + s + " to "
							+ keyName);
						returnGood = false;
					}
				}
			}
		}
		return validateConstructed();
	}

	private boolean validateConstructed()
	{
		boolean returnGood = true;
		for (Class cl : referenced.getKeySet())
		{
			// System.err.println(cl);
			for (Category cat : referenced.getSecondaryKeySet(cl))
			{
				// System.err.println(cat);
				for (CaseInsensitiveString s : referenced.getTertiaryKeySet(cl,
					cat))
				{
					// System.err.println(s);
					if (!active.containsKey(cl, cat, s))
					{
						Logging.errorPrint("Unconstructed Reference: "
							+ cl.getSimpleName() + " " + cat + " " + s);
						returnGood = false;
					}
				}
			}
		}
		return returnGood;
	}

	public void clear()
	{
		duplicates.clear();
		active.clear();
		referenced.clear();
	}

	public <T extends PObject & CategorizedCDOMObject<T>> ReferenceManufacturer<T> getReferenceManufacturer(
		final Class<T> c, final Category<T> cat)
	{
		if (!Ability.class.equals(c))
		{
			throw new IllegalArgumentException(c.getSimpleName()
				+ " is not a Categorized Class");
		}
		return new ReferenceManufacturer<T>()
		{
			public CDOMReference<T> getReference(String key)
			{
				return getCDOMReference(c, cat, key);
			}

			public Class<T> getCDOMClass()
			{
				return c;
			}
		};
	}

	public Collection<CategorizedCDOMObject> getAllConstructedCDOMObjects()
	{
		Set<CategorizedCDOMObject> set = new HashSet<CategorizedCDOMObject>();
		for (Class<?> cl : active.getKeySet())
		{
			for (Category<?> cat : active.getSecondaryKeySet(cl))
			{
				set.addAll(active.values(cl, cat));
			}
		}
		return set;
	}
}
