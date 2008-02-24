/*
 * VisionLstLoaderTest.java
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
 * Created on 31-jul-07
 *
 * $Id: $
 */
package plugin.lsttokens.loader;

import java.net.URI;

import pcgen.cdom.inst.CDOMTemplate;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.rules.context.EditorLoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.VisionLst;
import plugin.lsttokens.loader.testsupport.AbstractTokenLoaderTestCase;

/**
 * <code>VisionLstLoaderTest</code> is ...
 *
 * @author Koen Van Daele <vandaelek@users.sourceforge.net>
 * @version $Revision$
 */
public class VisionLstLoaderTest extends AbstractTokenLoaderTestCase{
	static VisionLst token = new VisionLst();
	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);
	
	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public VisionLst getToken()
	{
		return token;
	}
	
	public String[] getTestArray()
	{
		return new String[] {"Darkvision (25')" , "Normal (30')"};
	}
	
	protected boolean isClearable()
	{
		return true;
	}
	
	protected boolean isDotClearable()
	{
		return true;
	}
	

	public void testEditorContextMultipleSources() throws PersistenceLayerException
	{
		context = new EditorLoadContext();
		URI sourceURI = sourceCampaign.getURI();
		URI modURI = modCampaign.getURI();
		context.setSourceURI(sourceURI);
		testParse(context, "Normal (30')");
		context.setSourceURI(modURI);
		testParse(context, "Darkvision (20')");
		testParse(context, ".CLEAR.Normal (30')");
		context.setExtractURI(sourceURI);
		testUnparse(context, "Normal (30')");
		context.setExtractURI(modURI);
		testUnparse(context, ".CLEAR.Normal (30')|Darkvision (20')");
	}
	

}
