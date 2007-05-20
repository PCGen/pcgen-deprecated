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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SPELLSTAT Token
 */
public class SpellstatToken implements PCClassLstToken, PCClassClassLstToken
{

	private static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

	public String getTokenName()
	{
		return "SPELLSTAT";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setSpellBaseStat(value);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		PCStat pcs = context.ref.getConstructedCDOMObject(PCSTAT_CLASS, value);
		if (pcs == null)
		{
			Logging.errorPrint("Invalid Stat Abbreviation in Token + "
				+ getTokenName() + ": " + value);
			return false;
		}
		context.obj.put(pcc, ObjectKey.SPELL_STAT, pcs);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		PCStat pcs = context.obj.getObject(pcc, ObjectKey.SPELL_STAT);
		if (pcs == null)
		{
			return null;
		}
		return new String[]{pcs.getKeyName()};
	}
}
