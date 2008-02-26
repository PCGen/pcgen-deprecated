/*
 * CompanionListLst.java
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.TripleKeyMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.inst.CDOMRace;
import pcgen.cdom.inst.CompanionList;
import pcgen.core.FollowerOption;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstUtils;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.MapToList;
import pcgen.util.PropertyFactory;

/**
 * This class implments the parsing for the COMPANIONLIST token.
 * <p />
 * <b>Tag Name</b>: <code>COMPANIONLIST</code>:x|y,y|z
 * <p />
 * <b>Variables Used (x)</b>: <i>Text</i> (The type of companion list to add
 * to).<br />
 * <b>Variables Used (y)</b>: <i>Text</i> (A race of companion to allow to the
 * character).<br />
 * <b>Variables Used (y)</b>: <code>RACETYPE</code>=<i>Text</i> (all races
 * with the specified <code>RACETYPE</code> are available as this type of
 * companion). <br />
 * <b>Variables Used (y)</b>: <code>ANY</code> (Any race can be a companion
 * of this type).<br />
 * <b>Variables Used (z)</b>: <code>FOLLOWERADJUSTMENT</code>=<i>Number</i>
 * (Adjustment to the follower level variable).
 * <p />
 * <b>What it does:</b>
 * <ul>
 * <li>Adds a specific race or races to the list of available companions for
 * the specified companion type.</li>
 * <li>PRExxx tags can be added at the end of COMPANIONLIST tags, PRExxx tags
 * are checked against the master.</li>
 * <li>If the master does not meet the prereqs the companion will be displayed
 * in the list but will be listed in red and cannot be added as a companion.
 * </li>
 * </ul>
 * <p />
 * <b>Examples:</b><br />
 * <code>COMPANIONLIST:Familiar|Bat,Cat,Hawk,Lizard,Owl,Rat,Raven,
 * Snake (Tiny/Viper),Toad,Weasel</code><br />
 * Would build the list of standard familiars available to a Sorcerer or Wizard.
 * <p />
 * <code>COMPANIONLIST:Pet|RACETYPE=Animal</code><br />
 * Would build a list of all animals to available as a Pet.
 * <p />
 * <code>COMPANIONLIST:Familiar|Quasit|PREFEAT:1,Special Familiar|
 * PREALIGN:CE</code><br />
 * A Quasit can be chosen as a Familiar but only if the master is evil and has
 * the Special Familiar feat.
 * <p />
 * <code>COMPANIONLIST:Animal Companion|Ape|FOLLOWERADJUSTMENT:-3</code>
 * <br />
 * An Ape companion to a 4th level Druid gains the benefits normally granted to
 * a companion of a 1st level Druid.
 * 
 * @author divaa01
 * 
 */
public class CompanionListLst extends AbstractToken implements GlobalLstToken, CDOMPrimaryToken<CDOMObject>
{
	private static final String COMPANIONLIST = "COMPANIONLIST"; //$NON-NLS-1$

	private static final String FOLLOWERADJUSTMENT = "FOLLOWERADJUSTMENT"; //$NON-NLS-1$

