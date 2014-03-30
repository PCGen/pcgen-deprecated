/*
 * Logging.java
 * Copyright 2003 (C) Jonas Karlsson <jujutsunerd@sf.net>
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
 * Created on April 12, 2003, 3:20 AM
 */
package pcgen.util;

import pcgen.rules.context.LoadContext;
import pcgen.system.LanguageBundle;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.lang.SystemUtils;

import pcgen.core.SettingsHandler;

/**
 * This contains logging functions. It is a proxy for the 
 * Java logging API.
 * 
 * @author     Jonas Karlsson <jujutsunerd@sf.net>
 * @version    $Revision$
 */
public class Logging
{
	private static boolean debugMode = false;
	private static final Toolkit s_TOOLKIT = Toolkit.getDefaultToolkit();

	/** Log level for error output. */
	public static final Level ERROR = Level.SEVERE;

	/** Log level for LST error output. */
	public static final Level LST_ERROR = PCGenLogLevel.LST_ERROR;

	/** Logging level for code warnings. */
	public static final Level WARNING = Level.WARNING;

	/** Logging level for LST warnings such as deprecated syntax use. */
	public static final Level LST_WARNING = PCGenLogLevel.LST_WARNING;

	/** Logging level for code info. */
	public static final Level INFO = Level.INFO;

	/** Logging level for LST information such as references to missing items in PRE or CHOOSE tags. */
	public static final Level LST_INFO = PCGenLogLevel.LST_INFO;

	/** Log level for application debug output. */
	public static final Level DEBUG = Level.FINER;

	private static Logger pcgenLogger;
	private static Logger pluginLogger;

	/**
	 * Do any required initialization of the Logger.
	 */
	static
	{
		// Set a default configuration file if none was specified.
		Properties p = System.getProperties();
		File propsFile =
				new File(SystemUtils.USER_DIR + File.separator
					+ "logging.properties");
		if (!propsFile.exists())
		{
			propsFile = new File("logging.properties");
		}
		if (propsFile.exists()
			&& null == p.get("java.util.logging.config.file"))
		{
			p.put("java.util.logging.config.file", propsFile.getAbsolutePath());
		}
		//System.out.println("Using log settings from " + propsFile.getAbsolutePath());

		// Get Java Logging to read in the config. 
		try
		{
			LogManager.getLogManager().readConfiguration();
		}
		catch (SecurityException e)
		{
			System.err
				.println("Failed to read logging configuration. Error was:");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err
				.println("Failed to read logging configuration. Error was:");
			e.printStackTrace();
		}
	}

	/**
	 * Set debugging state: <code>true</code> is on.
	 *
	 * @param argDebugMode boolean debugging state
	 */
	public static void setDebugMode(final boolean argDebugMode)
	{
		retainRootLoggers();

		debugMode = argDebugMode;
		if (debugMode)
		{
			Logger.getLogger("pcgen").setLevel(DEBUG);
			Logger.getLogger("plugin").setLevel(DEBUG);
		}
		else
		{
			Logger.getLogger("pcgen").setLevel(LST_WARNING);
			Logger.getLogger("plugin").setLevel(LST_WARNING);
		}
	}

	/**
	 * Ensure that our root loggers (pcgen and plugin) do not get garbage 
	 * collected, otherwise we lose the logging level!
	 */
	private static void retainRootLoggers()
	{
		pcgenLogger = Logger.getLogger("pcgen");
		pluginLogger = Logger.getLogger("plugin");
	}

	/**
	 * Is someone debugging PCGen?
	 *
	 * @return boolean debugging state
	 */
	public static boolean isDebugMode()
	{
		return debugMode;
	}

	/**
	 * Check if the level of logs would be output for the caller. This can 
	 * be used to prevent building logging output if it will not be used. 
	 * @param level The logging level to be checked.
	 * @return true if the level would be output, false if not.
	 */
	public static boolean isLoggable(Level level)
	{
		Logger l = getLogger();
		return l != null && l.isLoggable(level);
	}
	
	/**
	 * Print information message if PCGen is debugging.
	 *
	 * @param s String information message
	 */
	public static void debugPrint(final String s)
	{
		Logger l = getLogger();
		if (l != null && l.isLoggable(DEBUG))
		{
			l.log(DEBUG, s);
		}
	}

	/**
	 * Print information message if PCGen is debugging.
	 *
	 * @param param1 String information message (usually variable)
	 * @param param2 Object information message (usually value)
	 */
	public static void debugPrint(final String param1, Object param2)
	{
		Logger l = getLogger();
		if (l.isLoggable(DEBUG))
		{
			l.log(DEBUG, param1 + param2);
		}
	}

