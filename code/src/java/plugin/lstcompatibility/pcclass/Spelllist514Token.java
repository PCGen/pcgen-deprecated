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
 * Current Ver: $Revision: 3890 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-08-26 23:42:42 -0400 (Sun, 26 Aug 2007) $
 */
package plugin.lstcompatibility.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.actor.GrantActor;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.choiceset.SpellReferenceChoiceSet;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.util.Logging;

/**
 * Class deals with SPELLLIST Token
 */
public class Spelllist514Token extends AbstractToken implements
		CDOMCompatibilityToken<CDOMPCClass>
{

	private static Class<ClassSpellList> SPELLLIST_CLASS = ClassSpellList.class;
	private static Class<DomainSpellList> DOMAINSPELLLIST_CLASS = DomainSpellList.class;

	@Override
	public String getTokenName()
	{
		return "SPELLLIST";
	}

	public boolean parse(LoadContext context, CDOMPCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		if (value.indexOf('|') == -1)
		{
			Logging.errorPrint(getTokenName()
					+ " may not have only one argument");
			return false;
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		int count;
		try
		{
			count = Integer.parseInt(tok.nextToken());
			if (count <= 0)
			{
				Logging.errorPrint("Number in " + getTokenName()
						+ " must be greater than zero: " + value);
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid Number in " + getTokenName() + ": "
					+ value);
			return false;
		}

		List<CDOMReference<? extends CDOMListObject>> refs = new ArrayList<CDOMReference<? extends CDOMListObject>>();
		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<? extends CDOMListObject> ref;
			if (Constants.LST_ALL.equals(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(SPELLLIST_CLASS);
			}
			else if (token.startsWith("DOMAIN."))
			{
				foundOther = true;
				ref = context.ref.getCDOMReference(DOMAINSPELLLIST_CLASS, token
						.substring(7));
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
					+ getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		ChooseActionContainer container = new ChooseActionContainer(
				getTokenName());
		container.addActor(new GrantActor<CDOMListObject<CDOMSpell>>());
		context.getObjectContext().give(getTokenName(), pcc, container);
		container.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
				.getFormulaFor(count));
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
				.getFormulaFor(Integer.MAX_VALUE));

		/*
		 * TODO This is a pretty ugly situation, but is required in the current
		 * design. Real question is how to make it so that a CHOOSER can choose
		 * between two related things without a huge set of contortion... Since
		 * on the back end of this, the Chooser will probably have issues
		 * knowing what to do.
		 */
		PrimitiveChoiceSet<CDOMListObject> rcs = new SpellReferenceChoiceSet(
				refs);
		ChoiceSet<? extends CDOMListObject<CDOMSpell>> cs = new ChoiceSet(
				getTokenName(), rcs);
		container.setChoiceSet(cs);
		return true;
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
