/* 
 * ClassToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.clazz;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.kit.CDOMKitClass;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.kit.KitClass;
import pcgen.persistence.lst.KitClassLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * CLASS token for KitClass
 */
public class ClassToken implements KitClassLstToken,
		CDOMSecondaryToken<CDOMKitClass>
{

	public boolean parse(KitClass kitClass, String value)
	{
		Logging.errorPrint("Ignoring second CLASS tag \"" + value
				+ "\" in Kit.");
		return false;
	}

	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "CLASS";
	}

	public Class<CDOMKitClass> getTokenClass()
	{
		return CDOMKitClass.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitClass kitClass, String value)
	{
		CDOMSingleRef<CDOMPCClass> ref = context.ref.getCDOMReference(
				CDOMPCClass.class, value);
		kitClass.setPcclass(ref);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitClass kitClass)
	{
		CDOMReference<CDOMPCClass> ref = kitClass.getPcclass();
		if (ref == null)
		{
			return null;
		}
		return new String[] { ref.getLSTformat() };
	}
}
