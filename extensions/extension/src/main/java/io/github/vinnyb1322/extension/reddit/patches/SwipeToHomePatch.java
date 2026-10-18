package io.github.vinnyb1322.extension.reddit.patches;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;

import io.github.vinnyb1322.extension.reddit.Utils;

/**
 * A right swipe on a post's comments or in the full screen media viewer goes to the home feed.
 * <p>
 * Touches are watched through the activity window callback without consuming them.
 * A swipe is ignored if it starts on something that scrolls horizontally, such as a gallery
 * that is not on its first image.
 */
@SuppressWarnings("unused")
public final class SwipeToHomePatch {
    private static final String MAIN_ACTIVITY = "com.reddit.launch.main.MainActivity";
    private static final String BOTTOM_NAV_SCREEN = "com.reddit.launch.bottomnav.BottomNavScreen";
    private static final String BOTTOM_NAV_TAB = "com.reddit.launch.bottomnav.BottomNavTab";

    /**
     * Activities that only show media, which are closed by the swipe.
     */
    private static final String[] MEDIA_ACTIVITIES = {
            "com.reddit.fullbleedplayer.common.FbpActivity",
            "com.reddit.frontpage.LightboxActivity"
    };

    /**
     * Screens of the main activity where the swipe works, by class name prefix.
     */
    private static final String[] SWIPE_SCREENS = {
            "com.reddit.postdetail.",
            "com.reddit.fullbleedplayer.",
            "com.reddit.fullbleedcontainer.",
            "com.reddit.frontpage.presentation.listing.linkpager."
    };

    /**
     * Minimum swipe distance as a fraction of the screen width.
     */
    private static final float MIN_DISTANCE_FRACTION = 0.25f;
    private static final long MAX_DURATION_MS = 800;

    private static boolean initialized;
    private static boolean goHomeOnResume;

    private static Method getBottomNavScreenMethod;
    private static Method getCurrentScreenMethod;
    private static Method selectTabMethod;
    private static Object homeTab;

    /**
     * Injection point. Called at the start of MainActivity.onCreate().
     */
    public static void init(Activity activity) {
        try {
            if (initialized) return;
            initialized = true;
            activity.getApplication().registerActivityLifecycleCallbacks(new LifecycleCallbacks());
        } catch (Exception ex) {
            Utils.logException("init failure", ex);
        }
    }

    private static boolean isMainActivity(Activity activity) {
        return activity.getClass().getName().equals(MAIN_ACTIVITY);
    }

    private static boolean isMediaActivity(Activity activity) {
        String name = activity.getClass().getName();
        for (String mediaActivity : MEDIA_ACTIVITIES) {
            if (name.equals(mediaActivity)) return true;
        }
        return false;
    }

    private static Object getBottomNavScreen(Activity activity) throws Exception {
        if (getBottomNavScreenMethod == null) {
            Class<?> bottomNavScreenClass = Class.forName(BOTTOM_NAV_SCREEN, false, activity.getClassLoader());
            for (Method method : activity.getClass().getDeclaredMethods()) {
                if (method.getParameterTypes().length == 0 && method.getReturnType() == bottomNavScreenClass) {
                    method.setAccessible(true);
                    getBottomNavScreenMethod = method;
                    break;
                }
            }
            if (getBottomNavScreenMethod == null) throw new NoSuchMethodException("BottomNavScreen getter");
        }
        return getBottomNavScreenMethod.invoke(activity);
    }

    /**
     * @return If the swipe should work in this activity right now.
     */
    private static boolean isSwipeEnabled(Activity activity) {
        try {
            if (isMediaActivity(activity)) return true;
            if (!isMainActivity(activity)) return false;

            Object bottomNavScreen = getBottomNavScreen(activity);
            if (bottomNavScreen == null) return false;
            if (getCurrentScreenMethod == null) {
                getCurrentScreenMethod = bottomNavScreen.getClass().getMethod("getCurrentScreen");
            }
            Object currentScreen = getCurrentScreenMethod.invoke(bottomNavScreen);
            if (currentScreen == null) return false;

            String name = currentScreen.getClass().getName();
            for (String prefix : SWIPE_SCREENS) {
                if (name.startsWith(prefix)) return true;
            }
        } catch (Exception ex) {
            Utils.logException("isSwipeEnabled failure", ex);
        }
        return false;
    }

