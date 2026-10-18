package io.github.vinnyb1322.extension.reddit.patches;

import android.os.Bundle;

import java.lang.reflect.Field;
import java.util.Locale;

import io.github.vinnyb1322.extension.reddit.Utils;

@SuppressWarnings("unused")
public final class BlockBreakingNewsNotificationsPatch {

    /**
     * Injection point.
     *
     * @param remoteMessage A Firebase RemoteMessage. Its data has a "type" key
     *                      such as BREAKING_NEWS, BREAKING_SPORTS or COMMENT_REPLY.
     * @return If the notification should be dropped.
     */
    public static boolean blockNotification(Object remoteMessage) {
        try {
            Bundle data = getBundle(remoteMessage);
            if (data == null) return false;

            Object type = data.get("type");
            return type instanceof String
                    && ((String) type).toUpperCase(Locale.ROOT).startsWith("BREAKING_");
        } catch (Exception ex) {
            Utils.logException("blockNotification failure", ex);
            return false;
        }
    }

    /**
     * RemoteMessage is obfuscated, so find its Bundle field by type.
     */
    private static Bundle getBundle(Object remoteMessage) throws IllegalAccessException {
        if (remoteMessage == null) return null;
        for (Field field : remoteMessage.getClass().getDeclaredFields()) {
            if (field.getType() == Bundle.class) {
                field.setAccessible(true);
                return (Bundle) field.get(remoteMessage);
            }
        }
        return null;
    }
}
