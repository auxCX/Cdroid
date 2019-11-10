package com.seafile.seadroid2.gallery;

import android.app.Activity;

/**
 * The type No search activity.
 */
public class NoSearchActivity extends Activity {
    @Override
    public boolean onSearchRequested() {
        return false;
    }
}
