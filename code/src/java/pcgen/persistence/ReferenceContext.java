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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.CDOMAddressedSingleRef;
import pcgen.cdom.base.CDOMCategorizedSingleRef;
import pcgen.cdom.base.CDOMDirectSingleRef;
import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.core.ClassSkillList;
import pcgen.core.ClassSpellList;
import pcgen.core.Domain;
import pcgen.core.DomainSpellList;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SpellProgressionInfo;
import pcgen.core.SubClass;
import pcgen.util.StringPClassUtil;

public class ReferenceContext
{

	private GroupReferenceContext group = new GroupReferenceContext();
	private SimpleReferenceContext simple = new SimpleReferenceContext();
	private CategorizedReferenceContext categorized =
			new CategorizedReferenceContext();

	public Class<? extends CDOMObject> getClassFor(String key)
	{
		return StringPClassUtil.getClassFor(key);
	}

	public void clear()
	{
		simple.clear();
		categorized.clear();
		group.clear();
	}

	public boolean validate()
	{
		return simple.validate() && categorized.validate();
	}

	public <T extends PObject & CategorizedCDOMObject<T>> CDOMGroupRef<T> getCDOMAllReference(
		Class<T> c, Category<T> cat)
	{
		return group.getCategorizedCDOMAllReference(c, cat);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> CDOMGroupRef<T> getCDOMTypeReference(
		Class<T> c, Category<T> cat, String... val)
	{
		return group.getCategorizedCDOMTypeReference(c, cat, val);
	}

	public <T extends PObject> CDOMGroupRef<T> getCDOMAllReference(Class<T> c)
	{
		return group.getCDOMAllReference(c);
	}

	public <T extends PObject> CDOMGroupRef<T> getCDOMTypeReference(Class<T> c,
		String... val)
	{
		return group.getCDOMTypeReference(c, val);
	}

	public <T extends PObject> T constructCDOMObject(Class<T> c, String val)
	{
		if (CategorizedCDOMObject.class.isAssignableFrom(c))
		{
			return (T) categorized.constructCDOMObject((Class) c, val);
		}
		else
		{
			return simple.constructCDOMObject(c, val);
		}
	}

	public <T extends CDOMObject> void constructIfNecessary(Class<T> cl,
		String value)
	{
		simple.constructIfNecessary(cl, value);
	}

	public <T extends CDOMObject> CDOMSimpleSingleRef<T> getCDOMReference(
		Class<T> c, String val)
	{
		return simple.getCDOMReference(c, val);
	}

	public <T extends PObject> T getConstructedCDOMObject(Class<T> c, String val)
	{
		return simple.getConstructedCDOMObject(c, val);
	}

	public <T extends PObject> void reassociateReference(String value, T obj)
	{
		if (CategorizedCDOMObject.class.isAssignableFrom(obj.getClass()))
		{
			categorized
				.reassociateReference(value, (CategorizedCDOMObject) obj);
		}
		else
		{
			simple.reassociateReference(value, obj);
		}
	}

	public <T extends PObject> T silentlyGetConstructedCDOMObject(Class<T> c,
		String val)
	{
		return simple.silentlyGetConstructedCDOMObject(c, val);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> CDOMCategorizedSingleRef<T> getCDOMReference(
		Class<T> c, Category<T> cat, String val)
	{
		return categorized.getCDOMReference(c, cat, val);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> T getConstructedCDOMObject(
		Class<T> c, Category<T> cat, String val)
	{
		return categorized.getConstructedCDOMObject(c, cat, val);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> T silentlyGetConstructedCDOMObject(
		Class<T> c, Category<T> cat, String val)
	{
		return categorized.silentlyGetConstructedCDOMObject(c, cat, val);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> void reassociateReference(
		Category<T> cat, T obj)
	{
		categorized.reassociateReference(cat, obj);
	}

	public <T extends PObject> T cloneConstructedCDOMObject(Class<T> cl,
		T orig, String newKey)
	{
		if (CategorizedCDOMObject.class.isAssignableFrom(cl))
		{
			Class catCl = (Class) cl;
			CategorizedCDOMObject cco = (CategorizedCDOMObject) orig;
			return (T) categorized.cloneConstructedCDOMObject(catCl, cco,
				newKey);
		}
		else
		{
			return simple.cloneConstructedCDOMObject(cl, orig, newKey);
		}
	}

	public <T extends PObject> ReferenceManufacturer<T> getReferenceManufacturer(
		Class<T> c)
	{
		return simple.getReferenceManufacturer(c);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> ReferenceManufacturer<T> getReferenceManufacturer(
		Class<T> c, Category<T> cat)
	{
		return categorized.getReferenceManufacturer(c, cat);
	}

	public <T extends PObject> Set<T> getConstructedCDOMObjects(Class<T> c)
	{
		return simple.getConstructedCDOMObjects(c);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> Set<T> getConstructedCDOMObjects(
		Class<T> c, Category<T> cat)
	{
		return categorized.getConstructedCDOMObjects(c, cat);
	}

	public <T extends PObject> boolean containsConstructedCDOMObject(
		Class<T> c, String s)
	{
		return simple.containsConstructedCDOMObject(c, s);
	}

	public void buildDerivedObjects()
	{
		Set<Domain> domains = simple.getConstructedCDOMObjects(Domain.class);
		for (Domain d : domains)
		{
			simple.constructCDOMObject(DomainSpellList.class, d.getKeyName());
		}
		Set<PCClass> classes = simple.getConstructedCDOMObjects(PCClass.class);
		for (PCClass pcc : classes)
		{
			simple.constructCDOMObject(ClassSkillList.class, pcc.getKeyName());
			//TODO Need to limit which are built to only spellcasters...
			simple.constructCDOMObject(ClassSpellList.class, pcc.getKeyName());
			simple.constructCDOMObject(SpellProgressionInfo.class, pcc.getKeyName());
			List<SubClass> subclasses = pcc.getSubClassList();
			if (subclasses != null)
			{
				for (SubClass subcl : subclasses)
				{
					simple.constructCDOMObject(ClassSkillList.class, subcl.getKeyName());
					//TODO Need to limit which are built to only spellcasters...
					simple.constructCDOMObject(ClassSpellList.class, subcl.getKeyName());
					simple.constructCDOMObject(SpellProgressionInfo.class, subcl.getKeyName());
				}
			}
		}
	}

	public <T extends PObject> CDOMAddressedSingleRef<T> getCDOMAddressedReference(
		CDOMObject obj, Class<T> name, String string)
	{
		return simple.getAddressedReference(obj, name, string);
	}

	private HashMap<PObject, CDOMSingleRef<?>> directRef =
			new HashMap<PObject, CDOMSingleRef<?>>();

	public <T extends PObject> CDOMSingleRef<T> getCDOMDirectReference(T obj)
	{
		CDOMSingleRef<?> ref = directRef.get(obj);
		if (ref == null)
		{
			ref = new CDOMDirectSingleRef<T>(obj);
		}
		return (CDOMSingleRef<T>) ref;
	}
}
