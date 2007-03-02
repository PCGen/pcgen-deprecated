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

import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Description;
import pcgen.core.PObject;
import pcgen.core.SpecialAbility;
import pcgen.io.EntityEncoder;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;

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
		while (tok.hasMoreTokens())
		{
			final String token = tok.nextToken();
			if (PreParserFactory.isPreReqString(token)) //$NON-NLS-1$
			{
				desc.addPrerequisite(getPrerequisite(token));
			}
			else
			{
				desc.addVariable(token);
			}
		}

		return desc;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			obj.removeListFor(ListKey.DESC);
			return true;
		}
		if (value.startsWith(Constants.LST_DOT_CLEAR_DOT))
		{
			/*
			 * TODO Can this import the Desc directly? Probably not? The problem
			 * is that this equality check would then test for Prerequisites, et
			 * al.
			 */
			obj.removeFromListFor(ListKey.DESC, new Description(value
				.substring(7)));
			return true;
		}

		Description d = parseDescription(value);
		if (d == null)
		{
			return false;
		}
		context.graph.linkObjectIntoGraph(getTokenName(), obj, d);
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

		Description desc =
				new Description(EntityEncoder.decode(tok.nextToken()));
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (token.startsWith("PRE"))
			{
				desc.addPrerequisite(getPrerequisite(token));
			}
			else
			{
				desc.addVariable(token);
				// desc.addVariable(FormulaFactory.getFormulaFor(token));
			}
		}
		// TODO Implement once Desc is capable of self-checking variable count
		// if (!desc.isValid()) {
		// Logging.errorPrint("Variable Count in " + getTokenName()
		// + " did not match variables requested "
		// + "in base string. Value was: " + aDesc);
		// return null;
		// }
		return desc;
	}

	public String unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					SpecialAbility.class);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean needsTab = false;
		for (PCGraphEdge edge : edges)
		{
			if (needsTab)
			{
				sb.append('\t');
			}
			needsTab = true;
			sb.append(getTokenName()).append(':');
			sb.append(((Description) edge.getSinkNodes().get(0)).getPCCText());
		}
		return sb.toString();
	}
}
