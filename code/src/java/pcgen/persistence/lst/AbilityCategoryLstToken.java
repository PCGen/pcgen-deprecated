/*
 * AbilityCategoryLstToken.java
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
package pcgen.persistence.lst;

import pcgen.core.AbilityCategory;

/**
 * Interface for tokens that parse ABILITYCATEGORY line items.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public interface AbilityCategoryLstToken extends LstToken
{
	/**
	 * Parse an ABILITYCATEGORY token.
	 * 
	 * @param aCat The AbilityCategory object we are building
	 * @param aValue The token text
	 * 
	 * @return <tt>true</tt> if the token is valid.
	 */
	public abstract boolean parse(final AbilityCategory aCat,
		final String aValue);
}
