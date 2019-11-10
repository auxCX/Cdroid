package com.seafile.seadroid2.transfer;

import android.util.Log;

import com.google.common.collect.Lists;
import com.seafile.seadroid2.SeadroidApplication;
import com.seafile.seadroid2.data.CameraSyncEvent;
import com.seafile.seadroid2.util.CameraSyncStatus;
import com.seafile.seadroid2.util.ConcurrentAsyncTask;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Manages file downloading and uploading.
 * <p/>
 * Currently use an AsyncTask for an file.
 */
public abstract class TransferManager {
    private static final String DEBUG_TAG = "TransferManager";

    /**
     * The constant BROADCAST_ACTION.
     */
    public static final String BROADCAST_ACTION = "com.seafile.seadroid.TX_BROADCAST";

    /**
     * unique task id
     */
    protected int notificationID;

    /**
     * The constant TRANSFER_MAX_COUNT.
     */
    protected static final int TRANSFER_MAX_COUNT = 2;
    /**
     * contains all transfer tasks, including failed, cancelled, finished, transferring, waiting tasks.
     */
    protected Map<Integer, TransferTask> allTaskList = new HashMap<>();
    /**
     * contains currently transferring tasks
     */
    protected List<TransferTask> transferringList = Lists.newArrayList();
    /**
     * contains waiting tasks
     */
    protected List<TransferTask> waitingList = Lists.newArrayList();

    /**
     * Gets task.
     *
     * @param taskID the task id
     * @return the task
     */
    protected synchronized TransferTask getTask(int taskID) {
       return allTaskList.get(taskID);
    }

    /**
     * Gets task info.
     *
     * @param taskID the task id
     * @return the task info
     */
    public TransferTaskInfo getTaskInfo(int taskID) {
        TransferTask task = getTask(taskID);
        if (task != null) {
            return task.getTaskInfo();
        }

        return null;
    }

    private synchronized boolean hasInQue(TransferTask transferTask) {
        if (waitingList.contains(transferTask)) {
            // Log.d(DEBUG_TAG, "in  Que  " + taskID + " " + repoName + path + "in waiting list");
            return true;
        }

        if (transferringList.contains(transferTask)) {
            // Log.d(DEBUG_TAG, "in  Que  " + taskID + " " + repoName + path + " in downloading list");
            return true;
        }
        return false;
    }

    /**
     * Add task to que.
     *
     * @param task the task
     */
    protected void addTaskToQue(TransferTask task) {
        if (!hasInQue(task)) {
            // remove the cancelled or failed task if any
            synchronized (this) {
                allTaskList.remove(task);

                // add new created task
                allTaskList.put(task.getTaskID(),task);

                // Log.d(DEBUG_TAG, "add Que  " + taskID + " " + repoName + path);
                waitingList.add(task);


            }
            doNext();
        }
    }

    /**
     * Do next.
     */
    public synchronized void doNext() {
        if (!waitingList.isEmpty()
                && transferringList.size() < TRANSFER_MAX_COUNT) {
            Log.d(DEBUG_TAG, "do next!");

            TransferTask task = waitingList.remove(0);
            transferringList.add(task);
            SeadroidApplication.getInstance().setCameraUploadNumber(waitingList.size(), allTaskList.size());
            SeadroidApplication.getInstance().setScanUploadStatus(CameraSyncStatus.UPLOADING);
            EventBus.getDefault().post(new CameraSyncEvent("upload"));
            ConcurrentAsyncTask.execute(task);
        }
    }

    /**
     * Cancel.
     *
     * @param taskID the task id
     */
    protected void cancel(int taskID) {
        TransferTask task = getTask(taskID);
        if (task != null) {
            task.cancel();

        }

        remove(taskID);
    }

    /**
     * Remove.
     *
     * @param taskID the task id
     */
    protected synchronized void remove(int taskID) {

        TransferTask toCancel = getTask(taskID);
        if (toCancel == null)
            return;

        if (!waitingList.isEmpty()) {
            waitingList.remove(toCancel);
        }

        if (!transferringList.isEmpty()) {
            transferringList.remove(toCancel);
        }
    }

    /**
     * Remove in all task list.
     *
     * @param taskID the task id
     */
    public synchronized void removeInAllTaskList(int taskID) {
        allTaskList.remove(taskID);
    }

    /**
     * Gets tasks by state.
     *
     * @param taskState the task state
     * @return the tasks by state
     */
    public synchronized List<TransferTask> getTasksByState(TaskState taskState) {
        List<TransferTask> taskList = Lists.newArrayList();
        Collection<TransferTask> values = allTaskList.values();
        for (TransferTask value : values) {
            if (value.state.equals(taskState)) {
                taskList.add(value);
            }
        }
        return taskList;
    }

    /**
     * remove tasks from {@link #allTaskList} by comparing the taskState,
     * all tasks with the same taskState will be removed.
     *
     * @param taskState taskState
     */
    public synchronized void removeByState(TaskState taskState) {
        Iterator<Map.Entry<Integer, TransferTask>> iterator = allTaskList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, TransferTask> next = iterator.next();
            TransferTask value = next.getValue();
            if (value.getState().equals(taskState)) {
                iterator.remove();
            }
        }
    }

    /**
     * remove tasks from {@link #allTaskList} by traversing the taskId list
     *
     * @param ids taskId list
     */
    public synchronized void removeByIds(List<Integer> ids) {
        for (int taskID : ids) {
            allTaskList.remove(taskID);
        }
    }

    /**
     * check if there are tasks under transferring state
     *
     * @return true, if there are tasks whose {@link com.seafile.seadroid2.transfer.TaskState} is {@code TRANSFERRING}.          false, otherwise.
     */
    public boolean isTransferring() {
        List<? extends TransferTaskInfo> transferTaskInfos = getAllTaskInfoList();
        for (TransferTaskInfo transferTaskInfo : transferTaskInfos) {
            if (transferTaskInfo.state.equals(TaskState.TRANSFERRING))
                return true;
        }
        return false;
    }

    /**
     * Cancel all.
     */
    public void cancelAll() {
        List<? extends TransferTaskInfo> transferTaskInfos = getAllTaskInfoList();
        for (TransferTaskInfo transferTaskInfo : transferTaskInfos) {
            cancel(transferTaskInfo.taskID);
        }
    }

    /**
     * Cancel by ids.
     *
     * @param taskIds the task ids
     */
    public void cancelByIds(List<Integer> taskIds) {
        for (int taskID : taskIds) {
            cancel(taskID);
        }
    }

    /**
     * Gets all task info list.
     *
     * @return the all task info list
     */
    public synchronized List<? extends TransferTaskInfo> getAllTaskInfoList() {
        ArrayList<TransferTaskInfo> infos = Lists.newArrayList();
        Collection<TransferTask> values = allTaskList.values();
        for (TransferTask value : values) {
            infos.add(value.getTaskInfo());
        }

        return infos;
    }

}
