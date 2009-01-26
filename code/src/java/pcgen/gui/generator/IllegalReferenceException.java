/*
 * IllegalReferenceException.java
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jan 22, 2009, 6:14:36 PM
 */
package pcgen.gui.generator;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class IllegalReferenceException extends Exception
{

    /**
     * Creates a new instance of <code>IllegalReferenceException</code> without detail message.
     */
    public IllegalReferenceException()
    {
    }

    /**
     * Constructs an instance of <code>IllegalReferenceException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public IllegalReferenceException(String msg)
    {
        super(msg);
    }

    public IllegalReferenceException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

}
