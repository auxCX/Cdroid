package com.seafile.seadroid2.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.google.common.collect.Lists;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeafException;
import com.seafile.seadroid2.account.Account;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.data.SeafDirent;

import java.util.List;

/**
 * * AsyncTask for deleting files
 */
class DeleteTask extends TaskDialog.Task {
    /**
     * The constant DEBUG_TAG.
     */
    public static final String DEBUG_TAG = "DeleteTask";

    /**
     * The Repo id.
     */
    String repoID;
    /**
     * The Dirents.
     */
    List<SeafDirent> dirents;
    /**
     * The Path.
     */
    String path;
    /**
     * The Isdir.
     */
    boolean isdir;
    /**
     * The Data manager.
     */
    DataManager dataManager;
    /**
     * The Manager.
     */
    DeleteTaskManager manager;

    /**
     * Instantiates a new Delete task.
     *
     * @param repoID      the repo id
     * @param path        the path
     * @param isdir       the isdir
     * @param dataManager the data manager
     */
    public DeleteTask(String repoID, String path, boolean isdir, DataManager dataManager) {
        this.repoID = repoID;
        this.path = path;
        this.isdir = isdir;
        this.dataManager = dataManager;
    }

    /**
     * Instantiates a new Delete task.
     *
     * @param repoID      the repo id
     * @param path        the path
     * @param dirents     the dirents
     * @param dataManager the data manager
     */
    public DeleteTask(String repoID, String path, List<SeafDirent> dirents, DataManager dataManager) {
        this.repoID = repoID;
        this.path = path;
        this.dirents = dirents;
        this.dataManager = dataManager;
        this.manager = new DeleteTaskManager();
    }

    @Override
    protected void runTask() {
        try {
            // batch operation
            if (dirents != null) {
                for (SeafDirent dirent : dirents) {
                    DeleteCell cell = new DeleteCell(repoID, path + "/" + dirent.name, dirent.isDir());
                    manager.addTaskToQue(cell);
                }
                manager.doNext();
            } else
                dataManager.delete(repoID, path, isdir);
        } catch (SeafException e) {
            setTaskException(e);
        }
    }

    /**
     * Class for deleting files sequentially, starting one after the previous completes.
     */
    class DeleteTaskManager {

        /**
         * The Waiting list.
         */
        protected List<DeleteCell> waitingList = Lists.newArrayList();

        private synchronized boolean hasInQue(DeleteCell deleteTask) {
            if (waitingList.contains(deleteTask)) {
                // Log.d(DEBUG_TAG, "in  Que  " + deleteTask.getPath() + "in waiting list");
                return true;
            }

            return false;
        }

        /**
         * Add task to que.
         *
         * @param cell the cell
         */
        public void addTaskToQue(DeleteCell cell) {
            if (!hasInQue(cell)) {
                // remove the cancelled or failed cell if any
                synchronized (this) {
                    // Log.d(DEBUG_TAG, "------ add Que  " + cell.getPath());
                    waitingList.add(cell);
                }
            }
        }

        /**
         * Do next.
         */
        public synchronized void doNext() {
            if (!waitingList.isEmpty()) {
                // Log.d(DEBUG_TAG, "--- do next!");

                DeleteCell cell = waitingList.remove(0);

                try {
                    dataManager.delete(cell.getRepoID(), cell.getPath(), cell.isdir);
                } catch (SeafException e) {
                    setTaskException(e);
                }
                doNext();
            }
        }

    }

    /**
     * Class for queuing deleting tasks
     */
    class DeleteCell {
        private String repoID;
        private String path;
        private boolean isdir;

        /**
         * Instantiates a new Delete cell.
         *
         * @param repoID the repo id
         * @param path   the path
         * @param isdir  the isdir
         */
        public DeleteCell(String repoID, String path, boolean isdir) {
            this.repoID = repoID;
            this.path = path;
            this.isdir = isdir;
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
         * Gets path.
         *
         * @return the path
         */
        public String getPath() {
            return path;
        }

        /**
         * Isdir boolean.
         *
         * @return the boolean
         */
        public boolean isdir() {
            return isdir;
        }
    }
}

/**
 * The type Delete file dialog.
 */
public class DeleteFileDialog extends TaskDialog {
    private String repoID;
    private String path;
    private List<SeafDirent> dirents;
    private boolean isdir;

    private DataManager dataManager;
    private Account account;

    /**
     * Init.
     *
     * @param repoID  the repo id
     * @param path    the path
     * @param isdir   the isdir
     * @param account the account
     */
    public void init(String repoID, String path, boolean isdir, Account account) {
        this.repoID = repoID;
        this.path = path;
        this.isdir = isdir;
        this.account = account;
    }

    /**
     * Init.
     *
     * @param repoID  the repo id
     * @param path    the path
     * @param dirents the dirents
     * @param account the account
     */
    public void init(String repoID, String path, List<SeafDirent> dirents, Account account) {
        this.repoID = repoID;
        this.path = path;
        this.dirents = dirents;
        this.account = account;
    }

    private DataManager getDataManager() {
        if (dataManager == null) {
            dataManager = new DataManager(account);
        }
        return dataManager;
    }

    @Override
    protected View createDialogContentView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete_file, null);
        return view;
    }

    @Override
    protected void onDialogCreated(Dialog dialog) {
        String str = getActivity().getString(
                isdir ? R.string.delete_dir : R.string.delete_file_f);
        dialog.setTitle(str);
    }

    @Override
    protected DeleteTask prepareTask() {
        if (dirents != null) {
            return new DeleteTask(repoID, path, dirents, getDataManager());
        }
        return new DeleteTask(repoID, path, isdir, getDataManager());
    }
}
