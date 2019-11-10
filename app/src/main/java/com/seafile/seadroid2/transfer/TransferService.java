package com.seafile.seadroid2.transfer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.seafile.seadroid2.account.Account;
import com.seafile.seadroid2.notification.DownloadNotificationProvider;
import com.seafile.seadroid2.notification.UploadNotificationProvider;

import java.util.List;

/**
 * The type Transfer service.
 */
public class TransferService extends Service {
    private static final String DEBUG_TAG = "TransferService";

    private final IBinder mBinder = new TransferBinder();

    /**
     * Gets download task manager.
     *
     * @return the download task manager
     */
    public DownloadTaskManager getDownloadTaskManager() {
        return downloadTaskManager;
    }

    /**
     * Gets upload task manager.
     *
     * @return the upload task manager
     */
    public UploadTaskManager getUploadTaskManager() {
        return uploadTaskManager;
    }

    private DownloadTaskManager downloadTaskManager;
    private UploadTaskManager uploadTaskManager;

    @Override
    public void onCreate() {
        downloadTaskManager = new DownloadTaskManager();
        uploadTaskManager = new UploadTaskManager();
    }

    @Override
    public void onDestroy() {
        Log.d(DEBUG_TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * The type Transfer binder.
     */
    public class TransferBinder extends Binder {
        /**
         * Gets service.
         *
         * @return the service
         */
        public TransferService getService() {
            return TransferService.this;
        }
    }

    /**
     * Is transferring boolean.
     *
     * @return the boolean
     */
    public boolean isTransferring() {
        List<UploadTaskInfo> uInfos = getNoneCameraUploadTaskInfos();
        for (UploadTaskInfo info : uInfos) {
            if (info.state.equals(TaskState.INIT)
                    || info.state.equals(TaskState.TRANSFERRING))
                return true;
        }

        List<DownloadTaskInfo> dInfos = getAllDownloadTaskInfos();
        for (DownloadTaskInfo info : dInfos) {
            if (info.state.equals(TaskState.INIT)
                    || info.state.equals(TaskState.TRANSFERRING))
                return true;
        }

        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Log.d(DEBUG_TAG, "onBind");
        return mBinder;
    }

    // -------------------------- upload task --------------------//

    /**
     * Call this method to handle upload request, like file upload or camera upload.
     * Uploading tasks are managed in a queue.
     * <p>
     * Note: use isCopyToLocal to mark automatic camera upload if false, or file upload if true.
     *
     * @param account       the account
     * @param repoID        the repo id
     * @param repoName      the repo name
     * @param dir           the dir
     * @param filePath      the file path
     * @param isUpdate      the is update
     * @param isCopyToLocal the is copy to local
     * @return int
     */
    public int addTaskToUploadQue(Account account, String repoID, String repoName, String dir, String filePath, boolean isUpdate, boolean isCopyToLocal) {
        return uploadTaskManager.addTaskToQue(account, repoID, repoName, dir, filePath, isUpdate, isCopyToLocal,false);
    }

    /**
     * Call this method to handle upload request, like file upload or camera upload.
     * Uploading tasks are managed in a queue.
     * <p>
     * Note: use isCopyToLocal to mark automatic camera upload if false, or file upload if true.
     *
     * @param account       the account
     * @param repoID        the repo id
     * @param repoName      the repo name
     * @param dir           the dir
     * @param filePath      the file path
     * @param isUpdate      the is update
     * @param isCopyToLocal the is copy to local
     * @return int
     */
    public int addTaskToUploadQueBlock(Account account, String repoID, String repoName, String dir,
                                       String filePath, boolean isUpdate, boolean isCopyToLocal) {
        return uploadTaskManager.addTaskToQue(account, repoID, repoName, dir, filePath, isUpdate, isCopyToLocal, true);
    }

    /**
     * Call this method to handle upload request, like file upload or camera upload.
     * <p>
     * Note: use isCopyToLocal to mark automatic camera upload if false, or file upload if true.
     *
     * @param account       the account
     * @param repoID        the repo id
     * @param repoName      the repo name
     * @param dir           the dir
     * @param filePath      the file path
     * @param isUpdate      the is update
     * @param isCopyToLocal the is copy to local
     * @return int
     */
    public int addUploadTask(Account account, String repoID, String repoName, String dir,
            String filePath, boolean isUpdate, boolean isCopyToLocal) {
        return addTaskToUploadQue(account, repoID, repoName, dir, filePath, isUpdate, isCopyToLocal);
    }

    /**
     * Gets upload task info.
     *
     * @param taskID the task id
     * @return the upload task info
     */
    public UploadTaskInfo getUploadTaskInfo(int taskID) {
        return (UploadTaskInfo) uploadTaskManager.getTaskInfo(taskID);
    }

    /**
     * Gets all upload task infos.
     *
     * @return the all upload task infos
     */
    public List<UploadTaskInfo> getAllUploadTaskInfos() {
        return (List<UploadTaskInfo>) uploadTaskManager.getAllTaskInfoList();
    }

    /**
     * Gets none camera upload task infos.
     *
     * @return the none camera upload task infos
     */
    public List<UploadTaskInfo> getNoneCameraUploadTaskInfos() {
        return uploadTaskManager.getNoneCameraUploadTaskInfos();
    }

    /**
     * Remove all upload tasks by state.
     *
     * @param taskState the task state
     */
    public void removeAllUploadTasksByState(TaskState taskState) {
        uploadTaskManager.removeByState(taskState);

    }

    /**
     * Restart all upload tasks by state.
     *
     * @param taskState the task state
     */
    public void restartAllUploadTasksByState(TaskState taskState) {
        for (TransferTask tt : uploadTaskManager.getTasksByState(taskState)) {
            retryUploadTask(tt.getTaskID());
        }
    }

    /**
     * Restart upload tasks by ids.
     *
     * @param ids the ids
     */
    public void restartUploadTasksByIds(List<Integer> ids) {
        for (int id : ids) {
            retryUploadTask(id);
        }
    }

    /**
     * Cancel upload task in que.
     *
     * @param taskID the task id
     */
    public void cancelUploadTaskInQue(int taskID) {
        uploadTaskManager.cancel(taskID);
        uploadTaskManager.doNext();
    }

    /**
     * Cancel all upload tasks.
     */
    public void cancelAllUploadTasks() {
        uploadTaskManager.cancelAll();
        uploadTaskManager.cancelAllUploadNotification();
    }

    /**
     * Cancel upload tasks by ids.
     *
     * @param ids the ids
     */
    public void cancelUploadTasksByIds(List<Integer> ids) {
        uploadTaskManager.cancelByIds(ids);
        uploadTaskManager.cancelAllUploadNotification();
    }

    /**
     * Retry upload task.
     *
     * @param taskID the task id
     */
    public void retryUploadTask(int taskID) {
        uploadTaskManager.retry(taskID);
    }

    /**
     * Remove upload task.
     *
     * @param taskID the task id
     */
    public void removeUploadTask(int taskID) {
        uploadTaskManager.removeInAllTaskList(taskID);
    }

    /**
     * remove all upload tasks by their taskIds.
     * <p>
     * Note: when deleting all tasks whose state is {@link com.seafile.seadroid2.transfer.TaskState#TRANSFERRING} in the queue,
     * other tasks left will never be executed, because they are all in the {@link com.seafile.seadroid2.transfer.TaskState#INIT} state.
     * In this case, explicitly call doNext to start processing the queue.
     *
     * @param ids the ids
     */
    public void removeUploadTasksByIds(List<Integer> ids) {
        uploadTaskManager.removeByIds(ids);
        // explicitly call doNext if there aren`t any tasks under transferring state,
        // in case that all tasks are waiting in the queue.
        // This could happen if all transferring tasks are removed by calling removeByIds.
        if (!uploadTaskManager.isTransferring())
            uploadTaskManager.doNext();
    }

    /**
     * Add download task int.
     *
     * @param account  the account
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param path     the path
     * @return the int
     */
// -------------------------- download task --------------------//
    public int addDownloadTask(Account account, String repoName, String repoID, String path) {
        return addDownloadTask(account, repoName, repoID, path, -1L);
    }

    /**
     * Add download task int.
     *
     * @param account  the account
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param path     the path
     * @param fileSize the file size
     * @return the int
     */
    public int addDownloadTask(Account account, String repoName, String repoID, String path, long fileSize) {
        return downloadTaskManager.addTask(account, repoName, repoID, path, fileSize);
    }

    /**
     * Add task to download que.
     *
     * @param account  the account
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param path     the path
     */
    public void addTaskToDownloadQue(Account account, String repoName, String repoID, String path) {
       downloadTaskManager.addTaskToQue(account, repoName, repoID, path);
    }

    /**
     * Gets all download task infos.
     *
     * @return the all download task infos
     */
    public List<DownloadTaskInfo> getAllDownloadTaskInfos() {
        return (List<DownloadTaskInfo>) downloadTaskManager.getAllTaskInfoList();
    }

    /**
     * Gets downloading file count by path.
     *
     * @param repoID  the repo id
     * @param dirPath the dir path
     * @return the downloading file count by path
     */
    public int getDownloadingFileCountByPath(String repoID, String dirPath) {
        return downloadTaskManager.getDownloadingFileCountByPath(repoID, dirPath);
    }

    /**
     * Gets download task infos by path.
     *
     * @param repoID the repo id
     * @param dir    the dir
     * @return the download task infos by path
     */
    public List<DownloadTaskInfo> getDownloadTaskInfosByPath(String repoID, String dir) {
        return downloadTaskManager.getTaskInfoListByPath(repoID, dir);
    }

    /**
     * Gets download task infos by repo.
     *
     * @param repoID the repo id
     * @return the download task infos by repo
     */
    public List<DownloadTaskInfo> getDownloadTaskInfosByRepo(String repoID) {
        return downloadTaskManager.getTaskInfoListByRepo(repoID);
    }

    /**
     * Remove download task.
     *
     * @param taskID the task id
     */
    public void removeDownloadTask(int taskID) {
        downloadTaskManager.removeInAllTaskList(taskID);
    }

    /**
     * Restart all download tasks by state.
     *
     * @param taskState the task state
     */
    public void restartAllDownloadTasksByState(TaskState taskState) {
        for (TransferTask tt : downloadTaskManager.getTasksByState(taskState)) {
            retryDownloadTask(tt.getTaskID());
        }
    }

    /**
     * Restart download tasks by ids.
     *
     * @param ids the ids
     */
    public void restartDownloadTasksByIds(List<Integer> ids) {
        for (int id : ids) {
            retryDownloadTask(id);
        }
    }

    /**
     * Remove all download tasks by state.
     *
     * @param taskState the task state
     */
    public void removeAllDownloadTasksByState(TaskState taskState) {
        downloadTaskManager.removeByState(taskState);
    }

    /**
     * remove all download tasks by their taskIds.
     * <p>
     * Note: when deleting all tasks whose state is {@link com.seafile.seadroid2.transfer.TaskState#TRANSFERRING} in the queue,
     * other tasks left will never be executed, because they are all in the {@link com.seafile.seadroid2.transfer.TaskState#INIT} state.
     * In this case, explicitly call doNext to start processing the queue.
     *
     * @param ids the ids
     */
    public void removeDownloadTasksByIds(List<Integer> ids) {
        downloadTaskManager.removeByIds(ids);
        // explicitly call doNext if there aren`t any tasks under transferring state,
        // in case that all tasks are waiting in the queue.
        // This could happen if all transferring tasks are removed by calling removeByIds.
        if (!downloadTaskManager.isTransferring())
            downloadTaskManager.doNext();
    }

    /**
     * Retry download task.
     *
     * @param taskID the task id
     */
    public void retryDownloadTask(int taskID) {
        downloadTaskManager.retry(taskID);
    }

    /**
     * Gets download task info.
     *
     * @param taskID the task id
     * @return the download task info
     */
    public DownloadTaskInfo getDownloadTaskInfo(int taskID) {
        return (DownloadTaskInfo) downloadTaskManager.getTaskInfo(taskID);
    }

    /**
     * Cancel download task.
     *
     * @param taskID the task id
     */
    public void cancelDownloadTask(int taskID) {
        cancelDownloadTaskInQue(taskID);
    }

    /**
     * Cancel notification.
     */
    public void cancelNotification() {
        downloadTaskManager.cancelAllDownloadNotification();
    }

    /**
     * Cancel download task in que.
     *
     * @param taskID the task id
     */
    public void cancelDownloadTaskInQue(int taskID) {
        downloadTaskManager.cancel(taskID);
        downloadTaskManager.doNext();
    }

    /**
     * Cancell all download tasks.
     */
    public void cancellAllDownloadTasks() {
        downloadTaskManager.cancelAll();
        downloadTaskManager.cancelAllDownloadNotification();
    }

    /**
     * Cancell download tasks by ids.
     *
     * @param ids the ids
     */
    public void cancellDownloadTasksByIds(List<Integer> ids) {
        downloadTaskManager.cancelByIds(ids);
        downloadTaskManager.cancelAllDownloadNotification();
    }

    // -------------------------- upload notification --------------------//

    /**
     * Save upload notif provider.
     *
     * @param provider the provider
     */
    public void saveUploadNotifProvider(UploadNotificationProvider provider) {
        uploadTaskManager.saveUploadNotifProvider(provider);
    }

    /**
     * Has upload notif provider boolean.
     *
     * @return the boolean
     */
    public boolean hasUploadNotifProvider() {
        return uploadTaskManager.hasNotifProvider();
    }

    /**
     * Gets upload notif provider.
     *
     * @return the upload notif provider
     */
    public UploadNotificationProvider getUploadNotifProvider() {
        return uploadTaskManager.getNotifProvider();
    }

    // -------------------------- download notification --------------------//

    /**
     * Save download notif provider.
     *
     * @param provider the provider
     */
    public void saveDownloadNotifProvider(DownloadNotificationProvider provider) {
        downloadTaskManager.saveNotifProvider(provider);
    }

    /**
     * Has download notif provider boolean.
     *
     * @return the boolean
     */
    public boolean hasDownloadNotifProvider() {
        return downloadTaskManager.hasNotifProvider();
    }

    /**
     * Gets download notif provider.
     *
     * @return the download notif provider
     */
    public DownloadNotificationProvider getDownloadNotifProvider() {
        return downloadTaskManager.getNotifProvider();
    }

}