	/**
	 * Print localised information message if PCGen is debugging.
	 *
	 * @param message String information message (usually variable)
	 * @param param Object information message (usually value) lis
	 */
	public static void debugPrintLocalised(final String message, Object... params)
	{
		Logger l = getLogger();
		if (l.isLoggable(DEBUG))
		{
			String msg =
					LanguageBundle.getFormattedString(message, params);
			l.log(DEBUG, msg);
		}
	}

	/**
	 * Print the message. Currently quietly discards the Throwable.
	 *
	 * @param s String error message
	 * @param thr Throwable stack frame
	 */
	public static void debugPrint(final String s, final Throwable thr)
	{
		debugPrint(s);
		Logger l = getLogger();
		if (l != null && l.isLoggable(DEBUG))
		{
			thr.printStackTrace(System.err);
		}
	}

	/**
	 * Print a localized error message from the passed in key.  If the
	 * application is in Debug mode will also issue a beep.
	 *
	 * @param aKey A key for the localized string in the language bundle
	 */
	public static void errorPrintLocalised(final String aKey)
	{
		if (isDebugMode())
		{
			s_TOOLKIT.beep();
		}

		final String msg = LanguageBundle.getString(aKey);
		System.err.println(msg);
	}

	/**
	 * Print a localized error message including parameter substitution. The
	 * method will issue a beep if the application is running in Debug mode.
	 * <p>
	 * This method accepts a variable number of parameters and will replace
	 * <code>{argno}</code> in the string with each passed paracter in turn.
	 * 
	 * @param aKey
	 *            A key for the localized string in the language bundle
	 * @param varargs
	 *            Variable number of parameters to substitute into the string
	 */
	public static void errorPrintLocalised(final String aKey, Object... varargs)
	{
		if (isDebugMode())
		{
			s_TOOLKIT.beep();
		}

		final String msg = LanguageBundle.getFormattedString(aKey, varargs);
		Logger l = getLogger();
		if (l.isLoggable(ERROR))
		{
			l.log(ERROR, msg);
		}
	}

	/**
	 * Beep and print error message if PCGen is debugging.
	 *
	 * @param s String error message
	 */
	public static void deprecationPrint(final String s)
	{
		deprecationPrint(s, null);
	}

	/**
	 * Beep and print error message if PCGen is debugging.
	 *
	 * @param s String error message
	 * @param context the LoadContext containing the deprecated resource 
	 */
	public static void deprecationPrint(final String s,
		final LoadContext context)
	{
		if (isDebugMode())
		{
			s_TOOLKIT.beep();
		}
		
		Logger l = getLogger();
		if (l.isLoggable(LST_WARNING)
			&& SettingsHandler.outputDeprecationMessages())
		{
			if (context != null && context.getObjectContext() != null
				&& context.getObjectContext().getSourceURI() != null)
			{
				l.log(LST_WARNING, s + " (Source: "
					+ context.getObjectContext().getSourceURI() + " )");
			}
			else
			{
				l.log(LST_WARNING, s);
			}
		}
	}

	/**
	 * Report where an issue was encountered.
	 *
	 * @param context the LoadContext containing the resource 
	 */
	public static void reportSource(final Level lvl, final LoadContext context)
	{
		Logger l = getLogger();
		if (l.isLoggable(lvl))
		{
			if (context != null && context.getObjectContext() != null
				&& context.getObjectContext().getSourceURI() != null)
			{
				l.log(lvl, " (Source: "
					+ context.getObjectContext().getSourceURI() + " )");
			}
			else
			{
				l.log(lvl, " (Source unknown)");
			}
		}
	}

	/**
	 * Report where an issue was encountered.
	 *
	 * @param sourceUri the source containing the resource 
	 */
	public static void reportSource(final Level lvl, final URI sourceUri)
	{
		Logger l = getLogger();
		if (l.isLoggable(lvl))
		{
			if (sourceUri != null)
			{
				l.log(lvl, " (Source: " + sourceUri + ")");
			}
			else
			{
				l.log(lvl, " (Source unknown)");
			}
		}
	}

	/**
	 * Beep and print error message if PCGen is debugging.
	 *
	 * @param s String error message
	 */
	public static void errorPrint(final String s)
	{
		if (isDebugMode())
		{
			s_TOOLKIT.beep();
		}

		Logger l = getLogger();
		if (l.isLoggable(ERROR))
		{
			l.log(ERROR, s);
		}
	}

