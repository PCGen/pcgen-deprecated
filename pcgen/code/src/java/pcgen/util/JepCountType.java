/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net> Derived from
 * AbstractCountCommand.java Copyright 2013 (C) James Dempsey
 * <jdempsey@users.sourceforge.net>
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
 * 
 * Created on 11/08/2013
 * 
 * $Id: AbstractCountCommand.java 22768 2014-01-04 10:35:48Z zaister $
 */
package pcgen.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.nfunk.jep.ParseException;

import pcgen.base.lang.UnreachableError;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.AbilityUtilities;
import pcgen.core.ChronicleEntry;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.util.AbstractCountCommand.JepAbilityCountEnum;
import pcgen.util.AbstractCountCommand.JepEquipmentCountEnum;
import pcgen.util.enumeration.Visibility;

public abstract class JepCountType
{

	/**
	 * This Map contains the mappings from Strings to the Type Safe Constant
	 */
	private static CaseInsensitiveMap<JepCountType> typeMap = null;

	public static final JepCountType ABILITIES = new JepCountFilterable<CNAbility>()
	{
		
		private final List<String> assocList = new ArrayList<String>();

		@Override
		protected Collection<CNAbility> getData(final PlayerCharacter pc)
		{
			assocList.clear();
			return pc.getCNAbilities();
		}

		@Override
		protected Double countData(Collection<? extends CNAbility> filtered,
			PlayerCharacter pc)
		{
			double accum = 0;
			for (final CNAbility ab : filtered)
			{
				if (assocList.isEmpty())
				{
					final double ac = pc.getSelectCorrectedAssociationCount(ab.getAbility());
					accum += 1.01 >= ac ? 1 : ac;
				}
				else
				{
					for (String assoc : pc.getAssociationList(ab.getAbility()))
					{
						if (assocList.contains(assoc))
						{
							accum++;
						}
					}
				}
			}
			return accum;
		}

		@Override
		protected Collection<? extends CNAbility> filterSetP(final String c,
			Collection<CNAbility> coll)
		{
			final String[] keyValue = c.split("=");
			final JepAbilityCountEnum en;

			try
			{
				en = JepAbilityCountEnum.valueOf(keyValue[0]);
			}
			catch (IllegalArgumentException ex)
			{
				Logging.errorPrint("Bad parameter to count(\"Ability\"), " + c);
				return new HashSet<CNAbility>();
			}

			ObjectFilter<CNAbility> filter = null;
			switch (en)
			{
				case CATEGORY:
				case CAT:
					filter = new CategoryFilter(keyValue[1]);
					break;

				case NAME:
				case NAM:
					//TODO need to initialize assocFilter :/
					filter = new DisplayNameFilter(keyValue[1]);
					break;

				case KEY:
					filter = new KeyNameFilter(keyValue[1], assocList);
					break;

				case NATURE:
				case NAT:
					try
					{
						Nature n = Nature.valueOf(keyValue[1]);
						if (!n.equals(Nature.ANY))
						{
							filter = new NatureFilter(n);
						}
					}
					catch (IllegalArgumentException ex)
					{
						Logging
							.errorPrint("Bad parameter to count(\"Ability\"), no such NATURE "
								+ c);
					}

					break;

				case TYPE:
				case TYP:
					filter = new TypeFilter(keyValue[1]);
					break;

				case EXCLUDETYPE:
					filter = new TypeExclusionFilter(keyValue[1]);
					break;

				case VISIBILITY:
				case VIS:
					try
					{
						final Visibility vi = Visibility.valueOf(keyValue[1]);
						filter = new VisibilityFilter(vi);
					}
					catch (IllegalArgumentException ex)
					{
						Logging
							.errorPrint("Bad parameter to count(\"Ability\"), no such Visibility "
								+ keyValue[1]);
					}
					break;

				case ASPECT:
					filter = new AspectFilter(keyValue);
					break;
			}

			List<CNAbility> ret = new ArrayList<CNAbility>(coll);
			if (filter != null)
			{
				for (Iterator<CNAbility> it = ret.iterator(); it.hasNext();)
				{
					CNAbility cna = it.next();
					if (!filter.accept(cna))
					{
						it.remove();
					}
				}
			}
			return ret;
		}
	};

