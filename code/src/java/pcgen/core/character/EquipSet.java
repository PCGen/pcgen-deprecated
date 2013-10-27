/*
 * EquipSet.java
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
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on April 29th, 2002, 11:26 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core.character;

import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Logging;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.bonus.BonusObj;

/*
 ******    ***   ******    ******   ****   *****
 **   **  ** **  **   **   **   ** **  ** **
 ******  **   *<code>EquipSet</code>*  ** **
 **   ** ******* **   **   **   ** **  ** **  ***
 **   ** **   ** **   **   **   ** **  ** **   **
 ******  **   ** ******    ******   ****   ******
 *
 */

/**
 * <code>EquipSet.java</code>
 * @author Jayme Cox <jaymecox@excite.com>
 * @version $Revision$
 */
public final class EquipSet implements Comparable<EquipSet>, Cloneable
{
	/** The ID component for the Root equip set */
	public static final String ROOT_ID = "0"; //$NON-NLS-1$
	
	/** The character to use to separate path components */
	public static final String PATH_SEPARATOR = "."; //$NON-NLS-1$
	
	private Equipment eq_item;
	private Float qty = new Float(1);
	private List<BonusObj> tempBonusList = new ArrayList<BonusObj>();

	/*
	 * the Structure of each EQUIPSET is as follows:
	 *
	 * EQUIPSET: id_path : name : value : item
	 *
	 * id_path = a . delimited string that denotes parent/child relationship
	 * name = name of EquipSet or item this represents
			  (and is used to define uniquiness for compareTo)
	 * value = Name of the Equipment stored in this item
	 * item = Equipment item stored (optional)
	 * qty = number of items this equipset contains (all same item)
	 *
	 */

	//
	// id_path for a "root" EquipSet looks like: 0.1
	// where
	// 0 == my parent (none)
	// 1 == my Id
	//
	// a Child id_path looks like this: 0.1.3
	// where
	// 0 == root
	// 1 == my parent
	// 3 == my Id
	//
	private String id_path = Constants.EMPTY_STRING;
	private String name = Constants.EMPTY_STRING;
	private String note = Constants.EMPTY_STRING;
	private String value = Constants.EMPTY_STRING;
	private boolean useTempBonuses = true;

	/**
	 * Constructor
	 * @param id
	 * @param aName
	 */
	public EquipSet(final String id, final String aName)
	{
		id_path = id;
		name = aName;
	}

	/**
	 * Constructor
	 * @param id
	 * @param aName
	 * @param aValue
	 * @param item
	 */
	public EquipSet(final String id, final String aName, final String aValue, final Equipment item)
	{
		id_path = id;
		name = aName;
		value = aValue;
		eq_item = item;
	}

	/**
	 * our Id is the last number on the id_path
	 * if id_path is "0.2.8.15", our id is 15
	 * @return id
	 **/
	public int getId()
	{
		int id = 0;

		try
		{
			final StringTokenizer aTok = new StringTokenizer(id_path, PATH_SEPARATOR, false);

			while (aTok.hasMoreTokens())
			{
				id = Integer.parseInt(aTok.nextToken());
			}
		}
		catch (NullPointerException e)
		{
			Logging.errorPrint("Error in EquipSet.getId", e);
		}

		return id;
	}

	/**
	 * Set ID Path
	 * @param x
	 */
	public void setIdPath(final String x)
	{
		id_path = x;
	}

	/**
	 * Get Id Path
	 * @return id_path
	 */
	public String getIdPath()
	{
		return id_path;
	}

	/**
	 * Set Item
	 * @param item
	 */
	public void setItem(final Equipment item)
	{
		eq_item = item;
	}

	/**
	 * Get item
	 * @return eq_item
	 */
	public Equipment getItem()
	{
		return eq_item;
	}

	/**
	 * Set name
	 * @param x
	 */
	public void setName(final String x)
	{
		name = x;
	}

	/**
	 * name is our EquipSet name if we are a root node
	 * or it is the name of the location for the equipment we are holding
	 * @return name
	 **/
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the player added note to aString
	 * @param aString
	 **/
	public void setNote(final String aString)
	{
		note = aString;
	}

