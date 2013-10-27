/*
 * CategoryToken.java
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package plugin.lsttokens.gamemode.abilitycategory;

import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.AbilityCategory;
import pcgen.persistence.lst.AbilityCategoryLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Handles the CATEGORY token on an ABILITYCATEGORY line.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class CategoryToken extends AbstractToken implements
		AbilityCategoryLstToken, CDOMPrimaryToken<CDOMAbilityCategory>
{

	private static final Class<CDOMAbilityCategory> AC_CLASS = CDOMAbilityCategory.class;

	/**
	 * @see pcgen.persistence.lst.AbilityCategoryLstToken#parse(pcgen.core.AbilityCategory,
	 *      java.lang.String)
	 */
	public boolean parse(final AbilityCategory aCat, final String aValue)
	{
		aCat.setAbilityCategory(aValue);
		return true;
	}

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "CATEGORY"; //$NON-NLS-1$
	}

	public boolean parse(LoadContext context, CDOMAbilityCategory eq,
			String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext().put(eq, ObjectKey.PARENT_CATEGORY,
				context.ref.getCDOMReference(AC_CLASS, value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMAbilityCategory eq)
	{
		CDOMSingleRef<CDOMAbilityCategory> ref = context.getObjectContext()
				.getObject(eq, ObjectKey.PARENT_CATEGORY);
		if (ref == null)
		{
			return null;
		}
		return new String[] { ref.getLSTformat() };
	}

	public Class<CDOMAbilityCategory> getTokenClass()
	{
		return AC_CLASS;
	}
}