	public static final JepCountType CAMPAIGNHISTORY =
			new JepCountFilterable<ChronicleEntry>()
			{
				@Override
				protected Collection<ChronicleEntry> getData(
					final PlayerCharacter pc)
				{
					return pc.getDisplay().getChronicleEntries();
				}

				@Override
				public Number count(PlayerCharacter pc, Object[] params)
					throws ParseException
				{
					final Object[] par =
							params.length > 0 ? params
								: new String[]{"EXPORT=YES"};
					return super.count(pc, par);
				}

				@Override
				protected Set<ChronicleEntry> filterSetP(final String c,
					Collection<ChronicleEntry> coll) throws ParseException
				{
					final String[] keyValue = c.split("=");

					if (!"EXPORT".equalsIgnoreCase(keyValue[0]))
					{
						throw new ParseException(
							"Bad parameter to count(\"CAMPAIGNHISTORY\" ... )"
								+ c);
					}
					if (!"NO".equalsIgnoreCase(keyValue[1])
						&& !"YES".equalsIgnoreCase(keyValue[1]))
					{
						throw new ParseException(
							"Bad EXPORT value to count(\"CAMPAIGNHISTORY\" ... )"
								+ c);
					}

					boolean wantExport = "YES".equalsIgnoreCase(keyValue[1]);
					final Set<ChronicleEntry> cs =
							new HashSet<ChronicleEntry>();
					for (ChronicleEntry ce : coll)
					{
						if (ce.isOutputEntry() == wantExport)
						{
							cs.add(ce);
						}
					}
					return cs;
				}

			};

	public static final JepCountType CLASSES = new JepCountCDOMObject<PCClass>()
	{
		@Override
		protected Collection<PCClass> getData(final PlayerCharacter pc)
		{
			return pc.getDisplay().getClassSet();
		}
	};
	
	public static final JepCountType DOMAINS = new JepCountCDOMObject<Domain>()
	{
		@Override
		protected Collection<Domain> getData(final PlayerCharacter pc)
		{
			return pc.getDisplay().getDomainSet();
		}
	};

	public static final JepCountType EQUIPMENT = new JepCountCDOMObject<Equipment>()
	{
		@Override
		protected Collection<Equipment> getData(final PlayerCharacter pc)
		{
			return pc.getEquipmentListInOutputOrder();
		}

		@Override
		protected Set<? extends Equipment> filterSetP(final String c,
			Collection<Equipment> coll) throws ParseException
		{
			final String[] keyValue = c.split("=");

			final JepEquipmentCountEnum en;

			try
			{
				en = JepEquipmentCountEnum.valueOf(keyValue[0]);
			}
			catch (IllegalArgumentException ex)
			{
				Logging.errorPrint("Bad parameter to count(\"Equipment\"), "
					+ c);
				return new HashSet<Equipment>();
			}

			final Set<Equipment> cs = new HashSet<Equipment>(coll);
			final Iterator<? extends Equipment> it = cs.iterator();

			switch (en)
			{
				case TYPE:
					filterPObjectByType(it, keyValue[1]);
					break;

				case WIELDCATEGORY:
					while (it.hasNext())
					{
						final Equipment e = it.next();
						if (!e.getWieldName().equalsIgnoreCase(keyValue[1]))
						{
							it.remove();
						}
					}
					break;

				// TODO have no idea how to get a suitable list of equipment
				// and test for this.

				case LOCATION:
					if ("CARRIED".equalsIgnoreCase(keyValue[1])
						|| "Equipped".equalsIgnoreCase(keyValue[1]))
					{
						//					while (it.hasNext())
						//					{
						//						Equipment e = (Equipment) it.next();
						//						if (! e.getParent().equalsIgnoreCase(keyValue[1]));
						//						{
						//							it.remove();
						//						}
						//					}
					}
				case LOC:
					break;
				case TYP:
					break;
				case WDC:
					break;
			}

			return cs;
		}
	};

	public static final JepCountType FOLLOWERS = new JepCountType()
	{
		@Override
		public Number count(PlayerCharacter pc, Object[] params)
		{
			//TODO what if params is not empty??
			return pc.getDisplay().getFollowerList().size();
		}
	};

	public static final JepCountType LANGUAGES = new JepCountCDOMObject<Language>()
	{
		@Override
		protected Collection<Language> getData(final PlayerCharacter pc)
		{
			return pc.getDisplay().getLanguageSet();
		}
	};

	public static final JepCountType RACESUBTYPE = new JepCountType()
	{
		@Override
		public Number count(PlayerCharacter pc, Object[] params)
			throws ParseException
		{
			return pc.getDisplay().getRacialSubTypeCount();
		}

	};

	public static final JepCountType SKILLS = new JepCountCDOMObject<Skill>()
	{
		@Override
		protected Collection<Skill> getData(PlayerCharacter pc)
		{
			pc.refreshSkillList();
			return pc.getDisplay().getSkillSet();
		}
	};

	public static final JepCountType SPELLBOOKS = new JepCountType()
	{
		@Override
		public Number count(PlayerCharacter pc, Object[] params)
		{
			//TODO what if params is not empty??
			return pc.getDisplay().getSpellBookCount();
		}
	};

