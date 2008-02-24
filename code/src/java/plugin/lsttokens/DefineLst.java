/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
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

import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.inst.CDOMStat;
import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class DefineLst implements GlobalLstToken, CDOMPrimaryToken<CDOMObject>
{

	private static final Class<CDOMStat> PCSTAT_CLASS = CDOMStat.class;

	public String getTokenName()
	{
		return "DEFINE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		final StringTokenizer tok = new StringTokenizer(value, "|");
		try
		{
			String varName = tok.nextToken();
			String defineFormula;
			if (varName.startsWith("UNLOCK."))
			{
				if (tok.hasMoreTokens())
				{
					Logging.log(Logging.LST_ERROR,
							"Cannot provide a value with DEFINE:UNLOCK. : "
									+ value);
					return false;
				}
				defineFormula = "";
			}
			else if (!tok.hasMoreTokens())
			{
				Logging.log(Logging.LST_ERROR,
						"Non UNLOCK DEFINE missing value. Fomrat should be DEFINE:var|value : "
								+ value);
				return false;
			}
			else
			{
				defineFormula = tok.nextToken();
			}
			obj.addVariable(anInt, varName, defineFormula);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		int barLoc = value.indexOf('|');
		if (barLoc != value.lastIndexOf('|'))
		{
			Logging
					.errorPrint(getTokenName()
							+ " must be of Format: varName|varFormula or LOCK.<stat>|value or UNLOCK.<stat>");
			return false;
		}
		if (barLoc == -1)
		{
			if (value.startsWith("UNLOCK."))
			{
				CDOMStat stat = context.ref.getAbbreviatedObject(
						PCSTAT_CLASS, value.substring(7));
				/*
				 * TODO Unlock the stat here
				 */
				return true;
			}
			else
			{
				Logging
						.errorPrint(getTokenName()
								+ " must be of Format: varName|varFormula or LOCK.<stat>|value or UNLOCK.<stat>");
				return false;
			}
		}
		else
		{
			
		}
		String var = value.substring(0, barLoc);
		if (var.length() == 0)
		{
			Logging.errorPrint("Empty Variable Name found in " + getTokenName()
					+ ": " + value);
			return false;
		}
		try
		{
			Formula f = FormulaFactory.getFormulaFor(value
					.substring(barLoc + 1));
			if (value.startsWith("LOCK."))
			{
				CDOMStat stat = context.ref.getAbbreviatedObject(
						PCSTAT_CLASS, value.substring(5, barLoc));
				/*
				 * TODO Lock the stat here
				 */
			}
			else
			{
				context.getObjectContext().put(obj,
						VariableKey.getConstant(var), f);
			}
			return true;
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint("Illegal Formula found in " + getTokenName()
					+ ": " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Set<VariableKey> keys = context.getObjectContext().getVariableKeys(obj);
		if (keys == null || keys.isEmpty())
		{
			return null;
		}
		TreeSet<String> set = new TreeSet<String>();
		for (VariableKey key : keys)
		{
			set.add(key.toString() + Constants.PIPE
					+ context.getObjectContext().getVariable(obj, key));
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
