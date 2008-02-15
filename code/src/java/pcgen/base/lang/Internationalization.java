/*
 * Copyright (c) Thomas Parker, 2007. 
 * derived from Globals.java Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.base.lang;

/**
 * This is like the top level model container. However, it is build from static
 * methods rather than instantiated.
 * 
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1650 $
 */
public final class Internationalization
{

	private Internationalization()
	{
		// Can't instantiate
	}

	private static String language = "en"; //$NON-NLS-1$

	private static String country = "US"; //$NON-NLS-1$

	/**
	 * Set Country
	 * 
	 * @param aString
	 */
	public static void setCountry(final String aString)
	{
		country = aString;
	}

	/**
	 * Get country
	 * 
	 * @return country
	 */
	public static String getCountry()
	{
		return country;
	}

	/**
	 * Set language
	 * 
	 * @param aString
	 */
	public static void setLanguage(final String aString)
	{
		language = aString;
	}

	/**
	 * Get language
	 * 
	 * @return language
	 */
	public static String getLanguage()
	{
		return language;
	}
}
