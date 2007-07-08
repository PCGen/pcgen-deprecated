/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CategorizedCDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.Qualifier;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;
import pcgen.core.spell.Spell;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.ReferenceManufacturer;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;
import pcgen.util.StringPClassUtil;

/**
 * Deals with the QUALIFY token for Abilities
 */
public class QualifyToken extends AbstractToken implements GlobalLstToken
{

	@Override
	public String getTokenName()
	{
		return "QUALIFY";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (!getLegalTypes().contains(obj.getClass()))
		{
			Logging.errorPrint("Cannot use QUALIFY on a " + obj.getClass());
			return false;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		String key = st.hasMoreTokens() ? st.nextToken() : "";
		Class<? extends PObject> c;
		String category = null;
		int equalLoc = key.indexOf('=');
		if (equalLoc == -1)
		{
			if ("ABILITY".equals(key))
			{
				Logging.errorPrint("Invalid use of ABILITY in QUALIFY "
					+ "(requires ABILITY=<category>): " + key);
				return false;
			}
			c = StringPClassUtil.getClassFor(key);
		}
		else
		{
			if (!"ABILITY".equals(key.substring(0, equalLoc)))
			{
				Logging.errorPrint("Invalid use of = in QUALIFY "
					+ "(only valid for ABILITY): " + key);
				return false;
			}
			c = Ability.class;
			category = key.substring(equalLoc + 1);
		}
		if (c == null)
		{
			Logging.errorPrint(getTokenName()
				+ " expecting a POBJECT Type, found: " + key);
			Logging
				.errorPrint("  5.14 Format is: QualifyType|Key[|Key] value was: "
					+ value);
			Logging.errorPrint("  Valid QualifyTypes are: "
				+ StringPClassUtil.getValidStrings());
			return false;
		}
		else
		{
			key = st.nextToken();
		}

		while (true)
		{
			obj.putQualifyString(c, category, key);
			if (!st.hasMoreTokens())
			{
				break;
			}
			key = st.nextToken();
		}

		return true;
	}

	public List<Class<? extends PObject>> getLegalTypes()
	{
		return Arrays.asList(Ability.class, Deity.class, Domain.class,
			Equipment.class, PCClass.class, PCClassLevel.class, Race.class,
			Skill.class, Spell.class, PCTemplate.class, WeaponProf.class);
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (!getLegalTypes().contains(obj.getClass()))
		{
			Logging.errorPrint("Cannot use QUALIFY on a " + obj.getClass());
			return false;
		}
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		if (value.indexOf("|") == -1)
		{
			Logging.errorPrint(getTokenName()
				+ " requires at least two arguments, QualifyType and Key: "
				+ value);
			return false;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		ReferenceManufacturer<? extends PObject> rm =
				StringPClassUtil.getReferenceManufacturer(context, st
					.nextToken());
		if (rm == null)
		{
			Logging.errorPrint("  Error encountered parsing " + getTokenName());
			Logging.errorPrint("  Format is: QualifyType|Key[|Key] value was: "
				+ value);
			Logging.errorPrint("  Valid QualifyTypes are: "
				+ StringPClassUtil.getValidStrings());
			return false;
		}

		while (st.hasMoreTokens())
		{
			CDOMReference<? extends PObject> ref =
					rm.getReference(st.nextToken());
			obj.addToListFor(ListKey.QUALIFY, new Qualifier(rm.getCDOMClass(),
				ref));
		}

		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<Qualifier> changes =
				context.obj.getListChanges(obj, ListKey.QUALIFY);
		if (changes == null)
		{
			return null;
		}
		Collection<Qualifier> quals = changes.getAdded();
		HashMapToList<String, CDOMReference<?>> map =
				new HashMapToList<String, CDOMReference<?>>();
		for (Qualifier qual : quals)
		{
			Class<? extends PObject> cl = qual.getQualifiedClass();
			String s = StringPClassUtil.getStringFor(cl);
			CDOMReference<?> ref = qual.getQualifiedReference();
			String key = s;
			if (ref instanceof CategorizedCDOMReference)
			{
				Category<?> cat =
						((CategorizedCDOMReference) ref).getCDOMCategory();
				key += '=' + cat.toString();
			}
			map.addToListFor(key, ref);
		}
		Set<CDOMReference<?>> set =
				new TreeSet<CDOMReference<?>>(TokenUtilities.REFERENCE_SORTER);
		Set<String> returnSet = new TreeSet<String>();
		for (String key : map.getKeySet())
		{
			set.clear();
			set.addAll(map.getListFor(key));
			StringBuilder sb = new StringBuilder();
			sb.append(key).append(Constants.PIPE).append(
				ReferenceUtilities.joinLstFormat(set, Constants.PIPE));
			returnSet.add(sb.toString());
		}
		return returnSet.toArray(new String[returnSet.size()]);
	}
}
