package com.seafile.seadroid2.editor;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.seafile.seadroid2.R;

/**
 * The type Link dialog view.
 */
public class LinkDialogView extends LinearLayout {
    private EditText mDescriptionEditText;
    private EditText mLinkEditText;

    /**
     * Instantiates a new Link dialog view.
     *
     * @param context the context
     */
    public LinkDialogView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Instantiates a new Link dialog view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public LinkDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Instantiates a new Link dialog view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public LinkDialogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_link, this, true);
        mDescriptionEditText = (EditText) v.findViewById(R.id.edit_description_link);
        mLinkEditText = (EditText) v.findViewById(R.id.edit_link);
    }

    /**
     * Clear.
     */
    public void clear() {
        mDescriptionEditText.setText("");
        mLinkEditText.setText("http://");
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return mDescriptionEditText.getText().toString();
    }

    /**
     * Gets link.
     *
     * @return the link
     */
    public String getLink() {
        return mLinkEditText.getText().toString();
    }


}
