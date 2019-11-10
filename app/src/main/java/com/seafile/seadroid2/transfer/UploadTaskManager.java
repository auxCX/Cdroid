package com.seafile.seadroid2.transfer;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.common.collect.Lists;
import com.seafile.seadroid2.SeadroidApplication;
import com.seafile.seadroid2.account.Account;
import com.seafile.seadroid2.notification.UploadNotificationProvider;

import java.util.List;

/**
 * Upload task manager
 * <p/>
 */
public class UploadTaskManager extends TransferManager implements UploadStateListener {
    private static final String DEBUG_TAG = "UploadTaskManager";

    /**
     * The constant BROADCAST_FILE_UPLOAD_SUCCESS.
     */
    public static final String BROADCAST_FILE_UPLOAD_SUCCESS = "uploaded";
    /**
     * The constant BROADCAST_FILE_UPLOAD_FAILED.
     */
    public static final String BROADCAST_FILE_UPLOAD_FAILED = "uploadFailed";
    /**
     * The constant BROADCAST_FILE_UPLOAD_PROGRESS.
     */
    public static final String BROADCAST_FILE_UPLOAD_PROGRESS = "uploadProgress";
    /**
     * The constant BROADCAST_FILE_UPLOAD_CANCELLED.
     */
    public static final String BROADCAST_FILE_UPLOAD_CANCELLED = "uploadCancelled";

    private static UploadNotificationProvider mNotifyProvider;


    /**
     * Add task to que int.
     *
     * @param account       the account
     * @param repoID        the repo id
     * @param repoName      the repo name
     * @param dir           the dir
     * @param filePath      the file path
     * @param isUpdate      the is update
     * @param isCopyToLocal the is copy to local
     * @param byBlock       the by block
     * @return the int
     */
    public int addTaskToQue(Account account, String repoID, String repoName, String dir, String filePath, boolean isUpdate, boolean isCopyToLocal, boolean byBlock) {
        if (repoID == null || repoName == null)
            return 0;

        // create a new one to avoid IllegalStateException
        UploadTask task = new UploadTask(++notificationID, account, repoID, repoName, dir, filePath, isUpdate, isCopyToLocal, byBlock, this);
        addTaskToQue(task);
        return task.getTaskID();
    }

    /**
     * Gets none camera upload task infos.
     *
     * @return the none camera upload task infos
     */
    public List<UploadTaskInfo> getNoneCameraUploadTaskInfos() {
        List<UploadTaskInfo> noneCameraUploadTaskInfos = Lists.newArrayList();
        List<UploadTaskInfo> uploadTaskInfos = (List<UploadTaskInfo>) getAllTaskInfoList();
        for (UploadTaskInfo uploadTaskInfo : uploadTaskInfos) {
            // use isCopyToLocal as a flag to mark a camera photo upload task if false
            // mark a file upload task if true
            if (!uploadTaskInfo.isCopyToLocal) {
                continue;
            }
            noneCameraUploadTaskInfos.add(uploadTaskInfo);
        }

        return noneCameraUploadTaskInfos;
    }

    /**
     * Retry.
     *
     * @param taskID the task id
     */
    public void retry(int taskID) {
        UploadTask task = (UploadTask) getTask(taskID);
        if (task == null || !task.canRetry())
            return;
        addTaskToQue(task.getAccount(), task.getRepoID(), task.getRepoName(), task.getDir(), task.getPath(), task.isUpdate(), task.isCopyToLocal(),false);
    }

    private void notifyProgress(int taskID) {
        UploadTaskInfo info = (UploadTaskInfo) getTaskInfo(taskID);
        if (info == null)
            return;

        // use isCopyToLocal as a flag to mark a camera photo upload task if false
        // mark a file upload task if true
        if (!info.isCopyToLocal)
            return;

        //Log.d(DEBUG_TAG, "notify key " + info.repoID);
        if (mNotifyProvider != null) {
            mNotifyProvider.updateNotification();
        }

    }

    /**
     * Save upload notif provider.
     *
     * @param provider the provider
     */
    public void saveUploadNotifProvider(UploadNotificationProvider provider) {
        mNotifyProvider = provider;
    }

    /**
     * Has notif provider boolean.
     *
     * @return the boolean
     */
    public boolean hasNotifProvider() {
        return mNotifyProvider != null;
    }

    /**
     * Gets notif provider.
     *
     * @return the notif provider
     */
    public UploadNotificationProvider getNotifProvider() {
        if (hasNotifProvider())
            return mNotifyProvider;
        else
            return null;
    }

    /**
     * Cancel all upload notification.
     */
    public void cancelAllUploadNotification() {
        if (mNotifyProvider != null)
            mNotifyProvider.cancelNotification();
    }

    // -------------------------- listener method --------------------//
    @Override
    public void onFileUploadProgress(int taskID) {
        Intent localIntent = new Intent(BROADCAST_ACTION).putExtra("type",
                BROADCAST_FILE_UPLOAD_PROGRESS).putExtra("taskID", taskID);
        LocalBroadcastManager.getInstance(SeadroidApplication.getAppContext()).sendBroadcast(localIntent);
        notifyProgress(taskID);
    }

    @Override
    public void onFileUploaded(int taskID) {
        remove(taskID);
        doNext();
        Intent localIntent = new Intent(BROADCAST_ACTION).putExtra("type",
                BROADCAST_FILE_UPLOAD_SUCCESS).putExtra("taskID", taskID);
        LocalBroadcastManager.getInstance(SeadroidApplication.getAppContext()).sendBroadcast(localIntent);
        notifyProgress(taskID);
    }

    @Override
    public void onFileUploadCancelled(int taskID) {
        Intent localIntent = new Intent(BROADCAST_ACTION).putExtra("type",
                BROADCAST_FILE_UPLOAD_CANCELLED).putExtra("taskID", taskID);
        LocalBroadcastManager.getInstance(SeadroidApplication.getAppContext()).sendBroadcast(localIntent);
        notifyProgress(taskID);
    }

    @Override
    public void onFileUploadFailed(int taskID) {
        remove(taskID);
        doNext();
        Intent localIntent = new Intent(BROADCAST_ACTION).putExtra("type",
                BROADCAST_FILE_UPLOAD_FAILED).putExtra("taskID", taskID);
        LocalBroadcastManager.getInstance(SeadroidApplication.getAppContext()).sendBroadcast(localIntent);
        notifyProgress(taskID);
    }

}