	/**
	 * Parses the COMPANIONLIST tag.
	 * 
	 * @param anInt
	 *            The level at which this tag should apply
	 * @param anObj
	 *            The object this tag was found on
	 * @param aValue
	 *            The token with the COMPANIONLIST portion striped off.
	 * @return true if the tag is successfully parsed.
	 * @throws PersistenceLayerException
	 * @see pcgen.persistence.lst.GlobalLstToken#parse(pcgen.core.PObject,
	 *      java.lang.String, int)
	 */
	public boolean parse(final PObject anObj, final String aValue,
		@SuppressWarnings("unused")
		final int anInt) throws PersistenceLayerException
	{
		final StringTokenizer tok = new StringTokenizer(aValue, LstUtils.PIPE);
		if (tok.hasMoreTokens())
		{
			final String companionType = tok.nextToken();
			if (tok.hasMoreTokens())
			{
				final Set<String> races = new HashSet<String>();

				final String list = tok.nextToken();
				final StringTokenizer subTok =
						new StringTokenizer(list, LstUtils.COMMA);
				while (subTok.hasMoreTokens())
				{
					// We can't expand races here since this is a global tag
					// and races may not have been processed yet.
					// For the same reason ANY will be passed on.
					// OLDTODO Need to figure out how to deal with this issue.
					races.add(subTok.nextToken());
				}

				int followerAdjustment = 0;
				final List<Prerequisite> prereqs =
						new ArrayList<Prerequisite>();

				// The remainder of the elements are optional.
				while (tok.hasMoreTokens())
				{
					final String optArg = tok.nextToken();
					if (optArg.startsWith(FOLLOWERADJUSTMENT))
					{
						if (followerAdjustment != 0)
						{
							Logging.debugPrint(getTokenName()
								+ ": Multiple " //$NON-NLS-1$
								+ FOLLOWERADJUSTMENT
								+ " tags specified.  Will use last one."); //$NON-NLS-1$
						}

						final String adj =
								optArg
									.substring(FOLLOWERADJUSTMENT.length() + 1);

						followerAdjustment = Integer.parseInt(adj);
					}
					else if (optArg.startsWith("PRE"))
					{
						final PreParserFactory factory =
								PreParserFactory.getInstance();
						final Prerequisite prereq = factory.parse(optArg);
						prereqs.add(prereq);
					}
					else
					{
						Logging.debugPrint(getTokenName()
							+ ": Unknown optional argument: " //$NON-NLS-1$
							+ optArg);
					}
				}
				for (final String r : races)
				{
					final FollowerOption option = new FollowerOption(r);
					option.setType(companionType);
					if (prereqs.size() > 0)
					{
						option.addAllPrerequisites(prereqs);
					}
					if (followerAdjustment != 0)
					{
						option.setAdjustment(followerAdjustment);
					}
					anObj.addToFollowerList(companionType, option);
				}
				return true;
			}
		}

		throw new PersistenceLayerException(PropertyFactory.getFormattedString(
			"Errors.LstTokens.InvalidTokenFormat", //$NON-NLS-1$
			getTokenName(), aValue));
	}

