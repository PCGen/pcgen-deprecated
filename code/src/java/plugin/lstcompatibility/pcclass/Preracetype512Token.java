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
 * Current Ver: $Revision: 2700 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-04-07 23:56:55 -0400 (Sat, 07 Apr 2007) $
 */
package plugin.lstcompatibility.pcclass;

import pcgen.cdom.inst.CDOMPCClass;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.util.Logging;

/**
 * Class deals with PRERACETYPE Token
 */
public class Preracetype512Token extends AbstractToken implements
		PCClassLstToken, CDOMCompatibilityToken<CDOMPCClass>
{

	@Override
	public String getTokenName()
	{
		return "PRERACETYPE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setPreRaceType(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMPCClass pcc, String value)
	{
		try
		{
			context.obj.put(pcc, context.getPrerequisite("RACE", "1,RACETYPE="
					+ value));
			return true;
		}
		catch (PersistenceLayerException ple)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Invalid conversion from PRERACETYPE to PRERACE for "
							+ value + " " + ple.getLocalizedMessage());
			return false;
		}
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
