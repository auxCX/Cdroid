package com.seafile.seadroid2.data;

import android.util.Log;

import com.seafile.seadroid2.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Seafile event entity
 */
public class SeafEvent implements SeafItem {
    /**
     * The constant DEBUG_TAG.
     */
    public static final String DEBUG_TAG = SeafItem.class.getSimpleName();

    /**
     * The constant EVENT_TYPE_REPO_CREATE.
     */
    public static final String EVENT_TYPE_REPO_CREATE = "repo-create";
    /**
     * The constant EVENT_TYPE_REPO_DELETE.
     */
    public static final String EVENT_TYPE_REPO_DELETE = "repo-delete";

    // true for events like a file upload by unregistered user from a
    // uploadable link
    private boolean anonymous;
    private String repo_id;
    private String author;
    private String nick;
    private long time;
    private String v_time;
    private String etype;
    private String repo_name;
    private String desc;
    private String commit_id;
    private String date;
    private String name;
    private String time_relative;
    private String converted_cmmt_desc;
    private String avatar;
    private String avatar_url;
    private boolean repo_encrypted;
    private boolean more_files;
    private String path;
    private String op_type;
    private String obj_type;
    private String author_name;

    /**
     * From json seaf event.
     *
     * @param obj the obj
     * @return the seaf event
     */
    public static SeafEvent fromJson(JSONObject obj) {
        SeafEvent event = new SeafEvent();
        try {
            event.author = obj.optString("author");
            if (event.author.isEmpty()) {
                event.author = "anonymous";
                event.anonymous = true;
            } else {
                event.anonymous = false;
            }

            event.repo_id = obj.optString("repo_id");
            event.nick = obj.optString("nick");
            if (event.nick.isEmpty()) {
                event.nick = "anonymous";
            }

            event.author_name = obj.optString("author_name");
            event.path = obj.optString("path");
            event.op_type = obj.optString("op_type");
            event.obj_type = obj.optString("obj_type");
            event.etype = obj.optString("etype");
            event.repo_name = obj.optString("repo_name");
            event.v_time = obj.optString("time");
            event.time = obj.optLong("time");
            event.avatar = obj.optString("avatar");
            event.avatar_url = obj.optString("avatar_url");
            event.commit_id = obj.optString("commit_id");
            event.date = obj.optString("date");
            event.name = obj.optString("name");
            event.time_relative = obj.optString("time_relative");
            event.converted_cmmt_desc = obj.optString("converted_cmmt_desc");
            event.repo_encrypted = obj.optBoolean("repo_encrypted");
            event.more_files = obj.optBoolean("more_files");

            event.desc = obj.optString("desc");
            if (event.etype.equals(EVENT_TYPE_REPO_CREATE)) {
                event.desc = String.format("Created library \"%s\"", event.repo_name);
            } else if (event.etype.equals(EVENT_TYPE_REPO_DELETE)) {
                event.desc = String.format("Deleted library \"%s\"", event.repo_name);
            }

            event.desc = translateCommitDesc(event.desc);
            return event;
        } catch (Exception e) {
            Log.d(DEBUG_TAG, e.getMessage());
            return null;
        }
    }

    private static Matcher fullMatch(Pattern pattern, String str) {
        Matcher matcher = pattern.matcher(str);
        return matcher.matches() ? matcher : null;
    }

    /**
     * Translate commit desc string.
     *
     * @param value the value
     * @return the string
     */
    public static String translateCommitDesc(String value) {
        if (value.startsWith("Reverted repo")) {
            value.replace("repo", "library");
        }

        if (value.startsWith("Reverted library")) {
            return value.replace("Reverted library to status at", "Reverted library to status at");
        } else if (value.startsWith("Reverted file")) {
            String regex = "Reverted file \"(.*)\" to status at (.*)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher;
            if ((matcher = fullMatch(pattern, value)) != null) {
                String name = matcher.group(1);
                String time = matcher.group(2);
                return String.format("Reverted file \"%s\" to status at %s.", name, time);
            }

        } else if (value.startsWith("Recovered deleted directory")) {
            return value.replace("Recovered deleted directory", "Recovered deleted directory");
        } else if (value.startsWith("Changed library")) {
            return value.replace("Changed library name or description", "Changed library name or description");
        } else if (value.startsWith("Merged") || value.startsWith("Auto merge")) {
            return "Auto merge by seafile system";
        } else if (value.startsWith("Deleted")) {
            return value;
        }

        final String[] lines = value.split("\n");
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            final String translateLine = translateLine(lines[i]);
            out.append(translateLine);
            // should avoid append for the last item
            if (i < lines.length - 1) out.append("\n");
        }

