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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CompanionList;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
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
		StringTokenizer tok = new StringTokenizer(value, LstUtils.PIPE);

		String companionType = tok.nextToken();

		if (!tok.hasMoreTokens())
		{
			throw new PersistenceLayerException(PropertyFactory
				.getFormattedString("Errors.LstTokens.InvalidTokenFormat", //$NON-NLS-1$
					getTokenName(), value));
		}

		String list = tok.nextToken();
		StringTokenizer subTok = new StringTokenizer(list, LstUtils.COMMA);

		Set<CDOMReference<Race>> races = new HashSet<CDOMReference<Race>>();
		while (subTok.hasMoreTokens())
		{
			String tokString = subTok.nextToken();
			if (Constants.LST_ANY.equalsIgnoreCase(tokString))
			{
				races.add(context.ref.getCDOMAllReference(Race.class));
			}
			else
			{
				races.add(context.ref.getCDOMReference(Race.class, tokString));
			}
		}

		int followerAdjustment = 0;
		final List<Prerequisite> prereqs = new ArrayList<Prerequisite>();

		// The remainder of the elements are optional.
		while (tok.hasMoreTokens())
		{
			String optArg = tok.nextToken();
			if (optArg.startsWith(FOLLOWERADJUSTMENT))
			{
				if (followerAdjustment != 0)
				{
					Logging.errorPrint(getTokenName() + " Error: Multiple "
						+ FOLLOWERADJUSTMENT + " tags specified.");
					return false;
				}

				//FIXME Check =
				String adj = optArg.substring(FOLLOWERADJUSTMENT.length() + 1);

				try
				{
					followerAdjustment = Integer.parseInt(adj);
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
				Logging.debugPrint(getTokenName()
					+ ": Unknown optional argument: " //$NON-NLS-1$
					+ optArg);
			}
		}
		CompanionList cl = new CompanionList(companionType, races);
		if (followerAdjustment != 0)
		{
			cl.setAdjustment(followerAdjustment);
		}
		PCGraphGrantsEdge edge =
				context.graph.linkObjectIntoGraph(getTokenName(), obj, cl);
		for (Prerequisite prereq : prereqs)
		{
			edge.addPrerequisite(prereq);
		}
		return true;
	}

	public String unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					CompanionList.class);
		if (edgeList == null || edgeList.isEmpty())
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		boolean needTab = false;
		for (PCGraphEdge edge : edgeList)
		{
			List<Prerequisite> prereqs = edge.getPrerequisiteList();
			CompanionList cl = (CompanionList) edge.getNodeAt(1);
			int followerAdjustment = cl.getAdjustment();
			if (needTab)
			{
				sb.append('\t');
			}
			needTab = true;
			sb.append(getTokenName()).append(Constants.COLON);
			sb.append(cl.getFollowerType());
			for (CDOMReference<Race> race : cl.getCompanionSet())
			{
				sb.append(Constants.PIPE).append(race.getLSTformat());
			}
			if (followerAdjustment != 0)
			{
				sb.append(Constants.PIPE).append(FOLLOWERADJUSTMENT).append(
					Constants.COLON);
				sb.append(followerAdjustment);
			}
			if (prereqs != null && !prereqs.isEmpty())
			{
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
			}
		}
		return sb.toString();
	}
}
