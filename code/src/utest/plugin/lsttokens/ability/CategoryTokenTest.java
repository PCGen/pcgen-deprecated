/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.ability;

import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbilityLoader;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.testsupport.AbstractTypeSafeTokenTestCase;

public class CategoryTokenTest extends AbstractTypeSafeTokenTestCase<Ability>
{

	static CategoryToken token = new CategoryToken();
	static AbilityLoader loader = new AbilityLoader();

	@Override
	public Class<Ability> getCDOMClass()
	{
		return Ability.class;
	}

	@Override
	public LstObjectFileLoader<Ability> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Ability> getToken()
	{
		return token;
	}

	@Override
	public Object getConstant(String string)
	{
		return AbilityCategory.getConstant(string);
	}

	@Override
	public ObjectKey<?> getObjectKey()
	{
		return ObjectKey.CATEGORY;
	}

	@Override
	protected boolean requiresPreconstruction()
	{
		return true;
	}

	@Override
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		expectedPrimaryMessageCount = 1;
		super.testRoundRobinBase();
	}

	@Override
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		expectedPrimaryMessageCount = 1;
		super.testRoundRobinHyphen();
	}

	@Override
	public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
	{
		expectedPrimaryMessageCount = 1;
		super.testRoundRobinNonEnglishAndN();
	}

	@Override
	public void testRoundRobinWithSpace() throws PersistenceLayerException
	{
		expectedPrimaryMessageCount = 1;
		super.testRoundRobinWithSpace();
	}

	@Override
	public void testRoundRobinY() throws PersistenceLayerException
	{
		expectedPrimaryMessageCount = 1;
		super.testRoundRobinY();
	}

	@Override
	public boolean isClearLegal()
	{
		return false;
	}

}
