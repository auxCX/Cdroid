package com.seafile.seadroid2.data;

import org.json.JSONObject;

/**
 * Seafile share link bean
 */
public class SeafLink  {

    /**
     * The constant DEBUG_TAG.
     */
    public static final String DEBUG_TAG = SeafLink.class.getSimpleName();


    private String username;
    private String repo_id;
    private String ctime;
    private String expire;
    private String token;
    private String count;
    private String link;
    private String name;
    private String path;
    private String isDir;
    private String isExpired;
    private String repoName;


    /**
     * From json seaf link.
     *
     * @param obj the obj
     * @return the seaf link
     */
    public static SeafLink fromJson(JSONObject obj) {
        SeafLink link = new SeafLink();
        link.username = obj.optString("username");
        link.repo_id = obj.optString("repo_id");
        link.ctime = obj.optString("ctime");
        link.expire = obj.optString("expire_date");
        link.token = obj.optString("token");
        link.count = obj.optString("view_cnt");
        link.link = obj.optString("link");
        link.name = obj.optString("obj_name");
        link.path = obj.optString("path");
        link.isDir = obj.optString("is_dir");
        link.isExpired = obj.optString("is_expired");
        link.repoName = obj.optString("repo_name");
        return link;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets repo id.
     *
     * @return the repo id
     */
    public String getRepo_id() {
        return repo_id;
    }

    /**
     * Sets repo id.
     *
     * @param repo_id the repo id
     */
    public void setRepo_id(String repo_id) {
        this.repo_id = repo_id;
    }

    /**
     * Gets ctime.
     *
     * @return the ctime
     */
    public String getCtime() {
        return ctime;
    }

    /**
     * Sets ctime.
     *
     * @param ctime the ctime
     */
    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    /**
     * Gets expire.
     *
     * @return the expire
     */
    public String getExpire() {
        return expire;
    }

    /**
     * Sets expire.
     *
     * @param expire the expire
     */
    public void setExpire(String expire) {
        this.expire = expire;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets token.
     *
     * @param token the token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets count.
     *
     * @return the count
     */
    public String getCount() {
        return count;
    }

    /**
     * Sets count.
     *
     * @param count the count
     */
    public void setCount(String count) {
        this.count = count;
    }

    /**
     * Gets link.
     *
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets link.
     *
     * @param link the link
     */
    public void setLink(String link) {
        this.link = link;
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
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
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
     * Sets path.
     *
     * @param path the path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets is dir.
     *
     * @return the is dir
     */
    public String getIsDir() {
        return isDir;
    }

    /**
     * Sets is dir.
     *
     * @param isDir the is dir
     */
    public void setIsDir(String isDir) {
        this.isDir = isDir;
    }

    /**
     * Gets is expired.
     *
     * @return the is expired
     */
    public String getIsExpired() {
        return isExpired;
    }

    /**
     * Sets is expired.
     *
     * @param isExpired the is expired
     */
    public void setIsExpired(String isExpired) {
        this.isExpired = isExpired;
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
     * Sets repo name.
     *
     * @param repoName the repo name
     */
    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    @Override
    public String toString() {
        return "SeafLink{" +
                "username='" + username + '\'' +
                ", repo_id='" + repo_id + '\'' +
                ", ctime='" + ctime + '\'' +
                ", expire='" + expire + '\'' +
                ", token='" + token + '\'' +
                ", count='" + count + '\'' +
                ", link='" + link + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", isDir='" + isDir + '\'' +
                ", isExpired='" + isExpired + '\'' +
                ", repoName='" + repoName + '\'' +
                '}';
    }
}
