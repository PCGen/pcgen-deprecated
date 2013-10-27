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
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.inst.CDOMDomain;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with DOMAIN Token
 */
public class DomainToken extends AbstractToken implements PCClassLstToken,
		CDOMPrimaryToken<CDOMPCClass>
{

	private static final Class<CDOMDomain> DOMAIN_CLASS = CDOMDomain.class;

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
				prereq = aString.substring(openBracketLoc + 1, aString.length()
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

	public boolean parse(LoadContext context, CDOMPCClass po, String value)
	{
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			context.getGraphContext().removeAll(getTokenName(), po);
			return true;
		}
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
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
				if (tok.indexOf(']') != -1)
				{
					Logging.errorPrint("Invalid " + getTokenName()
							+ " must have '[' if it contains a PREREQ tag");
					return false;
				}
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
				String prereqString = tok.substring(openBracketLoc + 1, tok
						.length() - 1);
				if (prereqString.length() == 0)
				{
					Logging.errorPrint(getTokenName()
							+ " cannot have empty prerequisite : " + value);
					return false;
				}
				prereq = getPrerequisite(prereqString);
			}
			CDOMSingleRef<CDOMDomain> domain = context.ref.getCDOMReference(
					DOMAIN_CLASS, domainKey);

			AssociatedPrereqObject edge = context.getGraphContext().grant(
					getTokenName(), po, domain);
			if (prereq != null)
			{
				edge.addPrerequisite(prereq);
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMPCClass po)
	{
		AssociatedChanges<CDOMReference<CDOMDomain>> changes = context.getGraphContext()
				.getChangesFromToken(getTokenName(), po, DOMAIN_CLASS);
		List<String> list = new ArrayList<String>();
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		Collection<CDOMReference<CDOMDomain>> removedItems = changes.getRemoved();
		if (removedItems != null && !removedItems.isEmpty())
		{
			context.addWriteMessage(getTokenName()
					+ " does not support .CLEAR.");
			return null;
		}
		MapToList<CDOMReference<CDOMDomain>, AssociatedPrereqObject> mtl = changes
				.getAddedAssociations();
		if (mtl != null && !mtl.isEmpty())
		{
			PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
			for (CDOMReference<CDOMDomain> ab : mtl.getKeySet())
			{
				List<AssociatedPrereqObject> assocList = mtl.getListFor(ab);
				if (assocList.size() != 1)
				{
					context
							.addWriteMessage("Only one Association to a CHOOSE can be made per object");
					return null;
				}
				AssociatedPrereqObject assoc = assocList.get(0);
				StringBuilder sb = new StringBuilder();
				sb.append(ab.getLSTformat());
				if (assoc.hasPrerequisites())
				{
					List<Prerequisite> prereqs = assoc.getPrerequisiteList();
					if (prereqs.size() > 1)
					{
						context.addWriteMessage("Incoming Edge to "
								+ po.getKey() + " had more than one "
								+ "Prerequisite: " + prereqs.size());
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
						context.addWriteMessage("Error writing Prerequisite: "
								+ e);
						return null;
					}
					sb.append(swriter.toString());
					sb.append(']');
				}
				list.add(sb.toString());
			}
		}
		if (list.isEmpty())
		{
			return null;
		}
		return new String[] { StringUtil.join(list, Constants.PIPE) };
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}