	/**
	 * Returns the name of the token this class can process.
	 * 
	 * @return Token name
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return COMPANIONLIST;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, LstUtils.PIPE);

		String companionType = tok.nextToken();

		if (!tok.hasMoreTokens())
		{
			Logging.errorPrint(getTokenName()
				+ " requires more than just a Type: " + value);
			return false;
		}

		String list = tok.nextToken();

		if (hasIllegalSeparator(',', list))
		{
			return false;
		}

		StringTokenizer subTok = new StringTokenizer(list, LstUtils.COMMA);

		Set<CDOMReference<CDOMRace>> races = new HashSet<CDOMReference<CDOMRace>>();
		boolean foundAny = false;
		while (subTok.hasMoreTokens())
		{
			String tokString = subTok.nextToken();
			if (Constants.LST_ANY.equalsIgnoreCase(tokString))
			{
				foundAny = true;
				races.add(context.ref.getCDOMAllReference(CDOMRace.class));
			}
			else
			{
				races.add(context.ref.getCDOMReference(CDOMRace.class, tokString));
			}
		}
		if (foundAny && races.size() > 1)
		{
			Logging
				.errorPrint("Non-sensical Race List includes Any and specific races: "
					+ value);
			return false;
		}

		if (!tok.hasMoreTokens())
		{
			// No other args, so we're done
			finish(context, obj, companionType, races, null, null);
			return true;
		}

		// The remainder of the elements are optional.
		Integer followerAdjustment = null;
		String optArg = tok.nextToken();
		while (true)
		{
			if (optArg.startsWith(FOLLOWERADJUSTMENT))
			{
				if (followerAdjustment != null)
				{
					Logging.errorPrint(getTokenName() + " Error: Multiple "
						+ FOLLOWERADJUSTMENT + " tags specified.");
					return false;
				}

				int faStringLength = FOLLOWERADJUSTMENT.length();
				if (optArg.length() <= faStringLength)
				{
					Logging.errorPrint("Empty FOLLOWERALIGN value in "
						+ getTokenName() + " is prohibited");
					return false;
				}
				String adj = optArg.substring(faStringLength + 1);

				try
				{
					followerAdjustment = Integer.valueOf(adj);
				}
				catch (NumberFormatException nfe)
				{
					Logging
						.errorPrint("Expecting a number for FOLLOWERADJUSTMENT: "
							+ adj);
					Logging.errorPrint("  was parsing Token " + getTokenName());
					return false;
				}
			}
			else if (optArg.startsWith("PRE") || optArg.startsWith("!PRE"))
			{
				break;
			}
			else
			{
				Logging
					.errorPrint(getTokenName()
						+ ": Unknown argument (was expecting FOLLOWERADJUSTMENT: or PRExxx): "
						+ optArg);
				return false;
			}
			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				finish(context, obj, companionType, races, followerAdjustment,
					null);
				return true;
			}
			optArg = tok.nextToken();
		}

		List<Prerequisite> prereqs = new ArrayList<Prerequisite>();

		while (true)
		{
			Prerequisite prereq = getPrerequisite(optArg);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put items after the "
					+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			prereqs.add(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			optArg = tok.nextToken();
		}

		finish(context, obj, companionType, races, followerAdjustment, prereqs);
		return true;
	}

	private void finish(LoadContext context, CDOMObject obj,
		String companionType, Set<CDOMReference<CDOMRace>> races,
		Integer followerAdjustment, List<Prerequisite> prereqs)
	{
		context.ref.constructIfNecessary(CompanionList.class, companionType);
		CDOMReference<CompanionList> ref =
				context.ref
					.getCDOMReference(CompanionList.class, companionType);

		for (CDOMReference<CDOMRace> race : races)
		{
			AssociatedPrereqObject edge =
					context.getListContext().addToList(getTokenName(), obj,
						ref, race);
			if (followerAdjustment != null)
			{
				edge.setAssociation(AssociationKey.FOLLOWER_ADJUSTMENT,
					followerAdjustment);
			}
			edge.addAllPrerequisites(prereqs);
		}
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Collection<CDOMReference<? extends CDOMList<? extends CDOMObject>>> changedLists =
				context.getListContext().getChangedLists(obj,
					CompanionList.class);
		TripleKeyMapToList<Set<Prerequisite>, CDOMReference<? extends CDOMList<? extends CDOMObject>>, Integer, LSTWriteable> m =
				new TripleKeyMapToList<Set<Prerequisite>, CDOMReference<? extends CDOMList<? extends CDOMObject>>, Integer, LSTWriteable>();

		for (CDOMReference ref : changedLists)
		{
			AssociatedChanges changes =
					context.getListContext().getChangesInList(getTokenName(),
						obj, ref);
			if (changes == null)
			{
				// Legal if no COMPANIONLIST was present
				continue;
			}
			Collection<LSTWriteable> removedItems = changes.getRemoved();
			if (removedItems != null && !removedItems.isEmpty()
					|| changes.includesGlobalClear())
			{
				context.addWriteMessage(getTokenName()
					+ " does not support .CLEAR");
				return null;
			}
			MapToList<LSTWriteable, AssociatedPrereqObject> mtl =
					changes.getAddedAssociations();
			if (mtl == null || mtl.isEmpty())
			{
				// Zero indicates no Token
				// TODO Error message - unexpected?
				return null;
			}
			for (LSTWriteable added : mtl.getKeySet())
			{
				for (AssociatedPrereqObject assoc : mtl.getListFor(added))
				{
					Set<Prerequisite> prereqs =
							new HashSet<Prerequisite>(assoc
								.getPrerequisiteList());
					Integer fa =
							assoc
								.getAssociation(AssociationKey.FOLLOWER_ADJUSTMENT);
					m.addToListFor(prereqs, ref, fa, added);
				}
			}
		}
		if (m.isEmpty())
		{
			// Legal if no COMPANIONMOD Tokens
			return null;
		}

		Set<String> set = new TreeSet<String>();
		StringBuilder sb = new StringBuilder();
		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			String prereqString = null;
			if (prereqs != null && !prereqs.isEmpty())
			{
				prereqString = getPrerequisiteString(context, prereqs);
			}

			for (CDOMReference<? extends CDOMList<? extends CDOMObject>> cl : m
				.getSecondaryKeySet(prereqs))
			{
				for (Integer fa : m.getTertiaryKeySet(prereqs, cl))
				{
					sb.setLength(0);
					sb.append(cl.getLSTformat());
					sb.append(Constants.PIPE);
					sb.append(ReferenceUtilities.joinLstFormat(m.getListFor(
						prereqs, cl, fa), Constants.COMMA));
					if (fa != null)
					{
						sb.append(Constants.PIPE);
						sb.append("FOLLOWERADJUSTMENT:");
						sb.append(fa);
					}
					if (prereqString != null)
					{
						sb.append(Constants.PIPE);
						sb.append(prereqString);
					}
					set.add(sb.toString());
				}
			}
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
