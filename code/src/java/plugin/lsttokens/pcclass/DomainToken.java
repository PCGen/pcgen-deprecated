/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.pcclass;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.PCClassUniversalLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * Class deals with DOMAIN Token
 */
public class DomainToken extends AbstractToken implements PCClassLstToken,
		PCClassUniversalLstToken
{

	private static final Class<Domain> DOMAIN_CLASS = Domain.class;

	@Override
	public String getTokenName()
	{
		return "DOMAIN";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		final StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			String domainKey;
			String prereq = null; // Do not initialize, null is significant!

			// Note: May contain PRExxx
			if (aString.indexOf("[") == -1)
			{
				domainKey = aString;
			}
			else
			{
				int openBracketLoc = aString.indexOf("[");
				domainKey = aString.substring(0, openBracketLoc);
				if (!aString.endsWith("]"))
				{
					Logging.errorPrint("Unresolved Prerequisite on Domain "
						+ aString + " in " + getTokenName());
				}
				prereq =
						aString.substring(openBracketLoc + 1, aString.length()
							- openBracketLoc - 2);
			}

			Domain thisDomain = Globals.getDomainKeyed(domainKey);

			if (thisDomain == null)
			{
				Logging.errorPrint("Unresolved Domain " + domainKey + " in "
					+ getTokenName());
			}
			else
			{
				Domain clonedDomain = thisDomain.clone();
				if (prereq != null)
				{
					try
					{
						clonedDomain.addPreReq(PreParserFactory.getInstance()
							.parse(prereq));
					}
					catch (PersistenceLayerException e)
					{
						Logging.errorPrint("Error generating Prerequisite "
							+ prereq + " in " + getTokenName());
					}
				}
				pcclass.addDomain(level, clonedDomain);
			}
		}

		return true;
	}

	public boolean parse(LoadContext context, PObject po, String value)
		throws PersistenceLayerException
	{
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			context.graph.removeAll(getTokenName(), po);
			return true;
		}
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);

		while (pipeTok.hasMoreTokens())
		{
			String tok = pipeTok.nextToken();
			// Note: May contain PRExxx
			String domainKey;
			Prerequisite prereq = null;

			int openBracketLoc = tok.indexOf('[');
			if (openBracketLoc == -1)
			{
				domainKey = tok;
			}
			else
			{
				if (tok.indexOf(']') != tok.length() - 1)
				{
					Logging.errorPrint("Invalid " + getTokenName()
						+ " must end with ']' if it contains a PREREQ tag");
					return false;
				}
				domainKey = tok.substring(0, openBracketLoc);
				String prereqString =
						tok.substring(openBracketLoc + 1, tok.length() - 1);
				if (prereqString.length() == 0)
				{
					Logging.errorPrint(getTokenName()
						+ " cannot have empty prerequisite : " + value);
					return false;
				}
				prereq = getPrerequisite(prereqString);
			}
			CDOMSimpleSingleRef<Domain> domain =
					context.ref.getCDOMReference(DOMAIN_CLASS, domainKey);

			PCGraphGrantsEdge edge =
					context.graph.grant(getTokenName(), po, domain);
			if (prereq != null)
			{
				edge.addPrerequisite(prereq);
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, PObject po)
	{
		GraphChanges<Domain> changes =
				context.graph.getChangesFromToken(getTokenName(), po,
					DOMAIN_CLASS);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		List<String> list = new ArrayList<String>();
		for (LSTWriteable ab : added)
		{
			AssociatedPrereqObject assoc = changes.getAddedAssociation(ab);
			StringBuilder sb = new StringBuilder();
			sb.append(ab.getLSTformat());
			if (assoc.hasPrerequisites())
			{
				List<Prerequisite> prereqs = assoc.getPrerequisiteList();
				if (prereqs.size() > 1)
				{
					context.addWriteMessage("Incoming Edge to " + po.getKey()
						+ " had more than one " + "Prerequisite: "
						+ prereqs.size());
					return null;
				}
				sb.append('[');
				StringWriter swriter = new StringWriter();
				try
				{
					prereqWriter.write(swriter, prereqs.get(0));
				}
				catch (PersistenceLayerException e)
				{
					context.addWriteMessage("Error writing Prerequisite: " + e);
					return null;
				}
				sb.append(swriter.toString());
				sb.append(']');
			}
			list.add(sb.toString());
		}
		return new String[]{StringUtil.join(list, Constants.PIPE)};
	}
}