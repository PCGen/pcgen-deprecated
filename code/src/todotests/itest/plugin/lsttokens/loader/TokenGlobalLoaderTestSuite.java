/*
 * TokenGlobalLoaderTestSuite.java
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
 * Created on 1-aug-07
 *
 * $Id: $
 */
package itest.plugin.lsttokens.loader;

import itest.plugin.lsttokens.loader.VisionLstLoaderTest;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * <code>TokenGlobalLoaderTestSuite</code> is a test suite to test the integration of
 * Global Tokens and Context.
 *
 * @author Koen Van Daele <vandaelek@users.sourceforge.net>
 * @version $Revision$
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({VisionLstLoaderTest.class})
public class TokenGlobalLoaderTestSuite extends TestSuite{
	// No contents, see annotations
}
