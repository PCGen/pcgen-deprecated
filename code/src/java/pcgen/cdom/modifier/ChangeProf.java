/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.modifier;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.Modifier;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.cdom.reference.CDOMGroupRef;

public class ChangeProf extends ConcretePrereqObject implements
		Modifier<CDOMWeaponProf>, LSTWriteable
{

	private final CDOMReference<CDOMWeaponProf> source;

	private final CDOMGroupRef<CDOMWeaponProf> result;

	public ChangeProf(CDOMReference<CDOMWeaponProf> sourceProf,
		CDOMGroupRef<CDOMWeaponProf> resultType)
	{
		if (sourceProf == null)
		{
			throw new IllegalArgumentException(
				"Source Prof for ChangeProf cannot be null");
		}
		if (resultType == null)
		{
			throw new IllegalArgumentException(
				"Resulting Prof Type for ChangeProf cannot be null");
		}
		source = sourceProf;
		result = resultType;
	}

	public CDOMWeaponProf applyModifier(CDOMWeaponProf obj)
	{
		if (source.contains(obj))
		{
			// TODO BUG This is a hack to allow me to load :)
			return null;
			// return result;
		}
		return obj;
	}

	public Class<CDOMWeaponProf> getModifiedClass()
	{
		return CDOMWeaponProf.class;
	}

	public CDOMReference<CDOMWeaponProf> getSource()
	{
		return source;
	}

	public CDOMGroupRef<CDOMWeaponProf> getResult()
	{
		return result;
	}

	@Override
	public int hashCode()
	{
		return 31 * source.hashCode() + result.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (!(o instanceof ChangeProf))
		{
			return false;
		}
		ChangeProf other = (ChangeProf) o;
		return source.equals(other.source) && result.equals(other.result);
	}

	public String getLSTformat()
	{
		// FIXME Hack for back Generics in GraphChangs
		return source.getLSTformat() + " " + result.getLSTformat();
	}
}
