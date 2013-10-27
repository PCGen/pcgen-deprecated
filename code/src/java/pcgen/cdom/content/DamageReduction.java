/*
 * DamageReduction.java Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on March 16, 2006
 * 
 * Current Ver: $Revision: $ Last Editor: $Author: $ Last Edited: $Date: $
 */
package pcgen.cdom.content;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.ConcretePrereqObject;

/**
 * Encapsulates a single DR entity. This class encapsulates a DR entity and
 * provides utility methods to manipulate and combine multiple DRs together. The
 * consensus seems to be that brievity over clarity is preferred in the output
 * so that is what the methods attempt to provide.
 * 
 * @author boomer70
 * 
 */
public class DamageReduction extends ConcretePrereqObject
{

	private final Formula reduction;

	private final String theBypass;

	/**
	 * Constructs a DamageReduction object. The reduction is stored as a string
	 * to allow use of JEP formulas and variables.
	 * 
	 * @param aReduction
	 *            The reduction to set
	 * @param aBypass
	 *            The bypass type to set.
	 */
	public DamageReduction(Formula aReduction, String aBypass)
	{
		super();
		reduction = aReduction;
		theBypass = aBypass;
	}

	/**
	 * Gets the String representation of the amount of damage this DR reduces.
	 * 
	 * @return Returns the amount of reduction.
	 */
	public Formula getReduction()
	{
		return reduction;
	}

	/**
	 * Gets the string of damage types that bypass this DR.
	 * 
	 * @return Returns the bypass.
	 */
	public String getBypass()
	{
		return theBypass;
	}

	/**
	 * Returns a String representation of this DamageReduction object.
	 * 
	 * @return String
	 */
	@Override
	public String toString()
	{
		return reduction + "/" + theBypass;
	}

	/**
	 * Tests if two DR objects are the same. The test checks that all bypasses
	 * are present in any order and that the values are the same
	 * 
	 * @param other
	 *            The DR to test against.
	 * @return true if the DRs are the same.
	 */
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof DamageReduction)
		{
			DamageReduction othDR = (DamageReduction) other;
			return reduction.equals(othDR.reduction)
				&& theBypass.equals(othDR.theBypass);
			/*
			 * FIXME TODO Equals MUST test the prerequisites, too!!!
			 */
		}
		return false;
	}

	/**
	 * Returns a hash code to use for this object. This method is overridden to
	 * return the same hashcode for the same DR object. That is if
	 * dr.equals(dr1) the hashcodes must be the same
	 * 
	 * @return A hashcode
	 */
	@Override
	public int hashCode()
	{
		/*
		 * FIXME TODO hashCode should test the prerequisites, too!!!
		 */
		return reduction.hashCode() ^ theBypass.hashCode();
	}

	public static DamageReduction getDamageReduction(String drString)
	{
		int slashLoc = drString.indexOf('/');
		if (slashLoc == -1 || slashLoc != drString.lastIndexOf('/'))
		{
			/*
			 * TODO How to report this error IAE?
			 */
			throw new IllegalArgumentException(
				"DamageReduction must be of Form: reduction/bypass");
		}
		Formula f =
				FormulaFactory.getFormulaFor(drString.substring(0, slashLoc));
		DamageReduction dr =
				new DamageReduction(f, drString.substring(slashLoc + 1));
		return dr;
	}

}
