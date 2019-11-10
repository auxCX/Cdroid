package com.seafile.seadroid2.monitor;

import java.util.List;
import java.util.Map;

import org.apache.commons.io.monitor.FileAlterationMonitor;

import android.util.Log;

import com.google.common.collect.Maps;
import com.seafile.seadroid2.SeadroidApplication;
import com.seafile.seadroid2.account.Account;
import com.seafile.seadroid2.account.AccountManager;

/**
 * The type Seafile monitor.
 */
public class SeafileMonitor {
    private static final String DEBUG_TAG = "SeafileMonitor";

    private Map<Account, SeafileObserver> observers = Maps.newHashMap();
    private FileAlterationMonitor alterationMonitor = new FileAlterationMonitor();
    private CachedFileChangedListener listener;
    private boolean started;

    /**
     * Instantiates a new Seafile monitor.
     *
     * @param listener the listener
     */
    public SeafileMonitor(CachedFileChangedListener listener) {
        this.listener = listener;
    }

    /**
     * Is started boolean.
     *
     * @return the boolean
     */
    public boolean isStarted() {
        return started;
    }

    private synchronized void monitorFilesForAccount(Account account) {
        if (observers.containsKey(account)) {
            return;
        }
        SeafileObserver fileObserver = new SeafileObserver(account, listener);
        addObserver(fileObserver);
        observers.put(account, fileObserver);
    }

    /**
     * Stop monitor files for account.
     *
     * @param account the account
     */
    public synchronized void stopMonitorFilesForAccount(Account account) {
        SeafileObserver fileObserver = observers.get(account);
        if (fileObserver != null)
            removeObserver(fileObserver);

        observers.remove(account);
    }

    private void addObserver(SeafileObserver fileObserver) {
        alterationMonitor.addObserver(fileObserver.getAlterationObserver());
    }

    private void removeObserver(SeafileObserver fileObserver) {
        alterationMonitor.removeObserver(fileObserver.getAlterationObserver());
    }

    /**
     * On file downloaded.
     *
     * @param account    the account
     * @param repoID     the repo id
     * @param repoName   the repo name
     * @param pathInRepo the path in repo
     * @param localPath  the local path
     */
    public synchronized void onFileDownloaded(Account account, String repoID, String repoName,
            String pathInRepo, String localPath) {
        SeafileObserver observer = observers.get(account);
        if (observer == null)
            return;
        observer.watchDownloadedFile(repoID, repoName, pathInRepo, localPath);
    }

    private void start() throws Exception {
        if (!started) {
            alterationMonitor.start();
            started = true;
        }
    }

    /**
     * Stop.
     *
     * @throws Exception the exception
     */
    public void stop() throws Exception {
        alterationMonitor.stop();
    }

    /**
     * Watch cached files for all accounts
     */
    public synchronized void monitorAllAccounts() {
        List<Account> accounts =
                new AccountManager(SeadroidApplication.getAppContext()).getAccountList();

        for (Account account : accounts) {
            monitorFilesForAccount(account);
        }

        try {
            start();
            Log.d(DEBUG_TAG, "monitor started");
        } catch (Exception e) {
            Log.w(DEBUG_TAG, "failed to start file monitor");
            throw new RuntimeException("failed to start file monitor");
        }
    }
}