	public abstract Number count(PlayerCharacter pc, Object[] params)
		throws ParseException;

	private final class AspectFilter implements ObjectFilter<CNAbility>
	{
		private final String[] keyValue;

		private AspectFilter(String[] keyValue)
		{
			this.keyValue = keyValue;
		}

		public boolean accept(CNAbility o)
		{
			return o.getAbility().get(MapKey.ASPECT,
				AspectName.getConstant(keyValue[1])) != null;
		}
	}

	private final class VisibilityFilter implements ObjectFilter<CNAbility>
	{
		private final Visibility vi;

		private VisibilityFilter(Visibility vi)
		{
			this.vi = vi;
		}

		public boolean accept(CNAbility o)
		{
			return o.getAbility().getSafe(ObjectKey.VISIBILITY).equals(vi);
		}
	}

	private final class TypeFilter implements ObjectFilter<CNAbility>
	{
		String type;

		public TypeFilter(String typ)
		{
			type = typ;
		}

		public boolean accept(CNAbility o)
		{
			//isType already accounts for A.B.C, so we don't have to do that here
			return o.getAbility().isType(type);
		}
	}

	private final class TypeExclusionFilter implements ObjectFilter<CNAbility>
	{
		String type;

		public TypeExclusionFilter(String typ)
		{
			type = typ;
		}

		public boolean accept(CNAbility o)
		{
			//Since this is exclude on "any" we have to expand this out
			StringTokenizer tok = new StringTokenizer(type, ".");
			Ability a = o.getAbility();
			while (tok.hasMoreTokens())
			{
				if (a.containsInList(ListKey.TYPE,
					Type.getConstant(tok.nextToken())))
				{
					return false;
				}
			}
			return true;
		}
	}

	private final class NatureFilter implements ObjectFilter<CNAbility>
	{
		Nature nature;

		public NatureFilter(Nature n)
		{
			nature = n;
		}

		public boolean accept(CNAbility o)
		{
			return o.getNature().equals(nature);
		}
	}

	private final class KeyNameFilter implements ObjectFilter<CNAbility>
	{
		private final String name;
		private final List<String> assocList;

		private KeyNameFilter(String keyValue, List<String> list)
		{
			this.name = keyValue;
			assocList = list;
		}

		public boolean accept(CNAbility o)
		{
			List<String> assocs = new ArrayList<String>();
			String undec = AbilityUtilities.getUndecoratedName(name, assocs);
			Ability ab = o.getAbility();
			String keyName = ab.getKeyName();
			if (keyName.equalsIgnoreCase(undec))
			{
				assocList.addAll(assocs);
				return true;
			}
			return keyName.equalsIgnoreCase(name);
		}
	}

	private final class DisplayNameFilter implements ObjectFilter<CNAbility>
	{
		private final String name;

		private DisplayNameFilter(String keyValue)
		{
			this.name = keyValue;
		}

		public boolean accept(CNAbility o)
		{
			return o.getAbility().getDisplayName().equalsIgnoreCase(name);
		}
	}

	private final class CategoryFilter implements ObjectFilter<CNAbility>
	{
		private final String cat;

		private CategoryFilter(String cat)
		{
			this.cat = cat;
		}

		public boolean accept(CNAbility o)
		{
			return o.getAbilityCategory().getKeyName().equalsIgnoreCase(cat);
		}
	}

	public static abstract class JepCountCDOMObject<T extends CDOMObject>
			extends JepCountFilterable<T>
	{
		@Override
		public Number count(PlayerCharacter pc, Object[] params)
			throws ParseException
		{
			return super.count(pc, validateParams(params));
		}

		// By adding this it means that we can call count with just the object to be
		// counted and get a count of all e.g. count("ABILITIES") will return a
		// count of all abilities with no filtering at all.
		protected Object[] validateParams(final Object[] params)
			throws ParseException
		{
			Object[] p = new Object[1];
			if (1 > params.length)
			{
				p[0] = "TYPE=ALL";
			}
			else
			{
				p = params;
			}
			return p;
		}

		@Override
		protected Set<? extends T> filterSetP(final String c, Collection<T> coll)
			throws ParseException
		{
			final String[] keyValue = c.split("=");

			if (!"TYPE".equalsIgnoreCase(keyValue[0]))
			{
				throw new ParseException(
					"Bad parameter to count(\"CLASSES\" ... )" + c);
			}

			final Set<T> cs = new HashSet<T>(coll);
			final Iterator<? extends T> it = cs.iterator();

			filterPObjectByType(it, keyValue[1]);
			return cs;
		}

		protected void filterPObjectByType(final Iterator<? extends T> it,
			final String tString)
		{
			// If we want all then we don't need to filter.
			if (!"ALL".equalsIgnoreCase(tString))
			{
				// Make a List of all the types that each PObject should match
				final Collection<String> typeList = new ArrayList<String>();
				Collections.addAll(typeList, tString.split("\\."));

				// These nested loops remove all PObjects from the collection being
				// iterated that do not match all of the types in typeList
				while (it.hasNext())
				{
					final T pObj = it.next();

					for (final String type : typeList)
					{
						if (!pObj.isType(type))
						{
							it.remove();
							break;
						}
					}
				}
			}
		}
	}

