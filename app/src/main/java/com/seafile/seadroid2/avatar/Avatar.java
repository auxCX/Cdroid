package com.seafile.seadroid2.avatar;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Objects;

import android.util.Log;

/**
 * The type Avatar.
 */
public class Avatar {
    private static final String DEBUG_TAG = "Avatar";
    
    private String signature; // Account Signature
    private String url;
    private long mtime;
    // private boolean is_default;

    /**
     * From json avatar.
     *
     * @param obj the obj
     * @return the avatar
     */
    static Avatar fromJson(JSONObject obj) {
        Avatar avatar = new Avatar();
        try {
            avatar.url = obj.getString("url");
            avatar.mtime = obj.getLong("mtime");
            // avatar.is_default = obj.getBoolean("is_default");
            
            return avatar;
        } catch (JSONException e) {
            Log.d(DEBUG_TAG, e.getMessage());
            return null;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(url, mtime);
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
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
     * Sets mtime.
     *
     * @param mtime the mtime
     */
    public void setMtime(long mtime) {
        this.mtime = mtime;
    }

    /*public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }*/

    /**
     * Gets signature.
     *
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Sets signature.
     *
     * @param signature the signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("signature", signature)
                .add("url", url)
                .add("mtime", mtime)
                /*.add("is_default", is_default)*/
                .toString();
    }
}
