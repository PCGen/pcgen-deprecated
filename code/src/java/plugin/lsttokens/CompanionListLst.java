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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.TreeSet;

import pcgen.base.util.TripleKeyMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphAllowsEdge;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.Aggregator;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.CompanionList;
import pcgen.core.FollowerOption;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstUtils;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;
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
public class CompanionListLst extends AbstractToken implements GlobalLstToken
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
					// TODO Need to figure out how to deal with this issue.
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
						option.addPrerequisites(prereqs);
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
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
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

		if (list.charAt(0) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with , : " + value);
			return false;
		}
		if (list.charAt(list.length() - 1) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with , : " + value);
			return false;
		}
		if (list.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}

		StringTokenizer subTok = new StringTokenizer(list, LstUtils.COMMA);

		Set<CDOMReference<Race>> races = new HashSet<CDOMReference<Race>>();
		boolean foundAny = false;
		while (subTok.hasMoreTokens())
		{
			String tokString = subTok.nextToken();
			if (Constants.LST_ANY.equalsIgnoreCase(tokString))
			{
				foundAny = true;
				races.add(context.ref.getCDOMAllReference(Race.class));
			}
			else
			{
				races.add(context.ref.getCDOMReference(Race.class, tokString));
			}
		}
		if (foundAny && races.size() > 1)
		{
			Logging
				.errorPrint("Non-sensical Race List includes Any and specific races: "
					+ value);
			return false;
		}

		Integer followerAdjustment = null;
		final List<Prerequisite> prereqs = new ArrayList<Prerequisite>();

		// The remainder of the elements are optional.
		while (tok.hasMoreTokens())
		{
			String optArg = tok.nextToken();
			if (optArg.startsWith(FOLLOWERADJUSTMENT))
			{
				if (followerAdjustment != null)
				{
					Logging.errorPrint(getTokenName() + " Error: Multiple "
						+ FOLLOWERADJUSTMENT + " tags specified.");
					return false;
				}

				// FIXME Check =
				String adj = optArg.substring(FOLLOWERADJUSTMENT.length() + 1);

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
				prereqs.add(getPrerequisite(optArg));
			}
			else
			{
				Logging
					.errorPrint(getTokenName()
						+ ": Unknown argument (was expecting FOLLOWERALIGN: or PRExxx): "
						+ optArg);
				return false;
			}
		}

		CDOMReference<CompanionList> ref =
				context.ref
					.getCDOMReference(CompanionList.class, companionType);

		Aggregator agg = new Aggregator(obj, ref, getTokenName());
		agg.addPrerequisites(prereqs);
		/*
		 * This is intentionally Holds, as the context for traversal must only
		 * be the ref (linked by the Activation Edge). So we need an edge to the
		 * Activator to get it copied into the PC, but since this is a 3rd party
		 * Token, the Race should never grant anything hung off the aggregator.
		 */
		context.graph.linkHoldsIntoGraph(getTokenName(), obj, agg);
		context.graph.linkActivationIntoGraph(getTokenName(), ref, agg);
		for (CDOMReference<Race> race : races)
		{
			PCGraphAllowsEdge edge =
					context.graph.linkAllowIntoGraph(getTokenName(), agg, race);
			if (followerAdjustment != null)
			{
				edge.setAssociation(AssociationKey.FOLLOWER_ADJUSTMENT,
					followerAdjustment);
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> edgeSet =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					Aggregator.class);
		if (edgeSet == null || edgeSet.isEmpty())
		{
			return null;
		}

		TripleKeyMapToList<Set<Prerequisite>, CDOMReference<CompanionList>, Integer, CDOMReference<Race>> m =
				new TripleKeyMapToList<Set<Prerequisite>, CDOMReference<CompanionList>, Integer, CDOMReference<Race>>();
		for (PCGraphEdge edge : edgeSet)
		{
			Aggregator agg = (Aggregator) edge.getNodeAt(1);
			Set<PCGraphEdge> childSet =
					context.graph.getChildLinksFromToken(getTokenName(), agg,
						Race.class);
			if (childSet == null || childSet.isEmpty())
			{
				context
					.addWriteMessage("Empty Child Set not valid for Aggregator in "
						+ getTokenName());
				return null;
			}

			Set<PCGraphEdge> parentSet =
					context.graph.getParentLinksFromToken(getTokenName(), agg,
						CompanionList.class);
			if (parentSet == null || parentSet.isEmpty())
			{
				context
					.addWriteMessage("Empty Parent Set not valid for Aggregator in "
						+ getTokenName());
				return null;
			}
			if (parentSet.size() != 1)
			{
				context
					.addWriteMessage("Parent Set with more than one entry not valid for Aggregator in "
						+ getTokenName());
				return null;
			}
			CDOMReference<CompanionList> parent =
					(CDOMReference<CompanionList>) parentSet.iterator().next()
						.getNodeAt(0);

			Set<Prerequisite> prereqs =
					new HashSet<Prerequisite>(agg.getPreReqList());

			for (PCGraphEdge child : childSet)
			{
				Integer fa =
						child
							.getAssociation(AssociationKey.FOLLOWER_ADJUSTMENT);
				CDOMReference<Race> race =
						(CDOMReference<Race>) child.getNodeAt(1);
				m.addToListFor(prereqs, parent, fa, race);
			}
		}

		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		Set<String> set = new TreeSet<String>();
		Set<CDOMReference<?>> refSet =
				new TreeSet<CDOMReference<?>>(TokenUtilities.REFERENCE_SORTER);
		StringBuilder sb = new StringBuilder();
		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			String prereqString = "";
			if (prereqs != null && !prereqs.isEmpty())
			{
				sb.setLength(0);
				for (Prerequisite p : prereqs)
				{
					StringWriter swriter = new StringWriter();
					try
					{
						prereqWriter.write(swriter, p);
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage("Error writing Prerequisite: "
							+ e);
						return null;
					}
					sb.append(Constants.PIPE).append(swriter.toString());
				}
				prereqString = sb.toString();
			}

			for (CDOMReference<CompanionList> cl : m
				.getSecondaryKeySet(prereqs))
			{
				for (Integer fa : m.getTertiaryKeySet(prereqs, cl))
				{
					sb.setLength(0);
					sb.append(cl.getLSTformat());
					sb.append(Constants.PIPE);
					refSet.clear();
					refSet.addAll(m.getListFor(prereqs, cl, fa));
					sb.append(ReferenceUtilities.joinLstFormat(refSet,
						Constants.COMMA));
					if (fa != null)
					{
						sb.append(Constants.PIPE);
						sb.append("FOLLOWERADJUSTMENT:");
						sb.append(fa);
					}
					sb.append(prereqString);
					set.add(sb.toString());
				}
			}
		}
		return set.toArray(new String[set.size()]);
	}
}
