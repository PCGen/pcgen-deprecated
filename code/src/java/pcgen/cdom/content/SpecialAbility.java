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
package pcgen.cdom.content;


public class SpecialAbility extends TextProperty
{

	private final String property;

	public SpecialAbility(String s)
	{
		super();
		property = s;
	}

	public String toLSTform()
	{
		/*
		 * TODO FIXME This hack only works because variables are not used!
		 */
		return property;
	}

	@Override
	public int hashCode()
	{
		return property.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof SpecialAbility)
		{
			SpecialAbility sa = (SpecialAbility) o;
			return sa.property.equals(property) && super.matchesFormulaList(sa);
		}
		return false;
	}
}
