package com.seafile.seadroid2.ui;

/**
 * The type Nav context.
 */
public class NavContext {
    /**
     * The Repo id.
     */
    String repoID = null;
    /**
     * The Repo name.
     */
    String repoName = null;     // for display
    /**
     * The Dir path.
     */
    String dirPath = null;
    /**
     * The Dir id.
     */
    String dirID = null;
    /**
     * The Dir permission.
     */
    String dirPermission = null;

    /**
     * Instantiates a new Nav context.
     */
    public NavContext() {
    }

    /**
     * Sets repo id.
     *
     * @param repoID the repo id
     */
    public void setRepoID(String repoID) {
        this.repoID = repoID;
    }

    /**
     * Sets repo name.
     *
     * @param repoName the repo name
     */
    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    /**
     * Sets dir.
     *
     * @param path  the path
     * @param dirID the dir id
     */
    public void setDir(String path, String dirID) {
        this.dirPath = path;
        this.dirID = dirID;
    }

    /**
     * Sets dir id.
     *
     * @param dirID the dir id
     */
    public void setDirID(String dirID) {
        this.dirID = dirID;
    }

    /**
     * In repo boolean.
     *
     * @return the boolean
     */
    public boolean inRepo() {
        return repoID != null;
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
     * Gets repo name.
     *
     * @return the repo name
     */
    public String getRepoName() {
        return repoName;
    }

    /**
     * Is repo root boolean.
     *
     * @return the boolean
     */
    public boolean isRepoRoot() {
        return "/".equals(dirPath);
    }

    /**
     * Gets dir path.
     *
     * @return the dir path
     */
    public String getDirPath() {
        return dirPath;
    }

    /**
     * Gets dir id.
     *
     * @return the dir id
     */
    public String getDirID() {
        return dirID;
    }

    /**
     * Gets dir permission.
     *
     * @return the dir permission
     */
    public String getDirPermission() {
        return dirPermission;
    }

    /**
     * Sets dir permission.
     *
     * @param dirPermission the dir permission
     */
    public void setDirPermission(String dirPermission) {
        this.dirPermission = dirPermission;
    }
}
