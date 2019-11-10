package com.seafile.seadroid2.gallery;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import android.os.Bundle;

/**
 * The type Monitored activity.
 */
public class MonitoredActivity extends NoSearchActivity {

    private final ArrayList<LifeCycleListener> mListeners =
            Lists.newArrayList();

    /**
     * The interface Life cycle listener.
     */
    public static interface LifeCycleListener {
        /**
         * On activity created.
         *
         * @param activity the activity
         */
        public void onActivityCreated(MonitoredActivity activity);

        /**
         * On activity destroyed.
         *
         * @param activity the activity
         */
        public void onActivityDestroyed(MonitoredActivity activity);

        /**
         * On activity started.
         *
         * @param activity the activity
         */
        public void onActivityStarted(MonitoredActivity activity);

        /**
         * On activity stopped.
         *
         * @param activity the activity
         */
        public void onActivityStopped(MonitoredActivity activity);
    }

    /**
     * The type Life cycle adapter.
     */
    public static class LifeCycleAdapter implements LifeCycleListener {
        public void onActivityCreated(MonitoredActivity activity) {
        }

        public void onActivityDestroyed(MonitoredActivity activity) {
        }

        public void onActivityStarted(MonitoredActivity activity) {
        }

        public void onActivityStopped(MonitoredActivity activity) {
        }
    }

    /**
     * Add life cycle listener.
     *
     * @param listener the listener
     */
    public void addLifeCycleListener(LifeCycleListener listener) {
        if (mListeners.contains(listener)) return;
        mListeners.add(listener);
    }

    /**
     * Remove life cycle listener.
     *
     * @param listener the listener
     */
    public void removeLifeCycleListener(LifeCycleListener listener) {
        mListeners.remove(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityCreated(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityDestroyed(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityStarted(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityStopped(this);
        }
    }
}
