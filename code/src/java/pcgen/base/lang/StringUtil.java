/*
 * Copyright (c) Thomas Parker 2007
 *   derived from CoreUtility.java Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 */
package pcgen.base.lang;

import java.util.Collection;

/**
 * This class provides utility functions (similar to java.lang.Math) for use in
 * String manipulation.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public final class StringUtil
{

	private StringUtil()
	{
		// Do not instantiate
	}

	/**
	 * Concatenates the Collection of Strings into a String using the separator
	 * as the delimiter.
	 * 
	 * @param strings
	 *            An Collection of strings
	 * @param separator
	 *            The separating string
	 * @return A 'separator' separated String
	 */
	public static String join(final Collection<?> strings,
		final String separator)
	{
		return joinToStringBuffer(strings, separator).toString();
	}

	/**
	 * Concatenates the Collection of Strings into a StringBuffer using the
	 * separator as the delimiter.
	 * 
	 * @param strings
	 *            An Collection of strings
	 * @param separator
	 *            The separating character
	 * @return A 'separator' separated String
	 */
	public static StringBuilder joinToStringBuffer(final Collection<?> strings,
		final String separator)
	{
		if (strings == null)
		{
			return new StringBuilder();
		}

		final StringBuilder result = new StringBuilder(strings.size() * 10);

		boolean needjoin = false;

		for (Object obj : strings)
		{
			if (needjoin)
			{
				result.append(separator);
			}
			needjoin = true;
			result.append(obj.toString());
		}

		return result;
	}

	/**
	 * Replaces all of the instances of the find String with newStr in the
	 * (first) given String.
	 */
	public static String replaceAll(final String in, final String find,
		final String newStr)
	{
		final char[] working = in.toCharArray();
		final StringBuilder sb =
				new StringBuilder(in.length() + newStr.length());
		int startindex = in.indexOf(find);

		if (startindex < 0)
		{
			return in;
		}

		int currindex = 0;

		while (startindex > -1)
		{
			for (int i = currindex; i < startindex; ++i)
			{
				sb.append(working[i]);
			}

			currindex = startindex;
			sb.append(newStr);
			currindex += find.length();
			startindex = in.indexOf(find, currindex);
		}

		for (int i = currindex; i < working.length; ++i)
		{
			sb.append(working[i]);
		}

		return sb.toString();
	}

	/**
	 * Concatenates the Array of Strings into a String using the separator as
	 * the delimiter.
	 * 
	 * @param strings
	 *            An Array of strings
	 * @param separator
	 *            The separating string
	 * @return A 'separator' separated String
	 */
	public static String join(String[] strings, String separator)
	{
		if (strings == null)
		{
			return "";
		}

		final StringBuilder result = new StringBuilder(strings.length * 10);

		boolean needjoin = false;

		for (Object obj : strings)
		{
			if (needjoin)
			{
				result.append(separator);
			}
			needjoin = true;
			result.append(obj.toString());
		}

		return result.toString();
	}

}
