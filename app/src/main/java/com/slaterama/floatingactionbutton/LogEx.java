package com.slaterama.floatingactionbutton;

import android.text.TextUtils;
import android.util.Log;

/**
 * API for sending log output with class, method and other metadata automatically included.
 *
 * <p>Generally, use the LogEx.v() LogEx.d() LogEx.i() LogEx.w() and LogEx.e()
 * methods.
 *
 * <p>The order in terms of verbosity, from least to most is
 * ERROR, WARN, INFO, DEBUG, VERBOSE.  Verbose should never be compiled
 * into an application except during development.  Debug logs are compiled
 * in but stripped at runtime.  Error, warning and info logs are always kept.
 *
 * <p>LogEx allows for the automatic formatting of Log tag and message parameters
 * into a uniform format, and also simplifies the Logging experience by allowing
 * metadata such as class name, file name, method name, line number, etc. to
 * automatically be embedded into every Log call. This can be an expensive process
 * so be sure to call {@link #isLoggable(int)} before every log request. This will allow
 * calls to be programmatically filtered out at via the {@link #setLogLevel(int)} method.</p>
 */
public final class LogEx {

	/*
	 * Static constants
	 */

	/**
	 * The fully-qualified class name of this LogEx class.
	 */
	private static final String LOG_CLASSNAME = LogEx.class.getName();

	/**
	 * Priority constant for the println method; use LogEx.v.
	 */
	public static final int VERBOSE = Log.VERBOSE;

	/**
	 * Priority constant for the println method; use LogEx.d.
	 */
	public static final int DEBUG = Log.DEBUG;

	/**
	 * Priority constant for the println method; use LogEx.i.
	 */
	public static final int INFO = Log.INFO;

	/**
	 * Priority constant for the println method; use LogEx.w.
	 */
	public static final int WARN = Log.WARN;

	/**
	 * Priority constant for the println method; use LogEx.e.
	 */
	public static final int ERROR = Log.ERROR;

	/**
	 * Priority constant for the println method.
	 */
	public static final int ASSERT = Log.ASSERT;

	/*
	 * Static variables
	 */

	/**
	 * The current logging level.
	 *
	 * @see #isLoggable(int)
	 */
	private static int sLogLevel = INFO;

	/**
	 * The format that will be used to generate Log tags.
	 */
	private static String sTagFormat = "%s";

	/**
	 * The arguments that will be used to generate Log tags.
	 */
	private static Object[] sTagArgs = new Object[]{Placeholder.SIMPLE_CLASS_NAME};

	/**
	 * The format that will be used to generate Log messages.
	 */
	private static String sMessageFormat = "%s(%s:%d) %s";

	/**
	 * The arguments that will be used to generate Log messages.
	 */
	private static Object[] sMessageArgs = new Object[]{
			Placeholder.METHOD_NAME,
			Placeholder.FILE_NAME,
			Placeholder.LINE_NUMBER,
			Placeholder.MESSAGE
	};

	/*
	 * Static methods
	 */

	/**
	 * Sets a global Logging level that can be used to determine whether to Log messages.
	 * @param level The logging level.
	 */
	public static void setLogLevel(int level) {
		sLogLevel = level;
	}

	/**
	 * Returns the global logging level that can be used to determine whether to Log messages.
	 * @return The logging level.
	 */
	public static int getLogLevel() {
		return sLogLevel;
	}

	/**
	 * <p>Checks to see whether or not a log is loggable at the specified level. The default level is set to INFO. This means
	 * that any level above and including INFO will be logged. Before you make any calls to a logging method you should check
	 * to see if your tag should be logged.</p>
	 * <p>Not to be confused with {@link #isLoggable(String, int)}, which mirrors {@link android.util.Log#isLoggable(String, int)}.</p>
	 * @param level The level to check.
	 * @return Whether or not that this is allowed to be logged.
	 */
	public static boolean isLoggable(int level) {
		return level >= sLogLevel;
	}

	/**
	 * Checks to see whether or not a log for the specified tag is loggable at the specified level.
	 *
	 *  The default level of any tag is set to INFO. This means that any level above and including
	 *  INFO will be logged. Before you make any calls to a logging method you should check to see
	 *  if your tag should be logged. You can change the default level by setting a system property:
	 *      'setprop log.tag.&lt;YOUR_LOG_TAG> &lt;LEVEL>'
	 *  Where level is either VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT, or SUPPRESS. SUPPRESS will
	 *  turn off all logging for your tag. You can also create a local.prop file that with the
	 *  following in it:
	 *      'log.tag.&lt;YOUR_LOG_TAG>=&lt;LEVEL>'
	 *  and place that in /data/local.prop.
	 *
	 * @param tag The tag to check.
	 * @param level The level to check.
	 * @return Whether or not that this is allowed to be logged.
	 * @throws IllegalArgumentException is thrown if the tag.length() > 23.
	 */
	public static boolean isLoggable(String tag, int level) {
		return Log.isLoggable(tag, level);
	}

