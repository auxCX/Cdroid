package com.seafile.seadroid2.play;

/**
 * get video link state listener
 */
public interface VideoLinkStateListener {
    /**
     * On success.
     *
     * @param fileLink the file link
     */
    void onSuccess(String fileLink);

    /**
     * On error.
     *
     * @param errMsg the err msg
     */
    void onError(String errMsg);
}
