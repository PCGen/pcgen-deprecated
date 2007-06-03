/*
 * PrereqObject.java Copyright 2007 Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2006 Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Version: $Revision: 1434 $ Last Editor: $Author: $ Last Edited:
 * $Date: 2006-09-27 22:42:05 -0400 (Wed, 27 Sep 2006) $
 * 
 */
package pcgen.cdom.base;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.base.lang.UnreachableError;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;

/**
 * This class implements support for prerequisites for an object.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 */
public class ConcretePrereqObject implements PrereqObject, RestrictedObject,
		Cloneable
{
	/**
	 * The list of prerequisites
	 */
	private List<Prerequisite> thePrereqs = null;

	/**
	 * Add a <tt>Prerequesite</tt> to the prerequisite list.
	 * 
	 * @param preReq
	 *            The prerequisite to add.
	 */
	public final void addPrerequisite(Prerequisite preReq)
	{
		if (preReq == null)
		{
			return;
		}
		if (Prerequisite.CLEAR_KIND.equals(preReq.getKind()))
		{
			thePrereqs = null;
		}
		else
		{
			if (thePrereqs == null)
			{
				thePrereqs = new ArrayList<Prerequisite>();
			}
			thePrereqs.add(preReq);
		}
	}

	// TODO FACADE
	public final void addPreReq(Prerequisite preReq)
	{
		addPrerequisite(preReq);
	}

	/**
	 * Adds a <tt>Collection</tt> of <tt>Prerequisite</tt> objects.
	 * 
	 * @param prereqs
	 *            A <tt>Collection</tt> of <tt>Prerequisite</tt> objects.
	 */
	public void addAllPrerequisites(final Collection<Prerequisite> prereqs)
	{
		if (prereqs == null)
		{
			return;
		}
		if (thePrereqs == null)
		{
			thePrereqs = new ArrayList<Prerequisite>(prereqs.size());
		}
		for (final Prerequisite pre : prereqs)
		{
			addPrerequisite(pre);
		}
	}

	public void addAllPrerequisites(Prerequisite... prereqs)
	{
		if (prereqs == null)
		{
			return;
		}
		if (thePrereqs == null)
		{
			thePrereqs = new ArrayList<Prerequisite>(prereqs.length);
		}
		for (final Prerequisite pre : prereqs)
		{
			addPrerequisite(pre);
		}
	}

	/**
	 * Get the list of <tt>Prerequesite</tt>s.
	 * 
	 * @return An unmodifiable <tt>List</tt> of <tt>Prerequesite</tt>s or
	 *         <tt>
	 * null</tt> if no prerequisites have been set.
	 */
	public List<Prerequisite> getPrerequisiteList()
	{
		if (thePrereqs == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(thePrereqs);
	}

	// TODO FACADE
	public List<Prerequisite> getPreReqList()
	{
		return getPrerequisiteList();
	}

	/**
	 * Clear the prerequisite list.
	 */
	public final void clearPrerequisiteList()
	{
		thePrereqs = null;
	}

	// TODO FACADE
	public final void clearPreReq()
	{
		clearPrerequisiteList();
	}

	/**
	 * Tests to see if this object has any prerequisites associated with it.
	 * 
	 * @return <tt>true</tt> if it has prereqs
	 */
	public boolean hasPrerequisites()
	{
		return thePrereqs != null;
	}

	// TODO FACADE
	public boolean hasPreReqs()
	{
		return thePrereqs != null;
	}

	/**
	 * Gets the number of prerequisites currently associated.
	 * 
	 * @return the number of prerequesites
	 */
	public final int getPrerequisiteCount()
	{
		if (thePrereqs == null)
		{
			return 0;
		}
		return thePrereqs.size();
	}

	// TODO FACADE
	public final int getPreReqCount()
	{
		return getPrerequisiteCount();
	}

	/**
	 * Set the prerequisite list to the same prerequisites as the given
	 * PrereqObject.
	 * 
	 * @param prereqObject
	 *            The PrereqObject from which to copy the Prerequisites.
	 */
	public void setPrerequisiteListFrom(PrereqObject prereqObject)
	{
		if (prereqObject.getClass().equals(ConcretePrereqObject.class))
		{
			ConcretePrereqObject pro = (ConcretePrereqObject) prereqObject;
			thePrereqs = new ArrayList<Prerequisite>(pro.thePrereqs.size());
			try
			{
				for (Prerequisite element : pro.thePrereqs)
				{
					thePrereqs.add(element.clone());
				}
			}
			catch (CloneNotSupportedException cnse)
			{
				throw new UnreachableError(
					"Code assumes Prerequisite is Cloneable", cnse);
			}
		}
	}

	/**
	 * Returns true if this object has any prerequisites of the kind that is
	 * passed in.
	 * 
	 * @param matchType
	 *            The kind of Prerequisite to test for.
	 * 
	 * @return <tt>true</tt> if this object has a prerequisite of the kind
	 *         that is passed in
	 * 
	 * @see pcgen.core.prereq.Prerequisite#getKind()
	 */
	public final boolean hasPrerequisiteOfType(final String matchType)
	{
		if (thePrereqs == null)
		{
			return false;
		}

		for (Prerequisite prereq : getPrerequisiteList())
		{
			if (matchType == null && prereq.getKind() == null)
			{
				return true;
			}
			if (matchType != null
				&& matchType.equalsIgnoreCase(prereq.getKind()))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ConcretePrereqObject clone() throws CloneNotSupportedException
	{
		final ConcretePrereqObject obj = (ConcretePrereqObject) super.clone();
		if (thePrereqs != null)
		{
			obj.thePrereqs = new ArrayList<Prerequisite>(thePrereqs);
		}
		return obj;
	}

	// TODO FACADE, to be moved (inlined?)

	/**
	 * Returns the pre requesites as an HTML String
	 * 
	 * @param aPC
	 * @return the pre requesites as an HTML String
	 */
	public final String preReqHTMLStrings(final PlayerCharacter aPC)
	{
		return PrerequisiteUtilities.preReqHTMLStringsForList(aPC, null,
			thePrereqs, true);
	}

	/**
	 * Returns the pre requesites as an HTML String with a header
	 * 
	 * @param aPC
	 * @param includeHeader
	 * @return the pre requesites as an HTML String
	 */
	public String preReqHTMLStrings(final PlayerCharacter aPC,
		final boolean includeHeader)
	{
		return PrerequisiteUtilities.preReqHTMLStringsForList(aPC, null,
			thePrereqs, includeHeader);
	}

	/**
	 * Returns the pre requesites as an HTML String given an object
	 * 
	 * @param aPC
	 * @param p
	 * @return the pre requesites as an HTML String given an object
	 */
	public final String preReqHTMLStrings(final PlayerCharacter aPC,
		final PObject p)
	{
		return PrerequisiteUtilities.preReqHTMLStringsForList(aPC, p,
			thePrereqs, true);
	}

	/**
	 * Creates the requirement string for printing.
	 * 
	 * @return the requirement string for printing
	 */
	public final String preReqStrings()
	{
		return PrereqHandler.toHtmlString(thePrereqs);
	}

	/** TODO This is rather foobar'd */
	public final boolean passesPreReqToGain(final Equipment p,
		final PlayerCharacter aPC)
	{
		if (!hasPreReqs())
		{
			return true;
		}

		return PrereqHandler.passesAll(thePrereqs, p, aPC);
	}

	/**
	 * Adds the prerequisites to the <tt>Collection</tt> passed in.
	 * 
	 * @param A
	 *            <tt>Collection</tt> to add to.
	 */
	public final void addPreReqTo(final Collection<Prerequisite> collection)
	{
		if (thePrereqs != null)
		{
			collection.addAll(thePrereqs);
		}
	}

	/**
	 * Returns true if this object has any prerequisites of the kind that is
	 * passed in.
	 * 
	 * @param matchType
	 *            The kind of Prerequisite to test for.
	 * 
	 * @return <tt>true</tt> if this object has a prerequisite of the kind
	 *         that is passed in
	 * 
	 * @see pcgen.core.prereq.Prerequisite#getKind()
	 */
	public final boolean hasPreReqTypeOf(final String matchType)
	{
		if (!hasPreReqs())
		{
			return false;
		}

		for (Prerequisite prereq : getPreReqList())
		{
			if (prereq != null)
			{
				if (matchType == null && prereq.getKind() == null)
				{
					return true;
				}
				if (matchType.equalsIgnoreCase(prereq.getKind()))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Add a <tt>Prerequesite</tt> to the prereq list with a level qualifier.
	 * 
	 * <p>
	 * If the Prerequisite kind is &quot;clear&quot; all the prerequisites will
	 * be cleared from the list.
	 * 
	 * @param preReq
	 *            The <tt>Prerequisite</tt> to add.
	 * @param levelQualifier
	 *            A level qualifier.
	 * 
	 * @see pcgen.core.prereq.Prerequisite#setLevelQualifier(int)
	 */
	public final void addPreReq(final Prerequisite preReq,
		final int levelQualifier)
	{
		if (preReq == null)
		{
			return;
		}
		if (Prerequisite.CLEAR_KIND.equals(preReq.getKind())) //$NON-NLS-1$
		{
			thePrereqs = null;
		}
		else
		{
			if (thePrereqs == null)
			{
				thePrereqs = new ArrayList<Prerequisite>();
			}
			if (levelQualifier > 0)
			{
				preReq.setLevelQualifier(levelQualifier);
			}
			thePrereqs.add(preReq);
		}
	}

	/**
	 * Tests if the specified PlayerCharacter passes all the prerequisites.
	 * 
	 * @param aPC
	 *            The <tt>PlayerCharacter</tt> to test.
	 * 
	 * @return <tt>true</tt> if the PC passes all the prerequisites.
	 */
	public boolean qualifies(final PlayerCharacter aPC)
	{
		if (thePrereqs == null)
		{
			return true;
		}

		return PrereqHandler.passesAll(thePrereqs, aPC, null);
	}

	/**
	 * Returns the prerequisites in &quot;PCC&quot; format.
	 * 
	 * @return A string in &quot;PCC&quot; format or an empty string.
	 */
	public String getPCCText()
	{
		if (thePrereqs == null)
		{
			return Constants.EMPTY_STRING;
		}

		final StringWriter writer = new StringWriter();
		for (final Prerequisite prereq : thePrereqs)
		{
			final PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
			writer.write(Constants.PIPE);
			try
			{
				prereqWriter.write(writer, prereq);
			}
			catch (PersistenceLayerException e)
			{
				e.printStackTrace();
			}
		}
		return writer.toString();
	}

	private List<Restriction<?>> sinkRes = null;

	public boolean addSinkRestriction(Restriction<?> r)
	{
		if (sinkRes == null)
		{
			sinkRes = new ArrayList<Restriction<?>>();
		}
		return sinkRes.add(r);
	}

	public boolean hasSinkRestrictions()
	{
		return sinkRes != null && !sinkRes.isEmpty();
	}

	public List<Restriction<?>> getSinkRestrictions()
	{
		return sinkRes == null ? null : Collections.unmodifiableList(sinkRes);
	}

	private List<Restriction<?>> sourceRes = null;

	public boolean addSourceRestriction(Restriction<?> r)
	{
		if (sourceRes == null)
		{
			sourceRes = new ArrayList<Restriction<?>>();
		}
		return sourceRes.add(r);
	}

	public boolean hasSourceRestrictions()
	{
		return sourceRes != null && !sourceRes.isEmpty();
	}

	public List<Restriction<?>> getSourceRestrictions()
	{
		return sourceRes == null ? null : Collections
			.unmodifiableList(sourceRes);
	}

	public boolean equalsPrereqObject(PrereqObject other)
	{
		if (this == other)
		{
			return true;
		}
		boolean otherHas = other.hasPrerequisites();
		if (!hasPrerequisites())
		{
			return !otherHas;
		}
		if (!otherHas)
		{
			return false;
		}
		List<Prerequisite> otherPRL = other.getPrerequisiteList();
		if (otherPRL.size() != thePrereqs.size())
		{
			return false;
		}
		ArrayList<Prerequisite> removed =
				new ArrayList<Prerequisite>(thePrereqs);
		removed.removeAll(otherPRL);
		return removed.isEmpty();
	}

	// TODO Is this really the place for this? Would shorten GraphContext use if
	// this is possible, but looks like this shouldn't be advised...
	public Class<? extends PrereqObject> getReferenceClass()
	{
		return getClass();
	}
}
