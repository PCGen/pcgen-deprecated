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
package plugin.lsttokens.auto;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.content.AutomaticActionContainer;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.ChooseActor;
import pcgen.cdom.helper.CompoundOrChoiceSet;
import pcgen.cdom.helper.GrantActor;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.core.PObject;
import pcgen.core.ShieldProf;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.AutoLstToken;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Logging;

public class ShieldProfToken extends AbstractToken implements AutoLstToken
{

	private static final Class<ShieldProf> SHIELDPROF_CLASS = ShieldProf.class;

	@Override
	public String getTokenName()
	{
		return "SHIELDPROF";
	}

	public boolean parse(PObject target, String value, int level)
	{
		if (level > 1)
		{
			Logging.errorPrint("AUTO:" + getTokenName()
					+ " is not supported on class level lines");
			return false;
		}
		if (value.startsWith("TYPE"))
		{
			Logging.deprecationPrint("TYPE= in AUTO:" + getTokenName()
					+ " is deprecated.  " + "Use SHIELDTYPE=");
		}
		target.addAutoArray(getTokenName(), value);
		return true;
	}

	public boolean parse(LoadContext context, PObject obj, String value)
	{
		String shieldProfs;
		Prerequisite prereq = null; // Do not initialize, null is significant!

		// Note: May contain PRExxx
		if (value.indexOf("[") == -1)
		{
			shieldProfs = value;
		}
		else
		{
			int openBracketLoc = value.indexOf("[");
			shieldProfs = value.substring(0, openBracketLoc);
			if (!value.endsWith("]"))
			{
				Logging.errorPrint("Unresolved Prerequisite in "
						+ getTokenName() + " " + value + " in "
						+ getTokenName());
				return false;
			}
			prereq = getPrerequisite(value.substring(openBracketLoc + 1, value
					.length() - 1));
			if (prereq == null)
			{
				Logging.errorPrint("Error generating Prerequisite " + prereq
						+ " in " + getTokenName());
				return false;
			}
		}

		if (hasIllegalSeparator('|', shieldProfs))
		{
			return false;
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(shieldProfs, Constants.PIPE);
		List<PrimitiveChoiceSet<ShieldProf>> pcsList = new ArrayList<PrimitiveChoiceSet<ShieldProf>>();
		List<PrereqObject> applyList = new ArrayList<PrereqObject>();

		while (tok.hasMoreTokens())
		{
			String aProf = tok.nextToken();
			if ("%LIST".equals(value))
			{
				ChooseActionContainer container = obj.getChooseContainer();
				GrantActor<ShieldProf> actor = new GrantActor<ShieldProf>();
				container.addActor(actor);
				actor.setAssociation(AssociationKey.TOKEN, getTokenName());
				applyList.add(actor);
			}
			else
			{
				if (Constants.LST_ALL.equalsIgnoreCase(aProf))
				{
					foundAny = true;
					CDOMReference<ShieldProf> ref = context.ref
							.getCDOMAllReference(SHIELDPROF_CLASS);
					AssociatedPrereqObject edge = context.getGraphContext()
							.grant(getTokenName(), obj, ref);
					applyList.add(edge);
				}
				else if (aProf.startsWith("SHIELDTYPE="))
				{
					foundOther = true;
					PrimitiveChoiceSet<ShieldProf> pcs = ChooseLoader
							.getQualifier(context, SHIELDPROF_CLASS,
									"EQUIPMENT", aProf.substring(5));
					pcsList.add(pcs);
				}
				else if (aProf.startsWith("TYPE="))
				{
					Logging.errorPrint(aProf + " is prohibited in AUTO:"
							+ getTokenName() + ". Do you mean SHIELDTYPE=?");
					return false;
				}
				else
				{
					foundOther = true;
					CDOMReference<ShieldProf> ref = context.ref
							.getCDOMReference(SHIELDPROF_CLASS, aProf);
					if (ref == null)
					{
						return false;
					}
					AssociatedPrereqObject edge = context.getGraphContext()
							.grant(getTokenName(), obj, ref);
					applyList.add(edge);
				}
			}
		}

		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		PrimitiveChoiceSet<ShieldProf> pcs;
		if (pcsList.size() == 1)
		{
			pcs = pcsList.get(0);
		}
		else
		{
			pcs = new CompoundOrChoiceSet<ShieldProf>(pcsList);
		}

		ChoiceSet<ShieldProf> cs = new ChoiceSet<ShieldProf>("AUTO:SHIELDPROF",
				pcs);
		AutomaticActionContainer aac = new AutomaticActionContainer(
				"AUTO:SHIELDPROF");
		aac.setChoiceSet(cs);
		aac.addActor(new GrantActor<ShieldProf>());
		AssociatedPrereqObject edge = context.getGraphContext().grant(
				getTokenName(), obj, aac);
		applyList.add(edge);

		if (prereq != null)
		{
			for (PrereqObject pro : applyList)
			{
				pro.addPrerequisite(prereq);
			}
		}

		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
	{
		List<String> list = new ArrayList<String>();
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();

		ChooseActionContainer container = obj.getChooseContainer();
		Collection<ChooseActor> actors = container.getActors();
		for (ChooseActor actor : actors)
		{
			if (actor instanceof GrantActor)
			{
				GrantActor<?> ga = GrantActor.class.cast(actor);
				if (!getTokenName().equals(
						ga.getAssociation(AssociationKey.TOKEN)))
				{
					continue;
				}
				StringBuilder sb = new StringBuilder();
				sb.append("%LIST");
				List<Prerequisite> prereqList = ga.getPrerequisiteList();
				if (prereqList != null && !prereqList.isEmpty())
				{
					String prereqs = getPrerequisiteString(context, prereqList);
					sb.append('[').append(prereqs).append(']');
				}
				list.add(sb.toString());
			}
		}

		AssociatedChanges<ShieldProf> changes = context.getGraphContext()
				.getChangesFromToken(getTokenName(), obj, SHIELDPROF_CLASS);
		if (list.isEmpty() && changes == null)
		{
			return null;
		}
		MapToList<LSTWriteable, AssociatedPrereqObject> mtl = changes
				.getAddedAssociations();
		if (list.isEmpty() && (mtl == null || mtl.isEmpty()))
		{
			// Zero indicates no Token
			return null;
		}
		HashMapToList<Set<Prerequisite>, LSTWriteable> m = new HashMapToList<Set<Prerequisite>, LSTWriteable>();
		for (LSTWriteable ab : mtl.getKeySet())
		{
			List<AssociatedPrereqObject> assocList = mtl.getListFor(ab);
			if (assocList.size() != 1)
			{
				context
						.addWriteMessage("Only one Association to a CHOOSE can be made per object");
				return null;
			}
			AssociatedPrereqObject assoc = assocList.get(0);
			m.addToListFor(new HashSet<Prerequisite>(assoc
					.getPrerequisiteList()), ab);
		}

		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			String ab = ReferenceUtilities.joinLstFormat(m.getListFor(prereqs),
					Constants.PIPE);
			if (prereqs != null && !prereqs.isEmpty())
			{
				if (prereqs.size() > 1)
				{
					context.addWriteMessage("Error: "
							+ obj.getClass().getSimpleName()
							+ " had more than one Prerequisite for "
							+ getTokenName());
					return null;
				}
				Prerequisite p = prereqs.iterator().next();
				StringWriter swriter = new StringWriter();
				try
				{
					prereqWriter.write(swriter, p);
				}
				catch (PersistenceLayerException e)
				{
					context.addWriteMessage("Error writing Prerequisite: " + e);
					return null;
				}
				ab = ab + '[' + swriter.toString() + ']';
			}
			list.add(ab);
		}
		return list.toArray(new String[list.size()]);
	}
}