	/**
	 * Get note
	 * @return note
	 */
	public String getNote()
	{
		return note;
	}

	/**
	 * the Parent Id Path is everything except our Id
	 * if id_path is "0.2.8.15", our Parent Id is "0.2.8"
	 * @return parent id path
	 **/
	public String getParentIdPath()
	{
		final StringBuffer buf = new StringBuffer(50);

		// get all tokens and include the delimiter
		try
		{
			final StringTokenizer aTok = new StringTokenizer(id_path, PATH_SEPARATOR, true);

			// get all tokens (and delimiters) except last two
			for (int i = aTok.countTokens() - 2; i > 0; i--)
			{
				buf.append(aTok.nextToken());
			}
		}
		catch (NullPointerException e)
		{
			Logging.errorPrint("Error in EquipSet.getParentIdPath", e);
		}

		return buf.toString();
	}

	/**
	 * Set's the number of items in this equipset
	 * @param x
	 **/
	public void setQty(final Float x)
	{
		qty = x;
	}

	/**
	 * Get quantity
	 * @return quantity
	 */
	public Float getQty()
	{
		return qty;
	}

	/**
	 * return the root id of the EquipSet
	 * If our id_path is "0.2.8.15", the root would be "0.2"
	 * @return root id path
	 **/
	public String getRootIdPath()
	{
		final StringBuffer buf = new StringBuffer(50);
		final StringTokenizer aTok = new StringTokenizer(id_path, PATH_SEPARATOR, false);
		final String result;

		if (aTok.countTokens() < 2)
		{
			result = Constants.EMPTY_STRING;
		}
		else
		{
			// get first two tokens and delimiter
			buf.append(aTok.nextToken());
			buf.append('.');
			buf.append(aTok.nextToken());

			result = buf.toString();
		}

		return result;
	}

	/**
	 * Set temp bonus list
	 * @param aList
	 */
	public void setTempBonusList(final List<BonusObj> aList)
	{
		tempBonusList = aList;
	}

	/**
	 * a List of BonusObj's
	 * @return temp bonus list
	 **/
	public List<BonusObj> getTempBonusList()
	{
		return tempBonusList;
	}

	/**
	 * Should apply temporary bonuses to this equipset?
	 * @param aBool
	 **/
	public void setUseTempMods(final boolean aBool)
	{
		useTempBonuses = aBool;
	}

	/**
	 * Return TRUE if using temp mods
	 * @return TRUE if using temp mods
	 */
	public boolean getUseTempMods()
	{
		return useTempBonuses;
	}

	/**
	 * Set value
	 * @param x
	 */
	public void setValue(final String x)
	{
		value = x;
	}

	/**
	 * value is null for root nodes or
	 * it is the name of the piece of equipment we are holding
	 * @return value
	 **/
	public String getValue()
	{
		return value;
	}

	/**
	 * Clear the temp bonus list
	 */
	public void clearTempBonusList()
	{
		tempBonusList.clear();
	}

	/**
	 * Creates a duplicate of this equip set. Note that this is
	 * a deep clone - all equipment associated with this EquipSet
	 * will also be cloned.
	 *
	 * @return A new equip set, identical to this one.
	 */
	@Override
	public Object clone()
	{
		EquipSet eqSet = null;

		try
		{
			eqSet = (EquipSet) super.clone();

			if (eq_item != null)
			{
				eqSet.eq_item = eq_item.clone();
			}

			if (qty != null)
			{
				eqSet.qty = new Float(qty.floatValue());
			}
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		return eqSet;
	}

	/**
	 * Compares the path ids of each object to determine relative order.
	 * 
	 * @param obj The EquipSet to compare with.
	 *  
	 * @return a negative integer, zero, or a positive integer as this EquipSet 
	 * is less than, equal to, or greater than the specified EquipSet.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final EquipSet obj)
	{
		return id_path.compareToIgnoreCase(obj.id_path);
	}

	/**
	 * Returns the EquipSet name.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * true if temp bonus list is not empty
	 * @return true if temp bonus list is not empty
	 */
	public boolean useTempBonusList()
	{
		return !tempBonusList.isEmpty();
	}
}
