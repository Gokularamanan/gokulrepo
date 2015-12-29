package com.fleshkart.app.utils;

import android.os.Build;
import android.util.Log;
import android.util.TimingLogger;

/**
 * Created by rbp687 on 12/29/15.
 */
public final class Logger {

    public static final boolean DEVELOPMENT = !"user".equals(Build.TYPE);
    private static final String TAG = Logger.class.getSimpleName();
    private static final int INITIAL_CAPACITY = 64;

    public static boolean MSG_DEVELOPMENT = false;

    /**
     * Singleton timing logger for this particular VM
     */
    private static final String TAG_TIMER = "TIMER";
    private static TimingLogger sTimer;

    //    public static void registerTag(String tag) {
//    BlurMotherFrameworkDeps.registerTag(tag);
//    }

    private static synchronized TimingLogger getTimer() {
        if (sTimer == null) {
            sTimer = new TimingLogger(TAG_TIMER, TAG_TIMER);
        }
        return sTimer;
    }

    public static void split(String tag, String label) {
        if (Log.isLoggable(TAG_TIMER, Log.VERBOSE)) {
            getTimer().addSplit(buildLogMessage(tag, ":", label));
        }
    }

    public static void reset() {
        if (Log.isLoggable(TAG_TIMER, Log.VERBOSE)) {
            getTimer().reset();
        }
    }

    public static void dump() {
        if (Log.isLoggable(TAG_TIMER, Log.VERBOSE)) {
            getTimer().dumpToLog();
        }
    }

    public static void setMsgLog(boolean log) {
        MSG_DEVELOPMENT = log;
    }

    /**
     *
     * @param tag
     * @param messages
     */
    public static void d(String tag, Object... messages) {
        if (isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, buildLogMessage(messages));
        }
    }

    /**
     *
     * @param tag
     * @param t An exception to log.
     * @param messages
     */
    public static void d(String tag, Throwable t, Object... messages) {
        if (isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, buildLogMessage(messages), t);
        }
    }

    /**
     *
     * @param tag
     * @param messages
     */
    public static void e(String tag, Object... messages) {
        if (isLoggable(tag, Log.ERROR)) {
            Log.e(tag, buildLogMessage(messages));
        }
    }

    /**
     *
     * @param tag
     * @param t An exception to log.
     * @param messages
     */
    public static void e(String tag, Throwable t, Object... messages) {
        if (isLoggable(tag, Log.ERROR)) {
            Log.e(tag, buildLogMessage(messages), t);
        }
    }

    /**
     *
     * @param tag
     * @param messages
     */
    public static void i(String tag, Object... messages) {
        if (isLoggable(tag, Log.INFO)) {
            Log.i(tag, buildLogMessage(messages));
        }
    }

    /**
     *
     * @param tag
     * @param t An exception to log.
     * @param messages
     */
    public static void i(String tag, Throwable t, Object... messages) {
        if (isLoggable(tag, Log.INFO)) {
            Log.i(tag, buildLogMessage(messages), t);
        }
    }

    /**
     *
     * @param tag
     * @param messages
     */
    public static void v(String tag, Object... messages) {
        if (isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, buildLogMessage(messages));
        }
    }

    /**
     *
     * @param tag
     * @param t An exception to log.
     * @param messages
     */
    public static void v(String tag, Throwable t, Object... messages) {
        if (isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, buildLogMessage(messages), t);
        }
    }

    /**
     *
     * @param tag
     * @param messages
     */
    public static void w(String tag, Object... messages) {
        if (isLoggable(tag, Log.WARN)) {
            Log.w(tag, buildLogMessage(messages));
        }
    }

    /**
     *
     * @param tag
     * @param t An exception to log.
     * @param messages
     */
    public static void w(String tag, Throwable t, Object... messages) {
        if (isLoggable(tag, Log.WARN)) {
            Log.w(tag, buildLogMessage(messages), t);
        }
    }

    /**
     * Acts like {@link android.util.Log#println(int, String, String) } but checks that the logging level
     * is sufficient first, and appends the final String arguments.
     * @param priority The log level to print at.
     * @param tag The tag for this message.
     * @param message The String(s) that are appended to make up the message.
     */
    public static void println(int priority, String tag, Object... message) {
        if (isLoggable(tag, priority)) {
            Log.println(priority, tag, buildLogMessage(message));
        }
    }

    /**
     * Check is a specific tag string should be logged.
     *
     * @param tag The tag string to check.
     * @param level The log level to check.
     *
     * @return True if the tag is valid for the log level.
     *
     * @see android.util.Log(String, int)
     */
    public static boolean isLoggable(String tag, int level) {
//        String logState = System.getProperty("blur.logstate");
//        if ("always".equals(logState)) {
//            return true;
//        } else if ("disabled".equals(logState)) {
//            return false;
//        }

        //need to add a little hack here.  Log.isLoggable will throw an IllegalArgumentException
        //if the tag is longer than 23 characters, which is supposedly the limit.  But this exception
        //doesn't get thrown anywhere else.
        try {
            return DEVELOPMENT || Log.isLoggable(tag, level);
        } catch (Exception ex) {
            Log.e(TAG, "Logger.isLoggable() caught an exception when calling Log.isLoggable().", ex);
            return level >= Log.INFO || DEVELOPMENT;
        }
    }

    /**
     *  Build the log message form the array of messages passed in.
     *
     * @param messages The set of messages.
     *
     * @return The entire log message as a single String object.
     */
    private static String buildLogMessage(Object... messages) {
        StringBuilder logMessage = new StringBuilder(INITIAL_CAPACITY);
        for (Object message : messages) {
            logMessage.append(message);
        }
        return logMessage.toString();
    }
}
