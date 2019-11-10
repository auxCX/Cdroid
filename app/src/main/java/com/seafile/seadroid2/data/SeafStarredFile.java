package com.seafile.seadroid2.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.util.Utils;

/**
 * The type Seaf starred file.
 */
public class SeafStarredFile implements SeafItem {
    /**
     * The enum File type.
     */
    public enum FileType {
        /**
         * Dir file type.
         */
        DIR,
        /**
         * File file type.
         */
        FILE };
    private static final String DEBUG_TAG = "SeafStarredFile";

    private String repoID;
    private long mtime;
    private String path;
    private FileType type;
    private long size;    // size of file, 0 if type is dir

    /**
     * From json seaf starred file.
     *
     * @param obj the obj
     * @return the seaf starred file
     */
    static SeafStarredFile fromJson(JSONObject obj) {
        SeafStarredFile starredFile = new SeafStarredFile();
        try {
            starredFile.repoID = obj.getString("repo");
            starredFile.mtime = obj.getLong("mtime");
            starredFile.path = obj.getString("path");
            starredFile.size = obj.getLong("size");
            boolean type = obj.getBoolean("dir");
            if (!type) {
                starredFile.type = FileType.FILE;
            } else
                starredFile.type = FileType.DIR;
            return starredFile;
        } catch (JSONException e) {
            Log.d(DEBUG_TAG, e.getMessage());
            return null;
        }
    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * Gets mtime.
     *
     * @return the mtime
     */
    public long getMtime() {
        return mtime;
    }

    /**
     * Is dir boolean.
     *
     * @return the boolean
     */
    public boolean isDir() {
        return (type == FileType.DIR);
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

    @Override
    public String getTitle() {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    @Override
    public String getSubtitle() {
        String timestamp = Utils.translateCommitTime(mtime * 1000);
        if (isDir())
            return timestamp;
        return Utils.readableFileSize(size) + ", " + timestamp;
    }

    @Override
    public int getIcon() {
        if (isDir())
            return R.drawable.folder;
        return Utils.getFileIcon(getTitle());
    }
}
