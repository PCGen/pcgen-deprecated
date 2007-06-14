/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.choose;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.choice.AnyChooser;
import pcgen.cdom.choice.CompoundOrChooser;
import pcgen.cdom.choice.GrantedChooser;
import pcgen.cdom.choice.ListTransformer;
import pcgen.cdom.choice.ReferenceChooser;
import pcgen.cdom.choice.RemovingChooser;
import pcgen.cdom.filter.PCChoiceFilter;
import pcgen.cdom.filter.QualifyFilter;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.DomainList;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class DomainToken implements ChooseLstToken
{

	private static final Class<Domain> DOMAIN_CLASS = Domain.class;

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value.indexOf('|') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain | : " + value);
			return false;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return false;
		}
		if (value.charAt(0) == ',')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with , : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == ',')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with , : " + value);
			return false;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}
		StringBuilder sb = new StringBuilder();
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName()).append('|').append(value);
		po.setChoiceString(sb.toString());
		return true;
	}

	public String getTokenName()
	{
		return "DOMAIN";
	}

	public ChoiceSet<?> parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (value.indexOf('|') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain | : " + value);
			return null;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return null;
		}
		if (value.charAt(0) == ',')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with , : " + value);
			return null;
		}
		if (value.charAt(value.length() - 1) == ',')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with , : " + value);
			return null;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return null;
		}
		if (Constants.LST_ANY.equals(value))
		{
			return AnyChooser.getAnyChooser(DOMAIN_CLASS);
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		List<ChoiceSet<Domain>> list = new ArrayList<ChoiceSet<Domain>>();
		List<CDOMReference<Domain>> domainlist =
				new ArrayList<CDOMReference<Domain>>();
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			if (Constants.LST_ANY.equals(tokString))
			{
				Logging.errorPrint("Cannot use ANY and another qualifier: "
					+ value);
				return null;
			}
			else if (Constants.LST_PC.equals(tokString))
			{
				list.add(GrantedChooser.getPCChooser(DOMAIN_CLASS));
			}
			else if (Constants.LST_QUALIFY.equals(tokString))
			{
				AnyChooser<Domain> ac = AnyChooser.getAnyChooser(DOMAIN_CLASS);
				RemovingChooser<Domain> rc = new RemovingChooser<Domain>(ac);
				rc.addRemovingChoiceFilter(new QualifyFilter());
				rc.addRemovingChoiceFilter(PCChoiceFilter
					.getPCChoiceFilter(DOMAIN_CLASS));
				list.add(rc);
			}
			else if (tokString.startsWith("DEITY="))
			{
				// TODO need to deal with case insensitivity
				CDOMSimpleSingleRef<DomainList> dl =
						context.ref.getCDOMReference(DomainList.class,
							"*Starting");
				ListTransformer<Domain> dpc =
						new ListTransformer<Domain>(GrantedChooser
							.getPCChooser(Deity.class), dl);
				list.add(dpc);
			}
			else
			{
				domainlist.add(context.ref.getCDOMReference(DOMAIN_CLASS,
					tokString));
			}
		}
		ChoiceSet<Domain> listSet;
		if (domainlist.isEmpty())
		{
			listSet = null;
		}
		else
		{
			listSet = new ReferenceChooser<Domain>(domainlist);
		}
		if (list.isEmpty())
		{
			return listSet;
		}
		else
		{
			CompoundOrChooser<Domain> chooser = new CompoundOrChooser<Domain>();
			chooser.addAllChoiceSets(list);
			if (listSet != null)
			{
				chooser.addChoiceSet(listSet);
			}
			return chooser;
		}
	}
}
