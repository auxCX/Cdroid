package com.seafile.seadroid2.transfer;

/**
 * Upload state listener
 */
public interface UploadStateListener {
    /**
     * On file upload progress.
     *
     * @param taskID the task id
     */
    void onFileUploadProgress(int taskID);

    /**
     * On file uploaded.
     *
     * @param taskID the task id
     */
    void onFileUploaded(int taskID);

    /**
     * On file upload cancelled.
     *
     * @param taskID the task id
     */
    void onFileUploadCancelled(int taskID);

    /**
     * On file upload failed.
     *
     * @param taskID the task id
     */
    void onFileUploadFailed(int taskID);
}
