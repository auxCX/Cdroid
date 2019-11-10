/*
 * App passcode library for Android, master branch
 * Dual licensed under MIT, and GPL.
 * See https://github.com/wordpress-mobile/Android-PasscodeLock
 */
package com.seafile.seadroid2.gesturelock;

import android.app.Application;

/**
 * AppLock Manager
 */
public class AppLockManager {

    private static AppLockManager instance;
    private AbstractAppLock currentAppLocker;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static AppLockManager getInstance() {
        if (instance == null) {
            instance = new AppLockManager();
        }
        return instance;
    }

    /**
     * Enable default app lock if available.
     *
     * @param currentApp the current app
     */
    public void enableDefaultAppLockIfAvailable(Application currentApp) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            currentAppLocker = new DefaultAppLock(currentApp);
            currentAppLocker.enable();
        }
    }
}
