/*
 * Copyright (c) Thomas Parker, 2005.
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.graph.testsupport;

import java.util.Arrays;
import java.util.Collections;

import pcgen.base.graph.core.DefaultDirectionalHyperEdge;

/**
 * @author Me
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestDirectionalHyperEdge<T> extends DefaultDirectionalHyperEdge<T> {

	public TestDirectionalHyperEdge(T source, T[] sinks) {
		super(Collections.singletonList(source), Arrays.asList(sinks));
	}

	public String toString() {
		return "TDE: " + this.getSourceNodes() + " " + this.getSinkNodes();
	}
}