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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.GrantActor;
import pcgen.cdom.helper.ReferenceChoiceSet;
import pcgen.core.ClassSpellList;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SPELLLIST Token
 */
public class SpelllistToken extends AbstractToken implements PCClassLstToken,
		PCClassClassLstToken
{

	private static Class<ClassSpellList> SPELLLIST_CLASS = ClassSpellList.class;

	@Override
	public String getTokenName()
	{
		return "SPELLLIST";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|");
		int spellCount = 0;

		if (value.indexOf('|') >= 0)
		{
			try
			{
				spellCount = Integer.parseInt(aTok.nextToken());
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Import error: Expected first value of "
					+ "SPELLLIST token with a | to be a number");
				return false;
			}
		}

		final List<String> spellChoices = new ArrayList<String>();

		while (aTok.hasMoreTokens())
		{
			String className = aTok.nextToken();
			if (Globals.getDomainKeyed(className) != null)
			{
				Logging.deprecationPrint(getTokenName()
					+ " now requires a DOMAIN. prefix "
					+ "when used with a DOMAIN rather than a Class");
			}
			if (className.startsWith("DOMAIN."))
			{
				String domainName = className.substring(7);
				if (Globals.getDomainKeyed(domainName) == null)
				{
					Logging.errorPrint(getTokenName()
						+ " could not find Domain: " + domainName);
					return false;
				}
				// This is safe in 5.x since the class & domain names can't
				// conflict
				spellChoices.add(domainName);
			}
			else
			{
				spellChoices.add(className);
			}
		}

		// Protection against a "" value parameter
		if (spellChoices.size() > 0)
		{
			pcclass.setClassSpellChoices(spellCount, spellChoices);
		}
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		int pipeLoc = value.indexOf('|');
		if (pipeLoc == -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
				+ " may not have only one pipe separated argument: " + value);
			return false;
		}
		if (pipeLoc != value.lastIndexOf('|'))
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
				+ " may have only one pipe: " + value);
			return false;
		}

		String rest = value.substring(pipeLoc + 1);
		if (hasIllegalSeparator(',', rest))
		{
			return false;
		}
		int count;
		try
		{
			count = Integer.parseInt(value.substring(0, pipeLoc));
			if (count <= 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, "Number in "
					+ getTokenName() + " must be greater than zero: " + value);
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Invalid Number in "
				+ getTokenName() + ": " + value);
			return false;
		}

		StringTokenizer tok = new StringTokenizer(rest, Constants.COMMA);
		List<CDOMReference<ClassSpellList>> refs =
				new ArrayList<CDOMReference<ClassSpellList>>();
		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<ClassSpellList> ref;
			if (Constants.LST_ALL.equals(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(SPELLLIST_CLASS);
			}
			else
			{
				foundOther = true;
				ref = context.ref.getCDOMReference(SPELLLIST_CLASS, token);
			}
			refs.add(ref);
		}

		if (foundAny && foundOther)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Non-sensical "
				+ getTokenName() + ": Contains ANY and a specific reference: "
				+ value);
			return false;
		}

		ChooseActionContainer container =
				new ChooseActionContainer(getTokenName());
		container.addActor(new GrantActor<PCTemplate>());
		context.getGraphContext().grant(getTokenName(), pcc, container);
		container.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
			.getFormulaFor(count));
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
			.getFormulaFor(Integer.MAX_VALUE));
		ReferenceChoiceSet<ClassSpellList> rcs =
				new ReferenceChoiceSet<ClassSpellList>(refs);
		ChoiceSet<ClassSpellList> cs =
				new ChoiceSet<ClassSpellList>(getTokenName(), rcs);
		container.setChoiceSet(cs);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		AssociatedChanges<ChooseActionContainer> grantChanges =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					pcc, ChooseActionContainer.class);
		if (grantChanges == null)
		{
			return null;
		}
		if (!grantChanges.hasAddedItems())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> addStrings = new ArrayList<String>();
		for (LSTWriteable lstw : grantChanges.getAdded())
		{
			ChooseActionContainer container = (ChooseActionContainer) lstw;
			if (!getTokenName().equals(container.getName()))
			{
				context.addWriteMessage("Unexpected CHOOSE container found: "
					+ container.getName());
				continue;
			}
			ChoiceSet<?> cs = container.getChoiceSet();
			if (SPELLLIST_CLASS.equals(cs.getChoiceClass()))
			{
				Formula f =
						container.getAssociation(AssociationKey.CHOICE_COUNT);
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getTokenName()
						+ " Count");
					return null;
				}
				addStrings.add(f.toString() + "|" + cs.getLSTformat());

				// assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}
}