    /**
     * Switches to the Home tab and removes the screens opened on top of the home feed.
     * This is what Reddit does when the selected tab is tapped again.
     */
    private static void goHome(Activity activity) {
        try {
            Object bottomNavScreen = getBottomNavScreen(activity);
            if (bottomNavScreen == null) return;

            if (selectTabMethod == null) {
                Class<?> tabClass = Class.forName(BOTTOM_NAV_TAB, false, activity.getClassLoader());
                for (Method method : bottomNavScreen.getClass().getDeclaredMethods()) {
                    Class<?>[] parameters = method.getParameterTypes();
                    if (parameters.length == 2 && parameters[0] == tabClass && parameters[1] == boolean.class
                            && method.getReturnType() == void.class) {
                        method.setAccessible(true);
                        selectTabMethod = method;
                        break;
                    }
                }
                if (selectTabMethod == null) throw new NoSuchMethodException("BottomNavScreen tab selector");
                for (Object tab : tabClass.getEnumConstants()) {
                    if (((Enum<?>) tab).name().equals("Home")) homeTab = tab;
                }
            }

            selectTabMethod.invoke(bottomNavScreen, homeTab, true);
        } catch (Exception ex) {
            Utils.logException("goHome failure", ex);
        }
    }

    private static void onSwipe(Activity activity) {
        if (isMediaActivity(activity)) {
            // Close the media viewer, then clear the main activity once it is shown again.
            goHomeOnResume = true;
            activity.finish();
        } else {
            goHome(activity);
        }
    }

