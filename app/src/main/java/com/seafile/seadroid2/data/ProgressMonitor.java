package com.seafile.seadroid2.data;

/**
 * The interface Progress monitor.
 */
public interface ProgressMonitor {
    /**
     * On progress notify.
     *
     * @param total       the total
     * @param updateTotal the update total
     */
    void onProgressNotify(long total, boolean updateTotal);

    /**
     * Is cancelled boolean.
     *
     * @return the boolean
     */
    boolean isCancelled();
}