	/**
	 * Beep and print error message if PCGen is debugging.
	 *
	 * @param s String error message
	 * @param params Varargs list of parameters for substitution into the 
	 * error message.
	 */
	public static void errorPrint(final String s, final Object... params)
	{
		if (isDebugMode())
		{
			s_TOOLKIT.beep();
		}

		Logger l = getLogger();
		if (l.isLoggable(ERROR))
		{
			l.log(ERROR, s, params);
		}
	}
	
	/**
	 * Beep and print error message if PCGen is debugging.
	 *
	 * @param s String error message
	 * @param context the LoadContext containing the deprecated resource
	 */
	public static void errorPrint(final String s, final LoadContext context)
	{
		if (isDebugMode())
		{
			s_TOOLKIT.beep();
		}

		Logger l = getLogger();
		if (l.isLoggable(ERROR))
		{
			if (context != null && context.getObjectContext() != null
				&& context.getObjectContext().getSourceURI() != null)
			{
				l.log(ERROR, s + " (Source: "
					+ context.getObjectContext().getSourceURI() + " )");
			}
			else
			{
				l.log(ERROR, s);
			}
		}
	}

	/**
	 * Print error message with a stack trace if PCGen is
	 * debugging.
	 *
	 * @param s String error message
	 * @param thr Throwable stack frame
	 */
	public static void errorPrint(final String s, final Throwable thr)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		thr.printStackTrace(ps);
		errorPrint(s + "\n" + baos.toString());
	}

	/**
	 * Log a message, if logging is enabled at the
	 * supplied level of detail. 
	 * 
	 * @param lvl The detail level of the message
	 * @param msg String message
	 */
	public static void log(final Level lvl, final String msg)
	{
		Logger l = getLogger();
		if (l.isLoggable(lvl))
		{
			l.log(lvl, msg);
		}
	}

	/**
	 * Log a message with a stack trace, if logging is enabled at the
	 * supplied level of detail. 
	 * 
	 * @param lvl The detail level of the message
	 * @param msg String message
	 * @param thr Throwable stack frame
	 */
	public static void log(final Level lvl, final String msg,
		final Throwable thr)
	{
		Logger l = getLogger();
		if (l.isLoggable(lvl))
		{
			l.log(lvl, msg, thr);
		}
	}

	/**
	 * Log a message with a stack trace, if logging is enabled at the
	 * supplied level of detail. 
	 * This is mainly for use with the pcgen.rules.persistence.token.ParseResult class.
	 * 
	 * @param lvl        The detail level of the message
	 * @param msg        String message
	 * @param stackTrace The stack trace
	 */
	public static void log(Level lvl, String msg, StackTraceElement[] stackTrace)
	{
		Logger l = getLogger();
		if (l.isLoggable(lvl))
		{
			l.log(lvl, msg, stackTrace);
		}
	}

	/**
	 * Print error message with a stack trace if PCGen is
	 * debugging.
	 *
	 * @param s String error message
	 * @param thr Throwable stack frame
	 */
	public static void errorPrintLocalised(final String s, final Throwable thr)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		thr.printStackTrace(ps);
		errorPrint(LanguageBundle.getString(s) + "\n" + baos.toString());
	}

	/**
	 * Report to the console on the current memory sitution.
	 */
	public static void memoryReport()
	{
		System.out.println(memoryReportStr());
	}

	/**
	 * Generate the memory report string
	 * @return the memory report string
	 */
	public static String memoryReportStr()
	{
		Runtime rt = Runtime.getRuntime();
		NumberFormat numFmt = NumberFormat.getNumberInstance();
		StringBuilder sb = new StringBuilder("Memory: ");
		sb.append(numFmt.format(rt.totalMemory() / 1024.0));
		sb.append("Kb total, ");
		sb.append(numFmt.format(rt.freeMemory() / 1024.0));
		sb.append("Kb free, ");
		sb.append(numFmt.format(rt.maxMemory() / 1024.0));
		sb.append("Kb max.");
		return sb.toString();
	}

	/**
	 * Retrieve a Logger object with the specified name. Generally 
	 * this name should be either the fully qualified class name, 
	 * or the package name.
	 * 
	 * @param name The name of the logger
	 * @return An instance of Logger that deals with the specified name.
	 */
	private static java.util.logging.Logger getLogger()
	{
		StackTraceElement[] stack = new Throwable().getStackTrace();
		StackTraceElement caller = null;

		for (int i = 1; i < stack.length; i++) //1 to skip this method
		{
			if (!"pcgen.util.Logging".equals(stack[i].getClassName()))
			{
				caller = stack[i];
				break;
			}
		}

		String name =
				(caller == null/*just in case*/) ? "" : caller.getClassName();

		Logger l = null;
		final int maxRetries = 15;
		int retries = 0;
		while (l == null && retries < maxRetries)
		{
			l = java.util.logging.Logger.getLogger(name);
			retries++;
		}
		if (l == null)
		{
			System.err.println("Unable to get logger for " + name + " after "
				+ retries + " atempts.");
		}
		return l;
	}

	/**
	 * List the current stack of all threads to STDOUT. 
	 */
	public static void reportAllThreads()
	{
		Map<Thread, StackTraceElement[]> allThreads =
				Thread.getAllStackTraces();
		StringBuilder b = new StringBuilder();
		for (Thread t : allThreads.keySet())
		{
			b.append("Thread: ");
			b.append(t.getName());
			b.append(", stacktrace:\n");
			StackTraceElement[] traces = allThreads.get(t);
			for (StackTraceElement element : traces)
			{
				b.append("  ");
				b.append(element.toString());
				b.append("\n");
			}

		}
		System.out.println("==== Thread listing ====");
		System.out.println(b);
		System.out.println("===== end listing  =====");
	}

	/**
	 * Register a new log handler.
	 * @param handler The handler to be registered.
	 */
	public static void registerHandler(Handler handler)
	{
		Logger.getLogger("").addHandler(handler);
	}

	/**
	 * Removes a log handler.
	 * @param handler The handler to be removed.
	 */
	public static void removeHandler(Handler handler)
	{
		Logger.getLogger("").removeHandler(handler);
	}

	/**
	 * Return a list of the supported logging levels in 
	 * descending order of rank.
	 * @return List of logging levels.
	 */
	public static List<Level> getLoggingLevels()
	{
		List<Level> levels = new ArrayList<Level>();
		levels.add(ERROR);
		levels.add(LST_ERROR);
		levels.add(WARNING);
		levels.add(LST_WARNING);
		levels.add(INFO);
		levels.add(LST_INFO);
		levels.add(DEBUG);
		return levels;
	}

	/**
	 * @return The current logging level for the main program.
	 */
	public static Level getCurrentLoggingLevel()
	{
		return Logger.getLogger("pcgen").getLevel();
	}

	/**
	 * Set the current logging level for the main program.
	 * @param level The new level
	 */
	public static void setCurrentLoggingLevel(Level level)
	{
		retainRootLoggers();
		debugMode = (level == Logging.DEBUG);
		Logger.getLogger("pcgen").setLevel(level);
		Logger.getLogger("plugin").setLevel(level);
	}

	private static LinkedList<QueuedMessage> queuedMessages =
			new LinkedList<QueuedMessage>();

	public static void addParseMessage(Level lvl, String msg)
	{
		queuedMessages.add(new QueuedMessage(lvl, msg));
	}

	/*
	 * Temporary method for use with ParseResult conversion.
	 * See pcgen.rules.persistence.token.ParseResult for use.
	 */
	public static void addParseMessage(Level lvl, String msg,
		StackTraceElement[] stack)
	{
		queuedMessages.add(new QueuedMessage(lvl, msg, stack));
	}

	private static int queuedMessageMark = -1;

	public static void markParseMessages()
	{
		queuedMessageMark = queuedMessages.size();
	}

	public static void rewindParseMessages()
	{
		while (queuedMessageMark > -1
			&& queuedMessages.size() > queuedMessageMark)
		{
			queuedMessages.removeLast();
		}
	}

	public static void replayParsedMessages()
	{
		Logger l = getLogger();
		for (QueuedMessage msg : queuedMessages)
		{
			if (l.isLoggable(msg.level))
			{
				l.log(msg.level, msg.message, msg.stackTrace);
			}

		}
		queuedMessageMark = -1;
	}

	public static void clearParseMessages()
	{
		queuedMessageMark = -1;
		queuedMessages.clear();
	}

	private static class QueuedMessage
	{
		public final Level level;
		public final String message;
		public final StackTraceElement[] stackTrace;

		public QueuedMessage(Level lvl, String msg)
		{
			level = lvl;
			message = msg;
			stackTrace = Thread.currentThread().getStackTrace();
		}

		/*
		 * Temporary constructor for use with ParseResult conversion.
		 * See addParseMessage above.
		 */
		public QueuedMessage(Level lvl, String msg, StackTraceElement[] stack)
		{
			level = lvl;
			message = msg;
			stackTrace = stack;
		}
	}
}
