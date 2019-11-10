package com.seafile.seadroid2.monitor;

import com.seafile.seadroid2.account.Account;
import com.seafile.seadroid2.data.SeafCachedFile;

import java.io.File;

/**
 * The interface Cached file changed listener.
 */
interface CachedFileChangedListener {
    /**
     * On cached blocks changed.
     *
     * @param account the account
     * @param cf      the cf
     * @param file    the file
     */
    void onCachedBlocksChanged(Account account, SeafCachedFile cf, File file);

    /**
     * On cached file changed.
     *
     * @param account the account
     * @param cf      the cf
     * @param file    the file
     */
    void onCachedFileChanged(Account account, SeafCachedFile cf, File file);
}

