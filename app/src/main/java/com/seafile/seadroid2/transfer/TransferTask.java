package com.seafile.seadroid2.transfer;

import android.os.AsyncTask;
import com.seafile.seadroid2.SeafException;
import com.seafile.seadroid2.account.Account;

import java.io.File;

/**
 * Base class for transferring data
 * <p/>
 * reference for override equals and hashcode, http://www.javaranch.com/journal/2002/10/equalhash.html
 * <p/>
 */
public abstract class TransferTask extends AsyncTask<Void, Long, File> {

    /**
     * The Task id.
     */
    protected int taskID;
    /**
     * The Account.
     */
    protected Account account;
    /**
     * The Repo name.
     */
    protected String repoName;
    /**
     * The Repo id.
     */
    protected String repoID;
    /**
     * The Path.
     */
    protected String path;
    /**
     * The Total size.
     */
    protected long totalSize, /**
     * The Finished.
     */
    finished;
    /**
     * The State.
     */
    protected TaskState state;
    /**
     * The Err.
     */
    protected SeafException err;

    /**
     * Instantiates a new Transfer task.
     *
     * @param taskID   the task id
     * @param account  the account
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param path     the path
     */
    public TransferTask(int taskID, Account account, String repoName, String repoID, String path) {
        this.account = account;
        this.repoName = repoName;
        this.repoID = repoID;
        this.path = path;
        this.state = TaskState.INIT;

        // The size of the file would be known in the first progress update
        this.totalSize = -1;
        this.taskID = taskID;
    }

    /**
     * Cancel.
     */
    protected void cancel() {
        if (state != TaskState.INIT && state != TaskState.TRANSFERRING) {
            return;
        }
        state = TaskState.CANCELLED;
        super.cancel(true);
    }

    /**
     * Can retry boolean.
     *
     * @return the boolean
     */
    protected boolean canRetry() {
        return state == TaskState.CANCELLED || state == TaskState.FAILED;
    }

    /**
     * Gets task info.
     *
     * @return the task info
     */
    protected abstract TransferTaskInfo getTaskInfo();

    /**
     * Gets task id.
     *
     * @return the task id
     */
    public int getTaskID() {
        return taskID;
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public TaskState getState() {
        return state;
    }

    /**
     * Gets account.
     *
     * @return the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Gets repo name.
     *
     * @return the repo name
     */
    public String getRepoName() {
        return repoName;
    }

    /**
     * Gets repo id.
     *
     * @return the repo id
     */
    public String getRepoID() {
        return repoID;
    }

    /**
     * Gets total size.
     *
     * @return the total size
     */
    public long getTotalSize() {
        return totalSize;
    }

    /**
     * Gets finished.
     *
     * @return the finished
     */
    public long getFinished() {
        return finished;
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if ((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        TransferTask tt = (TransferTask) obj;
        return (account.getSignature() == tt.account.getSignature() || (account.getSignature() != null && account.getSignature().equals(tt.account.getSignature())))
                && (repoID == tt.repoID || (repoID != null && repoID.equals(tt.repoID)))
                && (path == tt.path || (path != null && path.equals(tt.path)));
    }

    @Override
    public String toString() {
        return "email " + account.getEmail() + " server " + account.getServer() + " taskID " + taskID + " repoID " + repoID +
                " repoName " + repoName + " path " + path;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (account.getSignature() == null ? 0 : account.getSignature().hashCode());
        hash = 31 * hash + (repoID == null ? 0 : repoID.hashCode());
        hash = 31 * hash + (path == null ? 0 : path.hashCode());
        return hash;
    }
}
