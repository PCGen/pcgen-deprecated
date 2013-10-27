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
import pcgen.cdom.actor.GrantActor;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseActionContainer;
import pcgen.cdom.base.ChooseActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.EquipmentNature;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AutoLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class EquipToken extends AbstractToken implements AutoLstToken,
		CDOMSecondaryToken<CDOMObject>
{

	private static final Class<CDOMEquipment> EQUIPMENT_CLASS = CDOMEquipment.class;

	private static final Integer INTEGER_ONE = Integer.valueOf(1);

	public String getParentToken()
	{
		return "AUTO";
	}

	@Override
	public String getTokenName()
	{
		return "EQUIP";
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
	}

	public boolean parse(PObject target, String value, int level)
	{
		if (level > 1)
		{
			Logging.errorPrint("AUTO:" + getTokenName()
					+ " is not supported on class level lines");
			return false;
		}
		target.addAutoArray(getTokenName(), value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		String equipItems;
		Prerequisite prereq = null; // Do not initialize, null is significant!

		/*
		 * CONSIDER There is the ability to consolidate this PREREQ processing
		 * into AutoLst.java (since it's the same across AUTO SubTokens)
		 */
		// Note: May contain PRExxx
		if (value.indexOf("[") == -1)
		{
			equipItems = value;
		}
		else
		{
			int openBracketLoc = value.indexOf("[");
			equipItems = value.substring(0, openBracketLoc);
			if (!value.endsWith("]"))
			{
				Logging.errorPrint("Unresolved Prerequisite in " + value
						+ " in " + getFullName());
				return false;
			}
			prereq = getPrerequisite(value.substring(openBracketLoc + 1, value
					.length() - 1));
			if (prereq == null)
			{
				Logging.errorPrint("Error generating Prerequisite " + prereq
						+ " in " + getFullName());
				return false;
			}
		}

		if (hasIllegalSeparator('|', equipItems))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(equipItems, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String aProf = tok.nextToken();
			AssociatedPrereqObject apo;
			if ("%LIST".equals(aProf))
			{
				ChooseActionContainer container = obj.getChooseContainer();
				GrantActor<CDOMEquipment> actor = new GrantActor<CDOMEquipment>();
				container.addActor(actor);
				actor.setAssociation(AssociationKey.TOKEN, getFullName());
				apo = actor;
			}
			else
			{
				CDOMReference<CDOMEquipment> ref = TokenUtilities
						.getTypeOrPrimitive(context, EQUIPMENT_CLASS, aProf);
				if (ref == null)
				{
					return false;
				}
				apo = context.getGraphContext().grant(getFullName(), obj, ref);
			}
			if (prereq != null)
			{
				apo.addPrerequisite(prereq);
			}
			apo.setAssociation(AssociationKey.EQUIPMENT_NATURE,
					EquipmentNature.AUTOMATIC);
			apo.setAssociation(AssociationKey.QUANTITY, INTEGER_ONE);
			// TODO Need to account for output index??
			// newEq.setOutputIndex(aList.size());
		}

		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
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
				if (!getFullName().equals(
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

		AssociatedChanges<CDOMReference<CDOMEquipment>> changes = context.getGraphContext()
				.getChangesFromToken(getFullName(), obj, EQUIPMENT_CLASS);
		MapToList<CDOMReference<CDOMEquipment>, AssociatedPrereqObject> mtl = changes
				.getAddedAssociations();
		if (list.isEmpty() && (mtl == null || mtl.isEmpty()))
		{
			// Zero indicates no Token
			return null;
		}
		HashMapToList<Set<Prerequisite>, LSTWriteable> m = new HashMapToList<Set<Prerequisite>, LSTWriteable>();
		for (CDOMReference<CDOMEquipment> lstw : mtl.getKeySet())
		{
			for (AssociatedPrereqObject assoc : mtl.getListFor(lstw))
			{
				m.addToListFor(new HashSet<Prerequisite>(assoc
						.getPrerequisiteList()), lstw);
			}
		}

		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			StringBuilder sb = new StringBuilder();
			sb.append(ReferenceUtilities.joinLstFormat(m.getListFor(prereqs),
					Constants.PIPE));
			if (prereqs != null && !prereqs.isEmpty())
			{
				if (prereqs.size() > 1)
				{
					context.addWriteMessage("Error: "
							+ obj.getClass().getSimpleName()
							+ " had more than one Prerequisite for "
							+ getFullName());
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
				sb.append('[').append(swriter.toString()).append(']');
			}
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
