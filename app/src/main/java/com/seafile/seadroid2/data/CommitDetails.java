package com.seafile.seadroid2.data;

import com.google.common.collect.Lists;
import com.seafile.seadroid2.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Commit details for activities history changes
 */
public class CommitDetails {
    /**
     * The Added files.
     */
    public List<String> addedFiles;
    /**
     * The Deleted files.
     */
    public List<String> deletedFiles;
    /**
     * The Modified files.
     */
    public List<String> modifiedFiles;
    /**
     * The Renamed files.
     */
    public List<String> renamedFiles;
    /**
     * The Added dirs.
     */
    public List<String> addedDirs;
    /**
     * The Deleted dirs.
     */
    public List<String> deletedDirs;

    /**
     * Instantiates a new Commit details.
     */
    public CommitDetails() {
        addedFiles = Lists.newArrayList();
        deletedFiles = Lists.newArrayList();
        modifiedFiles = Lists.newArrayList();
        renamedFiles = Lists.newArrayList();
        addedDirs = Lists.newArrayList();
        deletedDirs = Lists.newArrayList();
    }

    /**
     * From json commit details.
     *
     * @param json the json
     * @return the commit details
     * @throws JSONException the json exception
     */
    public static CommitDetails fromJson(String json) throws JSONException {
        final JSONObject jsonObject = Utils.parseJsonObject(json);
        final JSONArray addedFiles = jsonObject.optJSONArray("added_files");
        final JSONArray deletedFiles = jsonObject.optJSONArray("deleted_files");
        final JSONArray modifiedFiles = jsonObject.optJSONArray("modified_files");
        final JSONArray renamedFiles = jsonObject.optJSONArray("renamed_files");
        final JSONArray addedDirs = jsonObject.optJSONArray("added_dirs");
        final JSONArray deletedDirs = jsonObject.optJSONArray("deleted_dirs");

        CommitDetails details = new CommitDetails();
        processFileList(details.addedFiles, addedFiles);
        processFileList(details.deletedFiles, deletedFiles);
        processFileList(details.modifiedFiles, modifiedFiles);
        processFileList(details.renamedFiles, renamedFiles);
        processFileList(details.addedDirs, addedDirs);
        processFileList(details.deletedDirs, deletedDirs);

        return details;
    }

    private static void processFileList(List<String> list, JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) return;

        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
    }

    /**
     * Gets deleted dirs.
     *
     * @return the deleted dirs
     */
    public List<String> getDeletedDirs() {
        return deletedDirs;
    }

    /**
     * Sets deleted dirs.
     *
     * @param deletedDirs the deleted dirs
     */
    public void setDeletedDirs(List<String> deletedDirs) {
        this.deletedDirs = deletedDirs;
    }

    /**
     * Gets renamed files.
     *
     * @return the renamed files
     */
    public List<String> getRenamedFiles() {
        return renamedFiles;
    }

    /**
     * Sets renamed files.
     *
     * @param renamedFiles the renamed files
     */
    public void setRenamedFiles(List<String> renamedFiles) {
        this.renamedFiles = renamedFiles;
    }

    /**
     * Gets modified files.
     *
     * @return the modified files
     */
    public List<String> getModifiedFiles() {
        return modifiedFiles;
    }

    /**
     * Sets modified files.
     *
     * @param modifiedFiles the modified files
     */
    public void setModifiedFiles(List<String> modifiedFiles) {
        this.modifiedFiles = modifiedFiles;
    }

    /**
     * Gets added files.
     *
     * @return the added files
     */
    public List<String> getAddedFiles() {
        return addedFiles;
    }

    /**
     * Sets added files.
     *
     * @param addedFiles the added files
     */
    public void setAddedFiles(List<String> addedFiles) {
        this.addedFiles = addedFiles;
    }

    /**
     * Gets deleted files.
     *
     * @return the deleted files
     */
    public List<String> getDeletedFiles() {
        return deletedFiles;
    }

    /**
     * Sets deleted files.
     *
     * @param deletedFiles the deleted files
     */
    public void setDeletedFiles(List<String> deletedFiles) {
        this.deletedFiles = deletedFiles;
    }

    /**
     * Gets added dirs.
     *
     * @return the added dirs
     */
    public List<String> getAddedDirs() {
        return addedDirs;
    }

    /**
     * Sets added dirs.
     *
     * @param addedDirs the added dirs
     */
    public void setAddedDirs(List<String> addedDirs) {
        this.addedDirs = addedDirs;
    }

}
