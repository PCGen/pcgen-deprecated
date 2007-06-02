/*
 * PrerequisiteWriterFactory.java
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision: 1821 $
 *
 * Last Editor: $Author: jdempsey $
 *
 * Last Edited: $Date: 2006-12-28 01:12:38 -0500 (Thu, 28 Dec 2006) $
 *
 */
package plugin.pretokens.writer;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.AbstractPrerequisiteWriter;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;

import java.io.IOException;
import java.io.Writer;

public class PreSkillTotWriter extends AbstractPrerequisiteWriter implements
		PrerequisiteWriterInterface
{

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#kindHandled()
	 */
	public String kindHandled()
	{
		return "skill.tot";
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#operatorsHandled()
	 */
	public PrerequisiteOperator[] operatorsHandled()
	{
		return new PrerequisiteOperator[]{PrerequisiteOperator.GTEQ,
			PrerequisiteOperator.LT};
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#write(java.io.Writer, pcgen.core.prereq.Prerequisite)
	 */
	public void write(Writer writer, Prerequisite prereq)
		throws PersistenceLayerException
	{
		checkValidOperator(prereq, operatorsHandled());

		try
		{
			if (prereq.getOperator().equals(PrerequisiteOperator.LT))
			{
				writer.write('!');
			}

			writer.write("PRESKILLTOT:" + (prereq.isOverrideQualify() ? "Q:":"") + "1,");
			writer.write(prereq.getKey());
			if (prereq.getSubKey() != null)
			{
				writer.write(" (");
				writer.write(prereq.getSubKey());
				writer.write(')');
			}
			writer.write("=");
			writer.write(prereq.getOperand());
		}
		catch (IOException e)
		{
			throw new PersistenceLayerException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.AbstractPrerequisiteWriter#specialCase(java.io.Writer writer, pcgen.core.prereq.Prerequisite prereq)
	 */
	@Override
	public boolean specialCase(Writer writer, Prerequisite prereq)
		throws IOException
	{
		//
		// If this is a PREMULT with all PRESPELLTYPEs ...
		//
		if (checkForPremultOfKind(prereq, kindHandled(), true))
		{
			if (prereq.getOperator().equals(PrerequisiteOperator.LT))
			{
				writer.write('!');
			}
			writer.write("PRESKILLTOT:");
			writer.write(prereq.isOverrideQualify() ? "Q:":"");

			boolean needComma = false;
			for (Prerequisite subreq : prereq.getPrerequisites())
			{
				if (needComma)
				{
					writer.write(',');
				}
				needComma = true;
				writer.write(subreq.getKey());
			}
			writer.write('=');
			writer.write(prereq.getOperand());
			return true;
		}
		return false;
	}
}