	/**
	 * Sets the format and arguments that will be used to generate tag strings in future logging calls.
	 * @param format the format string
	 * @param args the list of arguments passed to the formatter. If there are more arguments than required by format,
	 *             additional arguments are ignored. Arguments of type {@link LogEx.Placeholder}
	 *             will be replaced with the appropriate value when the message is logged.
	 */
	public static void setTagFormat(String format, Object... args) {
		sTagFormat = format;
		sTagArgs = args;
	}

	/**
	 * Sets the format and arguments that will be used to generate message strings in future logging calls.
	 * @param format the format string
	 * @param args the list of arguments passed to the formatter. If there are more arguments than required by format,
	 *             additional arguments are ignored. Arguments of type {@link LogEx.Placeholder}
	 *             will be replaced with the appropriate value when the message is logged.
	 */
	public static void setMessageFormat(String format, Object... args) {
		sMessageFormat = format;
		sMessageArgs = args;
	}

	/**
	 * Returns a localized formatted string, using the supplied format and arguments, using the user's default locale.
	 * @param format the format string
	 * @param args the list of arguments passed to the formatter. If there are more arguments than required by <code>format</code>,
	 *             additional arguments are ignored. Arguments of type {@link LogEx.Placeholder}
	 *             are replaced with the appropriate value from <code>element</code>.
	 * @param element A representation of a single stack frame.
	 * @param str The string (i.e. tag or message) to be logged.
	 * @return The localized formatted string.
	 */
	private static String format(String format, Object[] args, StackTraceElement element, String str) {
		Object[] resolvedArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if (arg instanceof Placeholder) {
				Placeholder placeholder = (Placeholder) arg;
				try {
					switch (placeholder) {
						case CLASS_NAME: {
							Class cls = Class.forName(element.getClassName());
							String name = cls.getName();
							while (TextUtils.isEmpty(name)) {
								cls = cls.getEnclosingClass();
								if (cls == null)
									name = "[Unknown]";
								else
									name = cls.getName();
							}
							resolvedArgs[i] = name;
							break;
						}
						case FILE_NAME:
							resolvedArgs[i] = element.getFileName();
							break;
						case LINE_NUMBER:
							resolvedArgs[i] = element.getLineNumber();
							break;
						case METHOD_NAME:
							resolvedArgs[i] = element.getMethodName();
							break;
						case HASH_CODE:
							resolvedArgs[i] = element.hashCode();
							break;
						case MESSAGE:
							resolvedArgs[i] = str;
							break;
						case SIMPLE_CLASS_NAME: {
							Class cls = Class.forName(element.getClassName());
							String name = cls.getSimpleName();
							while (TextUtils.isEmpty(name)) {
								cls = cls.getEnclosingClass();
								if (cls == null)
									name = "[Unknown]";
								else
									name = cls.getSimpleName();
							}
							resolvedArgs[i] = name;
							break;
						}
						case PACKAGE:
							resolvedArgs[i] = Class.forName(element.getClassName()).getPackage().getName();
							break;
						default:
							resolvedArgs[i] = "[Unknown]";
					}
				} catch (ClassNotFoundException e) {
					resolvedArgs[i] = "[Unknown]";
				}
			} else {
				resolvedArgs[i] = arg;
			}
		}
		return String.format(format, resolvedArgs);
	}

	/**
	 * Send a {@link #VERBOSE} log message.
	 */
	public static int v() {
		return println(VERBOSE, "", "", new LogThrowable());
	}

	/**
	 * Send a {@link #VERBOSE} log message.
	 * @param msg The message you would like logged.
	 */
	public static int v(String msg) {
		return println(VERBOSE, "", msg, new LogThrowable());
	}

	/**
	 * Send a {@link #VERBOSE} log message.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int v(String msg, Throwable tr) {
		return println(VERBOSE, "", msg + '\n' + Log.getStackTraceString(tr), tr);
	}

	/**
	 * Send a {@link #VERBOSE} log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int v(String tag, String msg) {
		return println(VERBOSE, tag, msg, new LogThrowable());
	}

	/**
	 * Send a {@link #VERBOSE} log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int v(String tag, String msg, Throwable tr) {
		return println(VERBOSE, tag, msg + '\n' + Log.getStackTraceString(tr), tr);
	}

	/**
	 * Send a {@link #DEBUG} log message.
	 */
	public static int d() {
		return println(DEBUG, "", "", new LogThrowable());
	}

	/**
	 * Send a {@link #DEBUG} log message.
	 * @param msg The message you would like logged.
	 */
	public static int d(String msg) {
		return println(DEBUG, "", msg, new LogThrowable());
	}

	/**
	 * Send a {@link #DEBUG} log message.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int d(String msg, Throwable tr) {
		return println(DEBUG, "", msg + '\n' + Log.getStackTraceString(tr), tr);
	}

	/**
	 * Send a {@link #DEBUG} log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int d(String tag, String msg) {
		return println(DEBUG, tag, msg, new LogThrowable());
	}

	/**
	 * Send a {@link #DEBUG} log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int d(String tag, String msg, Throwable tr) {
		return println(DEBUG, tag, msg + '\n' + Log.getStackTraceString(tr), tr);
	}

	/**
	 * Send an {@link #INFO} log message.
	 */
	public static int i() {
		return println(INFO, "", "", new LogThrowable());
	}

	/**
	 * Send an {@link #INFO} log message.
	 * @param msg The message you would like logged.
	 */
	public static int i(String msg) {
		return println(INFO, "", msg, new LogThrowable());
	}

	/**
	 * Send an {@link #INFO} log message.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int i(String msg, Throwable tr) {
		return println(INFO, "", msg + '\n' + Log.getStackTraceString(tr), tr);
	}

	/**
	 * Send an {@link #INFO} log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int i(String tag, String msg) {
		return println(INFO, tag, msg, new LogThrowable());
	}

	/**
	 * Send an {@link #INFO} log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int i(String tag, String msg, Throwable tr) {
		return println(INFO, tag, msg + '\n' + Log.getStackTraceString(tr), tr);
	}

	/**
	 * Send a {@link #WARN} log message.
	 */
	public static int w() {
		return println(WARN, "", "", new LogThrowable());
	}

	/**
	 * Send a {@link #WARN} log message.
	 * @param msg The message you would like logged.
	 */
	public static int w(String msg) {
		return println(WARN, "", msg, new LogThrowable());
	}

	/**
	 * Send a {@link #WARN} log message.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int w(String msg, Throwable tr) {
		return println(WARN, "", msg + '\n' + Log.getStackTraceString(tr), tr);
	}

	/**
	 * Send a {@link #WARN} log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int w(String tag, String msg) {
		return println(WARN, tag, msg, new LogThrowable());
	}

	/**
	 * Send a {@link #WARN} log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int w(String tag, String msg, Throwable tr) {
		return println(WARN, tag, msg + '\n' + Log.getStackTraceString(tr), tr);
	}

	/**
	 * Send an {@link #ERROR} log message.
	 */
	public static int e() {
		return println(ERROR, "", "", new LogThrowable());
	}

	/**
	 * Send an {@link #ERROR} log message.
	 * @param msg The message you would like logged.
	 */
	public static int e(String msg) {
		return println(ERROR, "", msg, new LogThrowable());
	}

	/**
	 * Send an {@link #ERROR} log message.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int e(String msg, Throwable tr) {
		return println(ERROR, "", msg + '\n' + Log.getStackTraceString(tr), tr);
	}

	/**
	 * Send an {@link #ERROR} log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int e(String tag, String msg) {
		return println(ERROR, tag, msg, new LogThrowable());
	}

	/**
	 * Send an {@link #ERROR} log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int e(String tag, String msg, Throwable tr) {
		return println(ERROR, tag, msg + '\n' + Log.getStackTraceString(tr), tr);
	}

	/**
	 * A low-level logging call that uses the supplied Throwable to resolve information about
	 * the class/method/etc. that requested the logging call.
	 * @param priority The priority/type of this log message
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr The Throwable associated with this logging call.
	 * @return The number of bytes written.
	 */
	private static int println(int priority, String tag, String msg, Throwable tr) {
		String formattedTag = tag;
		String formattedMsg = msg;

		StackTraceElement[] elements = tr.getStackTrace();
		for (StackTraceElement element : elements) {
			if (!(tr instanceof LogThrowable) || !(TextUtils.equals(element.getClassName(), LOG_CLASSNAME))) {
				formattedTag = format(sTagFormat, sTagArgs, element, tag);
				formattedMsg = format(sMessageFormat, sMessageArgs, element, msg);
				break;
			}
		}
		return Log.println(priority, formattedTag, formattedMsg);
	}

	/*
	 * Constructor
	 */

	private LogEx() {}

	/*
	 * Classes
	 */

	/**
	 * A private extension of the {@link java.lang.Throwable} class used by {@link #println(int, String, String, Throwable)} to
	 * determine whether a Throwable was generated by this class.
	 */
	private static class LogThrowable extends Throwable {}

	/*
	 * Enums
	 */

	/**
	 * Placeholders that will be used to generate formatted logging messages.
	 */
	public static enum Placeholder {
		CLASS_NAME,
		SIMPLE_CLASS_NAME,
		PACKAGE,
		FILE_NAME,
		LINE_NUMBER,
		METHOD_NAME,
		HASH_CODE,
		MESSAGE
	}
}