package com.seafile.seadroid2.transfer;

/**
 * Download state listener
 */
public interface DownloadStateListener {
    /**
     * On file download progress.
     *
     * @param taskID the task id
     */
    void onFileDownloadProgress(int taskID);

    /**
     * On file downloaded.
     *
     * @param taskID the task id
     */
    void onFileDownloaded(int taskID);

    /**
     * On file download failed.
     *
     * @param taskID the task id
     */
    void onFileDownloadFailed(int taskID);
}
