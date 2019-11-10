package com.seafile.seadroid2.editor.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * The type Edit scroll view.
 */
public class EditScrollView extends ScrollView {
    /**
     * Instantiates a new Edit scroll view.
     *
     * @param context the context
     */
    public EditScrollView(Context context) {
        super(context);
    }

    /**
     * Instantiates a new Edit scroll view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public EditScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new Edit scroll view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public EditScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    private OnScrollChangedListener mOnScrollChangedListener;

    /**
     * Sets on scroll changed listener.
     *
     * @param onScrollChangedListener the on scroll changed listener
     */
    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        mOnScrollChangedListener = onScrollChangedListener;
    }

    /**
     * The interface On scroll changed listener.
     */
    public static interface OnScrollChangedListener {
        /**
         * On scroll changed.
         *
         * @param l    the l
         * @param t    the t
         * @param oldl the oldl
         * @param oldt the oldt
         */
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
