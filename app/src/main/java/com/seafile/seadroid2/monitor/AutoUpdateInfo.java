package com.seafile.seadroid2.monitor;

import com.google.common.base.Objects;
import com.seafile.seadroid2.SettingsManager;
import com.seafile.seadroid2.account.Account;

/**
 * The type Auto update info.
 */
class AutoUpdateInfo {
     public String updateVersion;
    /**
     * The Account.
     */
    final Account account;

    /**
     * The Repo id.
     */
    final String repoID;
    /**
     * The Repo name.
     */
    final String repoName;
    /**
     * The Parent dir.
     */
    final String parentDir;
    /**
     * The Local path.
     */
    final String localPath;



    /**
     * Instantiates a new Auto update info.
     *
     * @param account   the account
     * @param repoID    the repo id
     * @param repoName  the repo name
     * @param parentDir the parent dir
     * @param localPath the local path
     */
    public AutoUpdateInfo(Account account, String repoID, String repoName, String parentDir,
                          String localPath, String updateVersion) {

        this.account = account;
        this.repoID = repoID;
        this.repoName = repoName;
        this.parentDir = parentDir;
        this.localPath = localPath;
        this.updateVersion = "0";
    }

    /**
     * Can local decrypt boolean.
     *
     * @return the boolean
     */
    public boolean canLocalDecrypt() {
        return SettingsManager.instance().isEncryptEnabled();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || (obj.getClass() != this.getClass()))
            return false;

        AutoUpdateInfo that = (AutoUpdateInfo) obj;
        if(that.account == null || that.repoID == null || that.repoName == null || that.parentDir == null || that.localPath == null) {
            return false;
        }

        return that.account.equals(this.account) && that.repoID.equals(this.repoID) &&
                that.repoName.equals(this.repoName) && that.parentDir.equals(this.parentDir) &&
                that.localPath.equals(this.localPath);
    }

    private volatile int hashCode = 0;

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = Objects.hashCode(account, repoID, repoName, parentDir, localPath);
        }

        return hashCode;
    }
}