        return out.toString();
    }

    private static String translateLine(String line) {
        // String regex = String.format("(%s) \"(.*)\"\\s?(and ([0-9]+) more (files|directories))?", getOperations());
        // String regex = String.format("(%s).* ".*\..*"\s+(and ([0-9]+) more (files|directories))?", getOperations());
        String regex = String.format("(%s).* \"\\S+\\.\\S+\"\\s+(and ([0-9]+) more (files|directories))?", getOperations());
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher;
        if ((matcher = fullMatch(pattern, line)) == null) {
            return line;
        }

        String op = matcher.group(1);
        String file_name = matcher.group(2);
        String has_more = matcher.group(3);
        String n_more = matcher.group(4);
        String more_type = matcher.group(5);

        String op_trans = (getVerbsMap().get(op) == null ? op : getVerbsMap().get(op));

        String type, ret;
        // has more may be null caused a crash
        if (has_more.length() > 0) {
            if (more_type.equals("files")) {
                type = "files";
            } else {
                type = "directories";
            }

            String more = String.format("and %s more", n_more);
            ret = String.format("%s \"%s\" %s %s.", op_trans, file_name, more, type);
        } else {
            ret = String.format("%s \"%s\".", op_trans, file_name);
        }

        return ret;
    }

    private static HashMap<String, String> verbsMap = null;
    private static HashMap<String, String> getVerbsMap() {
        if (verbsMap == null) {
            verbsMap = new HashMap<>();
            verbsMap.put("Added", "Added");
            verbsMap.put("Deleted", "Deleted");
            verbsMap.put("Removed", "Removed");
            verbsMap.put("Modified", "Modified");
            verbsMap.put("Renamed", "Renamed");
            verbsMap.put("Moved", "Moved");
            verbsMap.put("Added directory", "Added directory");
            verbsMap.put("Removed directory", "Removed directory");
            verbsMap.put("Renamed directory", "Renamed directory");
            verbsMap.put("Moved directory", "Moved directory");
        }

        return verbsMap;
    }

    private static String getOperations() {
        return "Added|Deleted|Removed|Modified|Renamed|Moved|Added directory|Removed directory|Renamed directory|Moved directory";
    }

    /**
     * Is anonymous boolean.
     *
     * @return the boolean
     */
    public boolean isAnonymous() {
        return anonymous;
    }

    /**
     * Sets anonymous.
     *
     * @param anonymous the anonymous
     */
    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
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
     * Sets author.
     *
     * @param author the author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Sets nick.
     *
     * @param nick the nick
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * Sets etype.
     *
     * @param etype the etype
     */
    public void setEtype(String etype) {
        this.etype = etype;
    }

    /**
     * Sets repo name.
     *
     * @param repo_name the repo name
     */
    public void setRepo_name(String repo_name) {
        this.repo_name = repo_name;
    }

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
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
     * Gets author.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Gets nick.
     *
     * @return the nick
     */
    public String getNick() {
        return nick;
    }

    /**
     * Gets time.
     *
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * Gets etype.
     *
     * @return the etype
     */
    public String getEtype() {
        return etype;
    }

    /**
     * Gets repo name.
     *
     * @return the repo name
     */
    public String getRepo_name() {
        return repo_name;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    @Override
    public String getTitle() {
        return desc;
    }

    @Override
    public String getSubtitle() {
        return nick;
    }

    @Override
    public int getIcon() {
        return R.drawable.repo;
    }

    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Gets commit id.
     *
     * @return the commit id
     */
    public String getCommit_id() {
        return commit_id;
    }

    /**
     * Sets commit id.
     *
     * @param commit_id the commit id
     */
    public void setCommit_id(String commit_id) {
        this.commit_id = commit_id;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Set path.
     *
     * @param path the path
     */
    public void setPath(String path){
        this.path = path;
    }

    /**
     * Get path string.
     *
     * @return the string
     */
    public String getPath(){
        return path;
    }

    /**
     * Set v time.
     *
     * @param v_time the v time
     */
    public void setV_time(String v_time){
        this.v_time = v_time;
    }

    /**
     * Get v time string.
     *
     * @return the string
     */
    public String getV_time(){
        return v_time;
    }

    /**
     * Set author name.
     *
     * @param author_name the author name
     */
    public void setAuthor_name(String author_name){
        this.author_name = author_name;
    }

    /**
     * Get author name string.
     *
     * @return the string
     */
    public String getAuthor_name(){
        return author_name;
    }

    /**
     * Set op type.
     *
     * @param op_type the op type
     */
    public void setOp_type(String op_type){
        this.op_type = op_type;
    }

    /**
     * Get op type string.
     *
     * @return the string
     */
    public String getOp_type(){
        return op_type;
    }

    /**
     * Set obj type.
     *
     * @param obj_type the obj type
     */
    public void setObj_type(String obj_type){
        this.obj_type = obj_type;
    }

    /**
     * Get obj type string.
     *
     * @return the string
     */
    public String getObj_type(){
        return obj_type;
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
     * Gets time relative.
     *
     * @return the time relative
     */
    public String getTime_relative() {
        return time_relative;
    }

    /**
     * Sets time relative.
     *
     * @param time_relative the time relative
     */
    public void setTime_relative(String time_relative) {
        this.time_relative = time_relative;
    }

    /**
     * Gets converted cmmt desc.
     *
     * @return the converted cmmt desc
     */
    public String getConverted_cmmt_desc() {
        return converted_cmmt_desc;
    }

    /**
     * Sets converted cmmt desc.
     *
     * @param converted_cmmt_desc the converted cmmt desc
     */
    public void setConverted_cmmt_desc(String converted_cmmt_desc) {
        this.converted_cmmt_desc = converted_cmmt_desc;
    }

    /**
     * Gets avatar.
     *
     * @return the avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Gets avatar url.
     *
     * @return the avatar url
     */
    public String getAvatar_url() {
        return avatar_url;
    }

    /**
     * Sets avatar url.
     *
     * @param avatar_url the avatar url
     */
    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    /**
     * Sets avatar.
     *
     * @param avatar the avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Is repo encrypted boolean.
     *
     * @return the boolean
     */
    public boolean isRepo_encrypted() {
        return repo_encrypted;
    }

    /**
     * Sets repo encrypted.
     *
     * @param repo_encrypted the repo encrypted
     */
    public void setRepo_encrypted(boolean repo_encrypted) {
        this.repo_encrypted = repo_encrypted;
    }

    /**
     * Is more files boolean.
     *
     * @return the boolean
     */
    public boolean isMore_files() {
        return more_files;
    }

    /**
     * Sets more files.
     *
     * @param more_files the more files
     */
    public void setMore_files(boolean more_files) {
        this.more_files = more_files;
    }

}
