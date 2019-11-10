package com.seafile.seadroid2.transfer;

/**
 * The type Pending upload info.
 */
public class PendingUploadInfo {

    /**
     * The Repo id.
     */
    public String repoID;
    /**
     * The Repo name.
     */
    public String repoName;
    /**
     * The Target dir.
     */
    public String targetDir;
    /**
     * The Local file path.
     */
    public String localFilePath;
    /**
     * The Is update.
     */
    public boolean isUpdate;
    /**
     * The Is copy to local.
     */
    public boolean isCopyToLocal;

    /**
     * Instantiates a new Pending upload info.
     *
     * @param repoID        the repo id
     * @param repoName      the repo name
     * @param targetDir     the target dir
     * @param localFilePath the local file path
     * @param isUpdate      the is update
     * @param isCopyToLocal the is copy to local
     */
    public PendingUploadInfo(String repoID, String repoName, String targetDir,
            String localFilePath, boolean isUpdate, boolean isCopyToLocal) {
        this.repoID = repoID;
        this.repoName = repoName;
        this.targetDir = targetDir;
        this.localFilePath = localFilePath;
        this.isUpdate = isUpdate;
        this.isCopyToLocal = isCopyToLocal;
    }

}
