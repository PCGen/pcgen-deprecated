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

package plugin.lsttokens.kit.skill;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.kit.CDOMKitSkill;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.kit.KitSkill;
import pcgen.persistence.lst.KitSkillLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;

/**
 * CLASS token for KitSkill
 */
public class ClassToken implements KitSkillLstToken,
		CDOMSecondaryToken<CDOMKitSkill>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "CLASS";
	}

	/**
	 * parse
	 * 
	 * @param kitSkill
	 *            KitSkill
	 * @param value
	 *            String
	 * @return boolean
	 */
	public boolean parse(KitSkill kitSkill, String value)
	{
		kitSkill.setClassName(value);
		return true;
	}

	public Class<CDOMKitSkill> getTokenClass()
	{
		return CDOMKitSkill.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitSkill kitSkill,
			String value)
	{
		CDOMSingleRef<CDOMPCClass> ref = context.ref.getCDOMReference(
				CDOMPCClass.class, value);
		kitSkill.setPcclass(ref);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitSkill kitSkill)
	{
		CDOMReference<CDOMPCClass> ref = kitSkill.getPcclass();
		if (ref == null)
		{
			return null;
		}
		return new String[] { ref.getLSTformat() };
	}
}
