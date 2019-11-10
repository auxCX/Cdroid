package com.seafile.seadroid2.transfer;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.common.collect.Lists;
import com.seafile.seadroid2.SeadroidApplication;
import com.seafile.seadroid2.account.Account;
import com.seafile.seadroid2.notification.DownloadNotificationProvider;
import com.seafile.seadroid2.util.ConcurrentAsyncTask;
import com.seafile.seadroid2.util.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Download task manager
 * <p/>
 */
public class DownloadTaskManager extends TransferManager implements DownloadStateListener {
    private static final String DEBUG_TAG = "DownloadTaskManager";

    /**
     * The constant BROADCAST_FILE_DOWNLOAD_SUCCESS.
     */
    public static final String BROADCAST_FILE_DOWNLOAD_SUCCESS = "downloaded";
    /**
     * The constant BROADCAST_FILE_DOWNLOAD_FAILED.
     */
    public static final String BROADCAST_FILE_DOWNLOAD_FAILED = "downloadFailed";
    /**
     * The constant BROADCAST_FILE_DOWNLOAD_PROGRESS.
     */
    public static final String BROADCAST_FILE_DOWNLOAD_PROGRESS = "downloadProgress";

    private static DownloadNotificationProvider mNotifProvider;

    /**
     * Add a new download task.
     * call this method to execute a task immediately.
     *
     * @param account  the account
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param path     the path
     * @param fileSize the file size
     * @return the int
     */
    public int addTask(Account account, String repoName, String repoID, String path, long fileSize) {
        TransferTask task = new DownloadTask(++notificationID, account, repoName, repoID, path, this);
        task.totalSize = fileSize;
        TransferTask oldTask = null;
        if (allTaskList.containsValue(task)) {
            oldTask = allTaskList.get(task.taskID);
        }
        if (oldTask != null) {
            if (oldTask.getState().equals(TaskState.CANCELLED)
                    || oldTask.getState().equals(TaskState.FAILED)
                    || oldTask.getState().equals(TaskState.FINISHED)) {
                allTaskList.remove(oldTask);
            } else {
                // return taskID of old task
                return oldTask.getTaskID();
            }
        }
        allTaskList.put(task.getTaskID(),task);
        ConcurrentAsyncTask.execute(task);
        return task.getTaskID();
    }

    /**
     * Add task to que.
     *
     * @param account  the account
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param path     the path
     */
    public void addTaskToQue(Account account, String repoName, String repoID, String path) {
        // create a new one to avoid IllegalStateException
        DownloadTask downloadTask = new DownloadTask(++notificationID, account, repoName, repoID, path, this);
        addTaskToQue(downloadTask);
    }

    /**
     * Gets downloading file count by path.
     *
     * @param repoID the repo id
     * @param dir    the dir
     * @return the downloading file count by path
     */
    public int getDownloadingFileCountByPath(String repoID, String dir) {
        List<DownloadTaskInfo> downloadTaskInfos = getTaskInfoListByPath(repoID, dir);
        int count = 0;
        for (DownloadTaskInfo downloadTaskInfo : downloadTaskInfos) {
            if (downloadTaskInfo.state.equals(TaskState.INIT)
                    || downloadTaskInfo.state.equals(TaskState.TRANSFERRING))
                count++;
        }
        return count;
    }

    /**
     * get all download task info under a specific directory.
     *
     * @param repoID the repo id
     * @param dir    the dir
     * @return List<DownloadTaskInfo> task info list by path
     */
    public List<DownloadTaskInfo> getTaskInfoListByPath(String repoID, String dir) {
        ArrayList<DownloadTaskInfo> infos = Lists.newArrayList();
        Iterator<Map.Entry<Integer, TransferTask>> iterator = allTaskList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, TransferTask> next = iterator.next();
            TransferTask value = next.getValue();
            if (!value.getRepoID().equals(repoID)) {

                String parentDir = Utils.getParentPath(value.getPath());
                if (parentDir.equals(dir)) {
                    infos.add(((DownloadTask) value).getTaskInfo());
                }
            }
        }

        return infos;
    }

    /**
     * get all download task info under a specific repo.
     *
     * @param repoID the repo id
     * @return List<DownloadTaskInfo> task info list by repo
     */
    public List<DownloadTaskInfo> getTaskInfoListByRepo(String repoID) {
        ArrayList<DownloadTaskInfo> infos = Lists.newArrayList();
        Iterator<Map.Entry<Integer, TransferTask>> iterator = allTaskList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, TransferTask> next = iterator.next();
            TransferTask value = next.getValue();
            if (value.getRepoID().equals(repoID)) {
                infos.add(((DownloadTask) value).getTaskInfo());
            }
        }

        return infos;
    }

    /**
     * Retry.
     *
     * @param taskID the task id
     */
    public void retry(int taskID) {
        DownloadTask task = (DownloadTask) getTask(taskID);
        if (task == null || !task.canRetry())
            return;
        addTaskToQue(task.getAccount(), task.getRepoName(), task.getRepoID(), task.getPath());
    }

    private void notifyProgress(int taskID) {
        DownloadTaskInfo info = (DownloadTaskInfo) getTaskInfo(taskID);
        if (info == null)
            return;

        if (mNotifProvider != null)
            mNotifProvider.updateNotification();
    }

    /**
     * Save notif provider.
     *
     * @param provider the provider
     */
    public void saveNotifProvider(DownloadNotificationProvider provider) {
        mNotifProvider = provider;
    }

    /**
     * Has notif provider boolean.
     *
     * @return the boolean
     */
    public boolean hasNotifProvider() {
        return mNotifProvider != null;
    }

    /**
     * Gets notif provider.
     *
     * @return the notif provider
     */
    public DownloadNotificationProvider getNotifProvider() {
        if (hasNotifProvider())
            return mNotifProvider;
        else
            return null;
    }

    /**
     * Cancel all download notification.
     */
    public void cancelAllDownloadNotification() {
        if (mNotifProvider != null)
            mNotifProvider.cancelNotification();
    }

    // -------------------------- listener method --------------------//
    @Override
    public void onFileDownloadProgress(int taskID) {
        Intent localIntent = new Intent(BROADCAST_ACTION).putExtra("type",
                BROADCAST_FILE_DOWNLOAD_PROGRESS).putExtra("taskID", taskID);
        LocalBroadcastManager.getInstance(SeadroidApplication.getAppContext()).sendBroadcast(localIntent);
        notifyProgress(taskID);
    }

    @Override
    public void onFileDownloaded(int taskID) {
        remove(taskID);
        doNext();
        Intent localIntent = new Intent(BROADCAST_ACTION).putExtra("type",
                BROADCAST_FILE_DOWNLOAD_SUCCESS).putExtra("taskID", taskID);
        LocalBroadcastManager.getInstance(SeadroidApplication.getAppContext()).sendBroadcast(localIntent);
        notifyProgress(taskID);
    }

    @Override
    public void onFileDownloadFailed(int taskID) {
        remove(taskID);
        doNext();
        Intent localIntent = new Intent(BROADCAST_ACTION).putExtra("type",
                BROADCAST_FILE_DOWNLOAD_FAILED).putExtra("taskID", taskID);
        LocalBroadcastManager.getInstance(SeadroidApplication.getAppContext()).sendBroadcast(localIntent);
        notifyProgress(taskID);
    }
}