	public static abstract class JepCountFilterable<T> extends JepCountType
	{
		protected abstract Collection<T> getData(final PlayerCharacter pc);

		protected static ParameterTree convertParams(final Object[] params)
		{
			ParameterTree pt = null;

			for (final Object param : params)
			{
				try
				{
					if (pt == null)
					{
						pt = ParameterTree.makeTree((String) param);
					}
					else
					{
						final ParameterTree npt =
								ParameterTree.makeTree(ParameterTree.andString);
						npt.setLeftTree(pt);
						pt = npt;
						final ParameterTree npt1 =
								ParameterTree.makeTree((String) param);
						pt.setRightTree(npt1);
					}
				}
				catch (ParseException pe)
				{
					Logging.errorPrint(MessageFormat.format(
						"Malformed parameter to count {0}", param), pe);
				}
			}
			return pt;
		}

		protected Collection<? extends T> doFilterP(final ParameterTree pt,
			Collection<T> coll) throws ParseException
		{
			final String c = pt.getContents();
			if (c.equalsIgnoreCase(ParameterTree.orString)
				|| c.equalsIgnoreCase(ParameterTree.andString))
			{
				final Set<T> a =
						new HashSet<T>(doFilterP(pt.getLeftTree(), coll));
				final Collection<? extends T> b =
						doFilterP(pt.getRightTree(), coll);
				if (c.equalsIgnoreCase(ParameterTree.orString))
				{
					a.addAll(b);
				}
				else
				{
					a.retainAll(b);
				}
				return a;
			}
			return filterSetP(c, coll);
		}

		@Override
		public Number count(PlayerCharacter pc, Object[] params)
			throws ParseException
		{
			final ParameterTree pt = convertParams(params);
			Collection<T> data = getData(pc);
			Collection<? extends T> results;
			if (pt == null)
			{
				results = data;	
			}
			else
			{
				results = doFilterP(pt, data);
			}
			return countData(results, pc);
		}

		protected Double countData(final Collection<? extends T> filtered,
			PlayerCharacter pc)
		{
			return (double) filtered.size();
		}

		protected abstract Collection<? extends T> filterSetP(String c,
			Collection<T> coll) throws ParseException;

	}

	public interface ObjectFilter<T>
	{
		public boolean accept(T o);
	}

	private static final void buildMap()
	{
		typeMap = new CaseInsensitiveMap<JepCountType>();
		Field[] fields = JepCountType.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			int mod = fields[i].getModifiers();
			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
					&& Modifier.isPublic(mod))
			{
				try
				{
					Object obj = fields[i].get(null);
					if (obj instanceof JepCountType)
					{
						typeMap.put(fields[i].getName(), (JepCountType) obj);
					}
				}
				catch (IllegalArgumentException e)
				{
					throw new UnreachableError(e);
				}
				catch (IllegalAccessException e)
				{
					throw new UnreachableError(e);
				}
			}
		}
	}


	/**
	 * Returns the constant for the given String (the search for the constant is
	 * case insensitive). If the constant does not already exist, an
	 * IllegalArgumentException is thrown.
	 * 
	 * @param name
	 *            The name of the constant to be returned
	 * @return The Constant for the given name
	 * @throws IllegalArgumentException
	 *             if the given String is not a previously defined JepCountType
	 */
	public static synchronized JepCountType valueOf(String name)
	{
		if (typeMap == null)
		{
			buildMap();
		}
		JepCountType key = typeMap.get(name);
		if (key == null)
		{
			throw new IllegalArgumentException(name
				+ " is not a previously defined JepCountType");
		}
		return key;
	}

	/**
	 * Returns a Collection of all of the Constants in this Class.
	 * 
	 * This collection maintains a reference to the Constants in this Class, so
	 * if a new Constant is created, the Collection returned by this method will
	 * be modified. (Beware of ConcurrentModificationExceptions)
	 * 
	 * @return a Collection of all of the Constants in this Class.
	 */
	public static synchronized Collection<JepCountType> getAllConstants()
	{
		if (typeMap == null)
		{
			buildMap();
		}
		return Collections.unmodifiableCollection(typeMap.values());
	}


}