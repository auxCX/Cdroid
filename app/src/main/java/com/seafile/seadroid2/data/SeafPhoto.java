package com.seafile.seadroid2.data;

import java.io.Serializable;

/**
 * Photo entity for displaying photos in gallery
 */
public class SeafPhoto implements Serializable {
    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 0L;

    /** download shows tatus */
    private boolean downloaded;
    /** display name */
    private String name;
    /** repo name */
    private String repoName;
    /** repo id */
    private String repoID;
    /** dir path */
    private String dirPath;
    /** related {@link SeafDirent} */
    private SeafDirent dirent;

    /**
     * Instantiates a new Seaf photo.
     *
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param dirPath  the dir path
     * @param dirent   the dirent
     */
    public SeafPhoto(String repoName, String repoID, String dirPath, SeafDirent dirent) {
        this.repoName = repoName;
        this.repoID = repoID;
        this.dirPath = dirPath;
        this.dirent = dirent;
        this.name = dirent.name;
    }

    /**
     * Gets downloaded.
     *
     * @return the downloaded
     */
    public boolean getDownloaded() {
        return downloaded;
    }

    /**
     * Sets downloaded.
     *
     * @param downloaded the downloaded
     */
    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
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
     * Gets dirent.
     *
     * @return the dirent
     */
    public SeafDirent getDirent() {
        return dirent;
    }

}