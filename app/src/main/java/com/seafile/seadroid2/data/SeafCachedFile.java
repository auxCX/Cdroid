package com.seafile.seadroid2.data;

import com.seafile.seadroid2.util.Utils;

import java.io.File;

/**
 * The type Seaf cached file.
 */
public class SeafCachedFile implements SeafItem {
    /**
     * The Id.
     */
    public int id;
    /**
     * The File id.
     */
    public String fileID;
    /**
     * The Repo name.
     */
    public String repoName;
    /**
     * The Repo id.
     */
    public String repoID;
    /**
     * The Path.
     */
    public String path;
    /**
     * The Account signature.
     */
    public String accountSignature;
    /**
     * The File original size.
     */
    public long fileOriginalSize;
    /**
     * The File.
     */
    protected File file;

    /**
     * Instantiates a new Seaf cached file.
     */
    public SeafCachedFile() {
        id = -1;
    }

    @Override
    public String getTitle() {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    @Override
    public String getSubtitle() {
        return Utils.readableFileSize(file.length());
    }

    @Override
    public int getIcon() {
        return Utils.getFileIcon(file.getName());
    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public long getSize() {
        return file.length();
    }

    /**
     * Gets last modified.
     *
     * @return the last modified
     */
    public long getLastModified() {
        return file.lastModified();
    }

    /**
     * Is directory boolean.
     *
     * @return the boolean
     */
    public boolean isDirectory() {
        return file.isDirectory();
    }

    /**
     * Gets account signature.
     *
     * @return the account signature
     */
    public String getAccountSignature() {
        return accountSignature;
    }

    /**
     * Gets file original size.
     *
     * @return the file original size
     */
    public long getFileOriginalSize() {
        return fileOriginalSize;
    }

    /**
     * Sets file original size.
     *
     * @param fileOriginalSize the file original size
     */
    public void setFileOriginalSize(long fileOriginalSize) {
        this.fileOriginalSize = fileOriginalSize;
    }
}
