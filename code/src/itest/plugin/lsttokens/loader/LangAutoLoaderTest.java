/*
 * SaLstLoaderTest.java
 * Copyright 2007 (C) Koen Van Daele
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
 * Created on 17-aug-07
 *
 * $Id: $
 */
package plugin.lsttokens.loader;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.LangautoLst;
import plugin.lsttokens.loader.testsupport.AbstractTokenLoaderTestCase;

/**
 * <code>LangAutoLstLoaderTest</code> tests LANGAUTO tags in both Runtime and Editor contexts.
 *
 * @author Koen Van Daele <vandaelek@users.sourceforge.net>
 * @version $Revision$
 */
public class LangAutoLoaderTest extends AbstractTokenLoaderTestCase{
	static GlobalLstToken token = new LangautoLst();
	static PCTemplateLoader loader = new PCTemplateLoader();
	
	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}
	
	public String[] getTestArray()
	{
		return new String[] {"Gnome" , "Aspalanisch", "Common"};
	}
	
	protected boolean isClearable()
	{
		return true;
	}
	
	protected boolean isDotClearable()
	{
		return false;
	}
}
