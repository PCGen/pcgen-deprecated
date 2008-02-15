/*
 * Comparators.java
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
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
 * Created on Feb 14, 2008, 11:23:02 PM
 */
package pcgen.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public final class Comparators
{

    private static final StringComparator tSC = new StringComparator();

    private static final StringIgnoreCaseComparator tSICC = new StringIgnoreCaseComparator();
    public static <T> Comparator<T> toStringComparator()
    {
	return tSC;
    }
    
    public static <T> Comparator<T> toStringIgnoreCaseComparator()
    {
	return tSICC;
    }

    /**
     * A <code>Comparator</code> to compare objects as
     * <code>String</code>s.  This is particularly useful for applications
     * such as maintaining a sorted <code>JComboBoxEx</code> and the like.
     *
     * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
     * @version $Revision: 2112 $
     */
    private static final class StringComparator<E> implements Comparator<E>, Serializable
    {

	/** Constructs a <code>StringComparator</code>. */
	public StringComparator()
	{
	// TODO: Exception needs to be handled
	}

	/** {@inheritDoc} */
	public int compare(E o1, E o2)
	{
	    // Treat null as the empty string.
	    return ((o1 == null) ? "" : o1.toString()).compareTo((o2 == null) ? ""
								 : o2.toString());
	}

    }

    /**
     * A <code>Comparator</code> to compare objects as
     * <code>String</code>s ignoring case.  This is particularly useful
     * for applications such as maintaining a sorted
     * <code>JComboBoxEx</code> and the like.
     *
     * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
     * @version $Revision: 2112 $
     */
    private static final class StringIgnoreCaseComparator<E> implements Comparator<E>, Serializable
    {

	/** Constructs a <code>StringIgnoreCaseComparator</code>. */
	public StringIgnoreCaseComparator()
	{
	// TODO: Exception needs to be handled
	}

	/** {@inheritDoc} */
	public int compare(E o1, E o2)
	{
	    // Treat null as the empty string.
	    return ((o1 == null) ? "" : o1.toString()).compareToIgnoreCase((o2 == null) ? "" : o2.toString());
	}

    }
}
