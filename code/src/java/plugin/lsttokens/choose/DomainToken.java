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
import pcgen.cdom.base.Constants;
import pcgen.cdom.choice.AnyChooser;
import pcgen.cdom.choice.CompoundOrChooser;
import pcgen.cdom.choice.PCChooser;
import pcgen.cdom.choice.QualifyChooser;
import pcgen.cdom.choice.SetChooser;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.Domain;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class DomainToken implements ChooseLstToken
{

	public boolean parse(PObject po, String value)
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
		po.setChoiceString(value);
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
			return AnyChooser.getAnyChooser(Domain.class);
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
				list.add(PCChooser.getPCChooser(Domain.class));
			}
			else if (Constants.LST_QUALIFY.equals(tokString))
			{
				list.add(QualifyChooser.getQualifyChooser(Domain.class));
			}
			// TODO Need to do DEITY=
			else
			{
				domainlist.add(context.ref.getCDOMReference(Domain.class,
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
			listSet = new SetChooser<Domain>(domainlist);
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
