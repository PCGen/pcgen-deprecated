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
package plugin.lsttokens.pcclass;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMStat;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with BONUSSPELLSTAT Token
 */
public class BonusspellstatToken implements PCClassLstToken,
		CDOMPrimaryToken<CDOMPCClass>
{
	private static final Class<CDOMStat> PCSTAT_CLASS = CDOMStat.class;

	public String getTokenName()
	{
		return "BONUSSPELLSTAT";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setBonusSpellBaseStat(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMPCClass pcc, String value)
	{
		if (Constants.LST_NONE.equals(value))
		{
			context.getObjectContext().put(pcc, ObjectKey.HAS_BONUS_SPELL_STAT,
					Boolean.FALSE);
			return true;
		}
		context.getObjectContext().put(pcc, ObjectKey.HAS_BONUS_SPELL_STAT,
				Boolean.TRUE);
		/*
		 * TODO Does this consume DEFAULT in some way, so that it can set
		 * HAS_BONUS_SPELL_STAT to true, but not trigger the creation of
		 * BONUS_SPELL_STAT?
		 */
		CDOMStat pcs = context.ref.getAbbreviatedObject(PCSTAT_CLASS, value);
		if (pcs == null)
		{
			Logging.errorPrint("Invalid Stat Abbreviation in " + getTokenName()
					+ ": " + value);
			return false;
		}
		context.getObjectContext().put(pcc, ObjectKey.BONUS_SPELL_STAT, pcs);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMPCClass pcc)
	{
		Boolean bss = context.getObjectContext().getObject(pcc,
				ObjectKey.HAS_BONUS_SPELL_STAT);
		CDOMStat pcs = context.getObjectContext().getObject(pcc,
				ObjectKey.BONUS_SPELL_STAT);
		if (bss == null)
		{
			if (pcs != null)
			{
				context
						.addWriteMessage(getTokenName()
								+ " expected HAS_BONUS_SPELL_STAT to exist if BONUS_SPELL_STAT was defined");
			}
			return null;
		}
		if (bss.booleanValue())
		{
			if (pcs == null)
			{
				context
						.addWriteMessage(getTokenName()
								+ " expected BONUS_SPELL_STAT to exist since HAS_BONUS_SPELL_STAT was false");
				return null;
			}
			return new String[] { pcs.getLSTformat() };
		}
		else
		{
			return new String[] { "NONE" };
		}
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
