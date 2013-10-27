/*
 * SourceLogFormatter.java
 * Copyright 2007 (C) James Dempsey
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
 * Created on 17/06/2007
 *
 * $Id$
 */
package pcgen.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

/**
 * <code>SourceLogFormatter</code> is a log formater for the Java
 * Loggings API that ignores the call from the PCGen logging class.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public final class SourceLogFormatter extends Formatter
{
	private static final char SEPERATOR = ' ';
	private static final SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.S");
	private final Date date = new Date(); 
	private static final Pattern javaExtPattern = Pattern.compile("\\.java");
	
	/* (non-Javadoc)
	 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
	 */
	@Override
	public String format(LogRecord record)
	{
		StringBuffer sb = new StringBuffer();
		
		date.setTime(record.getMillis());
		sb.append(df.format(date));
		
		sb.append(SEPERATOR);
		sb.append(String.valueOf(record.getLevel()));
		sb.append(SEPERATOR);
		sb.append(Thread.currentThread().getName());
		sb.append(SEPERATOR);

		Object[] params = record.getParameters();
		StackTraceElement[] stack;
		if (params != null && params instanceof StackTraceElement[])
		{
			stack = (StackTraceElement[]) params;
		}
		else
		{
			stack = Thread.currentThread().getStackTrace();
		}
		// Pick out the caller from the stack trace, ignoring the 
		// logging classes themselves 
		StackTraceElement caller = null;		
		
		for (int i=0 ; i<stack.length ; i++) //1 to skip this method
		{
			String className = stack[i].getClassName();
			if (!className.startsWith("java.lang.Thread")
				&& !className.startsWith("pcgen.util.Logging")
				&& !className.startsWith("pcgen.util.SourceLogFormatter")
				&& !className.startsWith("java.util.logging"))
			{
				caller = stack[i];
				break;
			}
		}
		
		if (caller!=null) 
		{
			if (caller.getLineNumber()>=0)
			{
				sb.append(javaExtPattern.matcher(caller.getFileName()).replaceFirst(""));
				sb.append(':');
				sb.append(caller.getLineNumber());
			}
			else
			{
				sb.append(caller.getClassName());
				sb.append(' ');
				sb.append(caller.getMethodName());
			}
		}

		sb.append(SEPERATOR);
		
		sb.append(formatMessage(record));
		
		if (record.getThrown()!=null)
		{
			sb.append('\n');
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			record.getThrown().printStackTrace(pw);
			pw.flush();
			sb.append(sw);
		}
		
		sb.append('\n');
		
		return sb.toString();
	}
}
