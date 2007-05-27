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
package pcgen.persistence.lst.utils;

import java.util.Comparator;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.CategorizedCDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.DamageReduction;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.Aggregator;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.util.Logging;

public final class TokenUtilities
{

	public static final Comparator<LSTWriteable> WRITEABLE_SORTER =
			new Comparator<LSTWriteable>()
			{

				public int compare(LSTWriteable arg0, LSTWriteable arg1)
				{
					return compareWriteable(arg0, arg1);
				}
			};

	public static final Comparator<CDOMReference<?>> REFERENCE_SORTER =
			new Comparator<CDOMReference<?>>()
			{

				public int compare(CDOMReference<?> arg0, CDOMReference<?> arg1)
				{
					return compareRefs(arg0, arg1);
				}
			};

	public static final Comparator<CategorizedCDOMReference<?>> CAT_REFERENCE_SORTER =
			new Comparator<CategorizedCDOMReference<?>>()
			{

				public int compare(CategorizedCDOMReference<?> arg0,
					CategorizedCDOMReference<?> arg1)
				{
					if (arg0 instanceof CDOMSingleRef)
					{
						if (!(arg1 instanceof CDOMSingleRef))
						{
							return -1;
						}
						return arg0.getName().compareTo(arg1.getName());
					}
					if (arg1 instanceof CDOMSingleRef)
					{
						return 1;
					}
					return arg0.getName().compareTo(arg1.getName());
				}
			};

	public static final Comparator<Aggregator> AGG_COMPARATOR =
			new Comparator<Aggregator>()
			{

				public int compare(Aggregator arg0, Aggregator arg1)
				{
					int compSL =
							arg0.get(IntegerKey.START_LEVEL).compareTo(
								arg1.get(IntegerKey.START_LEVEL));
					if (compSL != 0)
					{
						return compSL;
					}
					int compLI =
							arg0.get(IntegerKey.LEVEL_INCREMENT).compareTo(
								arg1.get(IntegerKey.LEVEL_INCREMENT));
					if (compLI != 0)
					{
						return compLI;
					}
					int compC =
							arg0.get(IntegerKey.CONSECUTIVE).compareTo(
								arg1.get(IntegerKey.CONSECUTIVE));
					if (compC != 0)
					{
						return compC;
					}
					return arg0.get(IntegerKey.MAX_LEVEL).compareTo(
						arg1.get(IntegerKey.MAX_LEVEL));
				}
			};

	private TokenUtilities()
	{
		// Can't instantiate utility classes
	}

	public static <T extends PObject> CDOMReference<T> getTypeOrPrimitive(
		LoadContext context, Class<T> cl, String s)
	{
		if (s.startsWith(Constants.LST_TYPE_OLD)
			|| s.startsWith(Constants.LST_TYPE))
		{
			return getTypeReference(context, cl, s.substring(5));
		}
		else
		{
			return context.ref.getCDOMReference(cl, s);
		}
	}

	public static <T extends PObject> CDOMReference<T> getTypeReference(
		LoadContext context, Class<T> cl, String subStr)
	{
		if (subStr.length() == 0)
		{
			Logging.errorPrint("Type may not be empty in: " + subStr);
			return null;
		}
		if (subStr.charAt(0) == '.'
			|| subStr.charAt(subStr.length() - 1) == '.')
		{
			Logging
				.errorPrint("Type may not start or end with . in: " + subStr);
			return null;
		}
		String[] types = subStr.split("\\.");
		for (String type : types)
		{
			if (type.length() == 0)
			{
				Logging.errorPrint("Attempt to acquire empty Type in: "
					+ subStr);
				return null;
			}
		}
		return context.ref.getCDOMTypeReference(cl, types);
	}

	public static DamageReduction getDamageReduction(String drString)
	{
		int slashLoc = drString.indexOf('/');
		if (slashLoc == -1 || slashLoc != drString.lastIndexOf('/'))
		{
			Logging.errorPrint("Damage Reduction must be of Format: A/B");
			return null;
		}
		Formula f =
				FormulaFactory.getFormulaFor(drString.substring(0, slashLoc));
		DamageReduction dr =
				new DamageReduction(f, drString.substring(slashLoc + 1));
		return dr;
	}

	public static <T extends PObject & CategorizedCDOMObject<T>> CDOMReference<T> getTypeOrPrimitive(
		LoadContext context, Class<T> cl, Category<T> cat, String s)
	{
		if (s.startsWith(Constants.LST_TYPE_OLD)
			|| s.startsWith(Constants.LST_TYPE))
		{
			String subStr = s.substring(5);
			if (subStr.length() == 0)
			{
				Logging.errorPrint("Type may not be empty in: " + s);
				return null;
			}
			if (subStr.charAt(0) == '.'
				|| subStr.charAt(subStr.length() - 1) == '.')
			{
				Logging.errorPrint("Type may not start or end with . in: " + s);
				return null;
			}
			String[] types = subStr.split("\\.");
			for (String type : types)
			{
				/*
				 * TODO May be faster to just look for .. in the input string -
				 * make sure to do this in the uncategorized version too.
				 */
				if (type.length() == 0)
				{
					Logging
						.errorPrint("Attempt to acquire empty Type in: " + s);
					return null;
				}
			}
			return context.ref.getCDOMTypeReference(cl, cat, types);
		}
		else
		{
			return context.ref.getCDOMReference(cl, cat, s);
		}
	}

	public static int compareRefs(CDOMReference<?> arg0, CDOMReference<?> arg1)
	{
		if (arg0 instanceof CDOMSingleRef)
		{
			if (!(arg1 instanceof CDOMSingleRef))
			{
				return -1;
			}
			return arg0.getName().compareTo(arg1.getName());
		}
		if (arg1 instanceof CDOMSingleRef)
		{
			return 1;
		}
		return arg0.getName().compareTo(arg1.getName());
	}

	public static int compareWriteable(LSTWriteable arg0, LSTWriteable arg1)
	{
		if (arg0 instanceof CDOMSingleRef || arg0 instanceof CDOMObject)
		{
			if (!(arg1 instanceof CDOMSingleRef || arg1 instanceof CDOMObject))
			{
				return -1;
			}
			return compareLSTformats(arg0, arg1);
		}
		if (arg1 instanceof CDOMSingleRef)
		{
			return 1;
		}
		/*
		 * BUG TODO This is NOT Consistent with equals :(
		 */
		return compareLSTformats(arg0, arg1);
	}

	private static int compareLSTformats(LSTWriteable arg0, LSTWriteable arg1)
	{
		String base = arg0.getLSTformat();
		if (base == null)
		{
			if (arg1.getLSTformat() == null)
			{
				return 0;
			}
			else
			{
				return -1;
			}
		}
		else
		{
			if (arg1.getLSTformat() == null)
			{
				return 1;
			}
			else
			{
				return base.compareTo(arg1.getLSTformat());
			}
		}
	}
}
