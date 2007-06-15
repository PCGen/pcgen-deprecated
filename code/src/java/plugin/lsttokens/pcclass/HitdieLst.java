/*
 * Copyright 2005 (c) Devon Jones
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
package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import pcgen.base.formula.AddingFormula;
import pcgen.base.formula.DividingFormula;
import pcgen.base.formula.MultiplyingFormula;
import pcgen.base.formula.SubtractingFormula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.HitDieCommandFactory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.modifier.AbstractHitDieModifier;
import pcgen.cdom.modifier.HitDieFormula;
import pcgen.cdom.modifier.HitDieLock;
import pcgen.cdom.modifier.HitDieStep;
import pcgen.core.PCClass;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassLevelLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class HitdieLst extends AbstractToken implements PCClassLstToken,
		PCClassLevelLstToken
{

	@Override
	public String getTokenName()
	{
		return "HITDIE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.putHitDieLock(value, level);
		return true;
	}

	public boolean parse(LoadContext context, PCClass cl, String value,
		int level)
	{
		try
		{
			String lock = value;
			int pipeLoc = lock.indexOf(Constants.PIPE);
			if (pipeLoc != -1)
			{
				Logging.errorPrint(getTokenName() + " is invalid has a pipe: "
					+ value);
				return false;
			}
			AbstractHitDieModifier hdm;
			if (lock.startsWith("%/"))
			{
				// HITDIE:%/num --- divides the classes hit die by num.
				int denom = Integer.parseInt(lock.substring(2));
				if (denom <= 0)
				{
					Logging.errorPrint(getTokenName()
						+ " was expecting a Positive Integer "
						+ "for dividing Lock, was : " + lock.substring(2));
					return false;
				}
				hdm = new HitDieFormula(new DividingFormula(denom));
			}
			else if (lock.startsWith("%*"))
			{
				// HITDIE:%*num --- multiplies the classes hit die by num.
				int mult = Integer.parseInt(lock.substring(2));
				if (mult <= 0)
				{
					Logging.errorPrint(getTokenName()
						+ " was expecting a Positive "
						+ "Integer for multiplying Lock, was : "
						+ lock.substring(2));
					return false;
				}
				hdm = new HitDieFormula(new MultiplyingFormula(mult));
			}
			else if (lock.startsWith("%+"))
			{
				// possibly redundant with BONUS:HD MAX|num
				// HITDIE:%+num --- adds num to the classes hit die.
				int add = Integer.parseInt(lock.substring(2));
				if (add <= 0)
				{
					Logging
						.errorPrint(getTokenName()
							+ " was expecting a Positive "
							+ "Integer for adding Lock, was : "
							+ lock.substring(2));
					return false;
				}
				hdm = new HitDieFormula(new AddingFormula(add));
			}
			else if (lock.startsWith("%-"))
			{
				// HITDIE:%-num --- subtracts num from the classes hit die.
				// possibly redundant with BONUS:HD MAX|num if that will
				// take negative numbers.
				int sub = Integer.parseInt(lock.substring(2));
				if (sub <= 0)
				{
					Logging.errorPrint(getTokenName()
						+ " was expecting a Positive "
						+ "Integer for subtracting Lock, was : "
						+ lock.substring(2));
					return false;
				}
				hdm = new HitDieFormula(new SubtractingFormula(sub));
			}
			else if (lock.startsWith("%up"))
			{
				// HITDIE:%upnum --- moves the hit die num steps up the die size
				// list d4,d6,d8,d10,d12. Stops at d12.

				int steps = Integer.parseInt(lock.substring(3));
				if (steps <= 0)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
						+ getTokenName() + " up (must be positive)");
					return false;
				}
				if (steps >= 5)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
						+ getTokenName() + " up (too large)");
					return false;
				}

				hdm = new HitDieStep(steps, new HitDie(12));
			}
			else if (lock.startsWith("%Hup"))
			{
				// HITDIE:%upnum --- moves the hit die num steps up the die size
				// list d4,d6,d8,d10,d12. Stops at d12.

				int steps = Integer.parseInt(lock.substring(4));
				if (steps <= 0)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
						+ getTokenName());
					return false;
				}
				hdm = new HitDieStep(steps, null);
			}
			else if (lock.startsWith("%down"))
			{
				// HITDIE:%downnum --- moves the hit die num steps down the die
				// size
				// list d4,d6,d8,d10,d12. Stops at d4.

				int steps = Integer.parseInt(lock.substring(5));
				if (steps <= 0)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
						+ getTokenName() + " down (must be positive)");
					return false;
				}
				if (steps >= 5)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
						+ getTokenName() + " down (too large)");
					return false;
				}

				hdm = new HitDieStep(-steps, new HitDie(4));
			}
			else if (lock.startsWith("%Hdown"))
			{
				// HITDIE:%downnum --- moves the hit die num steps down the die
				// size
				// list. No limit.
				int steps = Integer.parseInt(lock.substring(6));
				if (steps <= 0)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
						+ getTokenName());
					return false;
				}
				hdm = new HitDieStep(-steps, null);
			}
			else
			{
				int i = Integer.parseInt(lock);
				if (i <= 0)
				{
					Logging.errorPrint("Invalid HitDie: " + i + " in "
						+ getTokenName());
					return false;
				}
				// HITDIE:num --- sets the hit die to num regardless of class.
				hdm = new HitDieLock(new HitDie(i));
			}

			PCClassLevel pcl = cl.getClassLevel(level);
			HitDieCommandFactory cf = new HitDieCommandFactory(pcl, hdm);
			pcl.put(ObjectKey.HITDIE, cf);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid Number in " + getTokenName() + ": "
				+ nfe.getLocalizedMessage());
			Logging.errorPrint("  Must be an Integer");
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc, int level)
	{
		PCClassLevel pcl = pcc.getClassLevel(level);
		GraphChanges<HitDieCommandFactory> changes =
				context.graph.getChangesFromToken(getTokenName(), pcl,
					HitDieCommandFactory.class);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (Iterator<LSTWriteable> it = added.iterator(); it.hasNext();)
		{
			StringBuilder sb = new StringBuilder();
			HitDieCommandFactory lcf = (HitDieCommandFactory) it.next();
			AbstractHitDieModifier mod = lcf.getModifier();
			sb.append(mod.getLSTform());
			String lcfString = lcf.getLSTformat();
			if (!lcfString.equals(Constants.LST_ALL))
			{
				sb.append("|CLASS");
				sb.append(lcfString.indexOf('=') == -1 ? '=' : '.');
				sb.append(lcfString);
			}
			list.add(sb.toString());
		}

		return list.toArray(new String[list.size()]);
	}
}
