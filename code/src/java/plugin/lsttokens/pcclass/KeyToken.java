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
 * Current Ver: $Revision: 3841 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-08-20 21:36:19 -0400 (Mon, 20 Aug 2007) $
 */
package plugin.lsttokens.pcclass;

import java.util.List;

import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMSubClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * @author djones4
 * 
 */
public class KeyToken implements CDOMPrimaryToken<CDOMPCClass>
{

	public String getTokenName()
	{
		return "KEY";
	}

	public boolean parse(LoadContext context, CDOMPCClass pcc, String value)
	{
		// THIS IS ORDER DEPENDENT, MUST BE DONE BEFORE resetting the key
		context.ref.reassociateKey(value, (pcc));
		/*
		 * TODO This actually needs to be special - since the Key is the lookup
		 * method FUTURE isn't this redundant with the set above?!
		 */
		context.getObjectContext().put(pcc, StringKey.KEY_NAME, value);
		/*
		 * TODO This bypasses a but when using the editor context :P
		 */
		pcc.put(StringKey.KEY_NAME, value);
		/*
		 * Note the additional work done here for PCClass, because the PCClass
		 * key is used as a CATEGORY for SubClasses.
		 */
		List<CDOMSubClass> subclasses = pcc.getCDOMSubClassList();
		if (subclasses != null)
		{
			SubClassCategory scc = SubClassCategory.getConstant(value);
			for (CDOMSubClass sc : subclasses)
			{
				context.ref.reassociateCategory(scc, sc);
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMPCClass pcc)
	{
		/*
		 * TODO more appropriate to grab KEY_NAME! (in case the key was set it
		 * should stay set)
		 */
		String key = pcc.getKeyName();
		String display = pcc.getDisplayName();
		if (key.equals(display))
		{
			return null;
		}
		return new String[]{key};
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
