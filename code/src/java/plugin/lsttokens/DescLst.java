/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
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
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.core.Description;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.io.EntityEncoder;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * Handles DESC token processing
 * 
 * @author djones4
 */
public class DescLst extends AbstractToken implements GlobalLstToken
{
	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "DESC"; //$NON-NLS-1$
	}

	/**
	 * @see pcgen.persistence.lst.GlobalLstToken#parse(pcgen.core.PObject,
	 *      java.lang.String, int)
	 */
	public boolean parse(final PObject obj, final String value,
		@SuppressWarnings("unused")
		int anInt)
	{
		if (value.startsWith(".CLEAR")) //$NON-NLS-1$
		{
			if (value.equals(".CLEAR")) //$NON-NLS-1$
			{
				obj.removeAllDescriptions();
			}
			else
			{
				obj.removeDescription(value.substring(7));
			}
			return true;
		}
		obj.addDescription(parseDescriptionOld(value));
		return true;
	}

	/**
	 * Parses the DESC tag into a Description object.
	 * 
	 * @param aDesc
	 *            The LST tag
	 * @return A <tt>Description</tt> object
	 */
	public Description parseDescriptionOld(final String aDesc)
	{
		final StringTokenizer tok = new StringTokenizer(aDesc, Constants.PIPE);

		final Description desc =
				new Description(EntityEncoder.decode(tok.nextToken()));

		boolean isPre = false;
		while (tok.hasMoreTokens())
		{
			final String token = tok.nextToken();
			if (PreParserFactory.isPreReqString(token)) //$NON-NLS-1$
			{
				desc.addPrerequisite(getPrerequisite(token));
				isPre = true;
			}
			else
			{
				if (isPre)
				{
					Logging.errorPrint("Invalid " + getTokenName() + ": "
						+ aDesc);
					Logging
						.errorPrint("  PRExxx must be at the END of the Token");
					isPre = false;
				}
				desc.addVariable(token);
			}
		}

		return desc;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			context.getGraphContext().removeAll(getTokenName(), obj);
			return true;
		}
		if (value.startsWith(Constants.LST_DOT_CLEAR_DOT))
		{
			/*
			 * TODO Can this import the Desc directly? Probably not? The problem
			 * is that this equality check would then test for Prerequisites, et
			 * al.
			 */
			// context.graph.remove(getTokenName(), obj, desc);
			// context.obj
			// .removeFromList(obj, new Description(value.substring(7)));
			return true;
		}

		Description d = parseDescription(value);
		if (d == null)
		{
			return false;
		}
		context.getGraphContext().grant(getTokenName(), obj, d);
		return true;
	}

	/**
	 * Parses the DESC tag into a Description object.
	 * 
	 * @param aDesc
	 *            The LST tag
	 * @return A <tt>Description</tt> object
	 */
	public Description parseDescription(final String aDesc)
	{
		StringTokenizer tok = new StringTokenizer(aDesc, Constants.PIPE);

		String descString = tok.nextToken();

		if (descString.startsWith("PRE") || descString.startsWith("!PRE"))
		{
			Logging.errorPrint(getTokenName() + " encountered only a PRExxx: "
				+ aDesc);
			return null;
		}
		Description desc = new Description(EntityEncoder.decode(descString));

		if (!tok.hasMoreTokens())
		{
			return desc;
		}

		String token = tok.nextToken();
		while (true)
		{
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				Logging.errorPrint(getTokenName()
					+ " tag confused by '.CLEAR' as a " + "middle token: "
					+ aDesc);
				return null;
			}
			else if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
			else
			{
				desc.addVariable(token);
			}

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return desc;
			}
			token = tok.nextToken();
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put Abilities after the "
					+ "PRExxx tags in " + getTokenName() + ":?)");
				return null;
			}
			desc.addPrerequisite(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}
		return desc;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		AssociatedChanges<Description> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					obj, Description.class);
		if (changes == null)
		{
			return null;
		}
		List<String> list = new ArrayList<String>();
		if (changes.hasRemovedItems())
		{
			if (changes.includesGlobalClear())
			{
				context.addWriteMessage("Non-sensical relationship in "
					+ getTokenName()
					+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			list.add(Constants.LST_DOT_CLEAR_DOT
				+ ReferenceUtilities.joinLstFormat(changes.getRemoved(),
					",|.CLEAR."));
		}
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		if (changes.hasAddedItems())
		{
			for (LSTWriteable lw : changes.getAdded())
			{
				list.add(lw.getLSTformat());
			}
		}
		if (list.isEmpty())
		{
			return null;
		}
		return list.toArray(new String[list.size()]);
	}
}