    /**
     * @return If a view under the touch point can scroll its content to the left, so a right swipe
     * belongs to it.
     */
    private static boolean canScrollRightAt(View view, float rawX, float rawY) {
        if (view.getVisibility() != View.VISIBLE) return false;

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        if (rawX < location[0] || rawX >= location[0] + view.getWidth()
                || rawY < location[1] || rawY >= location[1] + view.getHeight()) {
            return false;
        }

        // A disabled view, such as the post pager when Adobo turns off swiping between posts,
        // does not scroll, but its children still can.
        if (view.isEnabled() && view.canScrollHorizontally(-1)) return true;

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = group.getChildCount() - 1; i >= 0; i--) {
                if (canScrollRightAt(group.getChildAt(i), rawX, rawY)) return true;
            }
        }
        return false;
    }

    private static final class LifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityResumed(Activity activity) {
            try {
                Window window = activity.getWindow();
                if (window != null && !(window.getCallback() instanceof SwipeWindowCallback)
                        && (isMainActivity(activity) || isMediaActivity(activity))) {
                    window.setCallback(new SwipeWindowCallback(activity, window.getCallback()));
                }

                if (goHomeOnResume && isMainActivity(activity)) {
                    goHomeOnResume = false;
                    goHome(activity);
                }
            } catch (Exception ex) {
                Utils.logException("onActivityResumed failure", ex);
            }
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }

    /**
     * Passes everything through to the original callback and watches touches for the swipe.
     */
    private static final class SwipeWindowCallback implements Window.Callback {
        private final WeakReference<Activity> activity;
        private final Window.Callback wrapped;

        private boolean tracking;
        private boolean swiping;
        private float downX;
        private float downY;
        private long downTime;

        SwipeWindowCallback(Activity activity, Window.Callback wrapped) {
            this.activity = new WeakReference<>(activity);
            this.wrapped = wrapped;
        }

        private void onTouch(MotionEvent event) {
            Activity activity = this.activity.get();
            if (activity == null) return;

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    tracking = isSwipeEnabled(activity);
                    swiping = false;
                    downX = event.getRawX();
                    downY = event.getRawY();
                    downTime = event.getEventTime();
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_CANCEL:
                    tracking = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (!tracking || swiping) break;
                    float dx = event.getRawX() - downX;
                    float dy = event.getRawY() - downY;
                    int touchSlop = ViewConfiguration.get(activity).getScaledTouchSlop();
                    if (Math.abs(dx) < touchSlop && Math.abs(dy) < touchSlop) break;

                    // Decide once, on the first movement past the touch slop.
                    View decorView = activity.getWindow().getDecorView();
                    swiping = dx > 0 && Math.abs(dy) * 2 < dx
                            && !canScrollRightAt(decorView, downX, downY);
                    tracking = swiping;
                    break;

                case MotionEvent.ACTION_UP:
                    if (!swiping) break;
                    tracking = false;
                    swiping = false;
                    float distance = event.getRawX() - downX;
                    float drift = Math.abs(event.getRawY() - downY);
                    int width = activity.getWindow().getDecorView().getWidth();
                    if (distance > width * MIN_DISTANCE_FRACTION && drift * 2 < distance
                            && event.getEventTime() - downTime < MAX_DURATION_MS) {
                        onSwipe(activity);
                    }
                    break;
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            try {
                onTouch(event);
            } catch (Exception ex) {
                tracking = false;
                Utils.logException("onTouch failure", ex);
            }
            return wrapped.dispatchTouchEvent(event);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            return wrapped.dispatchKeyEvent(event);
        }

        @Override
        public boolean dispatchKeyShortcutEvent(KeyEvent event) {
            return wrapped.dispatchKeyShortcutEvent(event);
        }

        @Override
        public boolean dispatchTrackballEvent(MotionEvent event) {
            return wrapped.dispatchTrackballEvent(event);
        }

        @Override
        public boolean dispatchGenericMotionEvent(MotionEvent event) {
            return wrapped.dispatchGenericMotionEvent(event);
        }

        @Override
        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
            return wrapped.dispatchPopulateAccessibilityEvent(event);
        }

        @Override
        public View onCreatePanelView(int featureId) {
            return wrapped.onCreatePanelView(featureId);
        }

        @Override
        public boolean onCreatePanelMenu(int featureId, Menu menu) {
            return wrapped.onCreatePanelMenu(featureId, menu);
        }

        @Override
        public boolean onPreparePanel(int featureId, View view, Menu menu) {
            return wrapped.onPreparePanel(featureId, view, menu);
        }

        @Override
        public boolean onMenuOpened(int featureId, Menu menu) {
            return wrapped.onMenuOpened(featureId, menu);
        }

        @Override
        public boolean onMenuItemSelected(int featureId, MenuItem item) {
            return wrapped.onMenuItemSelected(featureId, item);
        }

        @Override
        public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
            wrapped.onWindowAttributesChanged(attrs);
        }

        @Override
        public void onContentChanged() {
            wrapped.onContentChanged();
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            wrapped.onWindowFocusChanged(hasFocus);
        }

        @Override
        public void onAttachedToWindow() {
            wrapped.onAttachedToWindow();
        }

        @Override
        public void onDetachedFromWindow() {
            wrapped.onDetachedFromWindow();
        }

        @Override
        public void onPanelClosed(int featureId, Menu menu) {
            wrapped.onPanelClosed(featureId, menu);
        }

        @Override
        public boolean onSearchRequested() {
            return wrapped.onSearchRequested();
        }

        @Override
        public boolean onSearchRequested(SearchEvent searchEvent) {
            return wrapped.onSearchRequested(searchEvent);
        }

        @Override
        public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
            return wrapped.onWindowStartingActionMode(callback);
        }

        @Override
        public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
            return wrapped.onWindowStartingActionMode(callback, type);
        }

        @Override
        public void onActionModeStarted(ActionMode mode) {
            wrapped.onActionModeStarted(mode);
        }

        @Override
        public void onActionModeFinished(ActionMode mode) {
            wrapped.onActionModeFinished(mode);
        }

        @Override
        public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu, int deviceId) {
            wrapped.onProvideKeyboardShortcuts(data, menu, deviceId);
        }

        @Override
        public void onPointerCaptureChanged(boolean hasCapture) {
            wrapped.onPointerCaptureChanged(hasCapture);
        }
    }
}
