package com.seafile.seadroid2.data;

import android.text.TextUtils;

import com.seafile.seadroid2.SettingsManager;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * The type Seaf repo encrypt.
 */
public class SeafRepoEncrypt {
    /**
     * The Id.
     */
    public String id;     // repo id
    /**
     * The Name.
     */
    public String name;
    /**
     * The Owner.
     */
    public String owner;
    /**
     * The Mtime.
     */
    public long mtime;    // the last modification time
    /**
     * The Is group repo.
     */
    public boolean isGroupRepo;
    /**
     * The Is personal repo.
     */
    public boolean isPersonalRepo;
    /**
     * The Is shared repo.
     */
    public boolean isSharedRepo;
    /**
     * The Encrypted.
     */
    public boolean encrypted;
    /**
     * The Permission.
     */
    public String permission;
    /**
     * The Magic.
     */
    public String magic;
    /**
     * The Enc key.
     */
    public String encKey;

    public String salt;

    /**
     * The Enc version.
     */
    public int encVersion;
    /**
     * The Size.
     */
    public long size;
    /**
     * The Root.
     */
    public String root; // the id of root directory

    /**
     * From json seaf repo encrypt.
     *
     * @param obj the obj
     * @return the seaf repo encrypt
     * @throws JSONException the json exception
     */
    static SeafRepoEncrypt fromJson(JSONObject obj) throws JSONException {
        SeafRepoEncrypt repo = new SeafRepoEncrypt();
        repo.magic = obj.optString("magic");
        repo.permission = obj.getString("permission");
        repo.encrypted = obj.getBoolean("encrypted");
        repo.encVersion = obj.optInt("enc_version");
        repo.mtime = obj.getLong("mtime");
        repo.owner = obj.getString("owner");
        repo.id = obj.getString("id");
        repo.size = obj.getLong("size");
        repo.name = obj.getString("name");
        repo.root = obj.getString("root");
        repo.encKey = obj.optString("random_key");
        repo.salt = obj.optString("salt");
        repo.isGroupRepo = obj.getString("type").equals("grepo");
        repo.isPersonalRepo = obj.getString("type").equals("repo");
        repo.isSharedRepo = obj.getString("type").equals("srepo");
        return repo;
    }

    /**
     * Instantiates a new Seaf repo encrypt.
     */
    public SeafRepoEncrypt() {
    }

    /**
     * Can local decrypt boolean.
     *
     * @return the boolean
     */
    public boolean canLocalDecrypt() {

        return encrypted
                && (encVersion == 2 || encVersion == 3)
                && !TextUtils.isEmpty(magic)
                && SettingsManager.instance().isEncryptEnabled();
    }

}
