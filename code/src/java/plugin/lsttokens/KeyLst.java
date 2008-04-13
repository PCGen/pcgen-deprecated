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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * @author djones4
 * 
 */
public class KeyLst implements GlobalLstToken, CDOMPrimaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "KEY";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		obj.setKeyName(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		// THIS IS ORDER DEPENDENT, MUST BE DONE BEFORE resetting the key
		context.ref.reassociateKey(value, obj);
		/*
		 * TODO This actually needs to be special - since the Key is the lookup
		 * method FUTURE isn't this redundant with the set above?!
		 */
		context.getObjectContext().put(obj, StringKey.KEY_NAME, value);
		/*
		 * TODO This bypasses a but when using the editor context :P
		 */
		obj.put(StringKey.KEY_NAME, value);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		String key = context.getObjectContext().getString(obj,
				StringKey.KEY_NAME);
		if (key == null)
		{
			return null;
		}
		return new String[] { key };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
