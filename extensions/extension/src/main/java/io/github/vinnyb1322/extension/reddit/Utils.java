package io.github.vinnyb1322.extension.reddit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

public final class Utils {
    private static final String TAG = "VincesPatches";

    private static Context context;

    private Utils() {
    }

    /**
     * @return The application context, or null if it is not available yet.
     */
    @SuppressLint("PrivateApi")
    public static Context getContext() {
        if (context == null) {
            try {
                context = (Context) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication")
                        .invoke(null);
            } catch (Exception ex) {
                logException("Could not get the application context", ex);
            }
        }
        return context;
    }

    /**
     * @return The resource id of an app string, or 0 if it does not exist.
     */
    public static int getStringId(String name) {
        Context context = getContext();
        if (context == null) return 0;
        return context.getResources().getIdentifier(name, "string", context.getPackageName());
    }

    /**
     * Reads a value out of a Kotlin data class toString() such as
     * {@code Author(id=t2_abc, username=foo, displayName=bar)}.
     * Data class toString() output keeps its field names even though the
     * classes themselves are obfuscated, so it is stable between app versions.
     *
     * @return The value after {@code key=} up to the next comma or closing
     * parenthesis, or null if the key is not present.
     */
    public static String getToStringValue(String toString, String key) {
        if (toString == null) return null;
        // Keys always follow "(" or ", ", which keeps "promoted" from matching "isPromoted".
        String prefix = key + "=";
        int parenthesisIndex = toString.indexOf("(" + prefix);
        int spaceIndex = toString.indexOf(" " + prefix);
        int index = parenthesisIndex < 0 ? spaceIndex
                : spaceIndex < 0 ? parenthesisIndex
                : Math.min(parenthesisIndex, spaceIndex);
        if (index < 0) return null;
        int start = index + 1 + prefix.length();
        int end = start;
        while (end < toString.length()) {
            char c = toString.charAt(end);
            if (c == ',' || c == ')') break;
            end++;
        }
        return toString.substring(start, end);
    }

    public static void logException(String message, Throwable ex) {
        Log.e(TAG, message, ex);
    }
}
