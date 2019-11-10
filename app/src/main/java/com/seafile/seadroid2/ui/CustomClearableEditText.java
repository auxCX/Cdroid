package com.seafile.seadroid2.ui;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import com.seafile.seadroid2.R;

/**
 * clearable EditText, also supports auto complete text typing.<br/>
 * if want to use auto complete feature, should set data source to it.
 */
public class CustomClearableEditText extends RelativeLayout {

    /**
     * The constant INPUT_TYPE_PASSWORD.
     */
    public static final String INPUT_TYPE_PASSWORD = "password";
    /**
     * The constant INPUT_TYPE_EMAIL.
     */
    public static final String INPUT_TYPE_EMAIL = "email";

    /**
     * The Inflater.
     */
    LayoutInflater inflater = null;
    /**
     * The Edit text.
     */
    AutoCompleteTextView edit_text;
    /**
     * The Btn clear.
     */
    Button btn_clear;

    /**
     * Instantiates a new Custom clearable edit text.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    public CustomClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    /**
     * Instantiates a new Custom clearable edit text.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public CustomClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();

    }

    /**
     * Instantiates a new Custom clearable edit text.
     *
     * @param context the context
     */
    public CustomClearableEditText(Context context) {
        super(context);
        initViews();
    }

    /**
     * Init views.
     */
    void initViews() {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.clearable_edit_text, this, true);
        edit_text = (AutoCompleteTextView) findViewById(R.id.clearable_edit);
        btn_clear = (Button) findViewById(R.id.clearable_button_clear);
        btn_clear.setVisibility(RelativeLayout.INVISIBLE);
        clearText();
        showHideClearButton();
    }

    /**
     * Clear text.
     */
    void clearText() {
        btn_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                edit_text.setText("");
            }
        });
    }

    /**
     * Show hide clear button.
     */
    void showHideClearButton() {
        edit_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    btn_clear.setVisibility(RelativeLayout.VISIBLE);
                else
                    btn_clear.setVisibility(RelativeLayout.INVISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * Gets text.
     *
     * @return the text
     */
    public Editable getText() {
        Editable text = edit_text.getText();
        return text;
    }

    /**
     * Sets email address auto complete adapter.
     *
     * @param adapter the adapter
     */
    public void setEmailAddressAutoCompleteAdapter(ArrayAdapter<String> adapter) {
        if (adapter != null)
            edit_text.setAdapter(adapter);
    }

    /**
     * Sets text.
     *
     * @param text the text
     */
    public void setText(String text) {
        edit_text.setText(text);
    }

    /**
     * Sets error.
     *
     * @param errorMessage the error message
     */
    public void setError(String errorMessage) {
        edit_text.setError(errorMessage);
    }

    /**
     * Sets display hint text.
     *
     * @param text the text
     */
    public void setDisplayHintText(String text) {
        edit_text.setHint(text);
    }

    /**
     * Sets input type.
     *
     * @param type the type
     */
    public void setInputType(String type) {
        if (type.equals(INPUT_TYPE_EMAIL)) {
            edit_text.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            edit_text.setHint(R.string.email_hint);
        } else if (type.equals(INPUT_TYPE_PASSWORD)) {
            edit_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edit_text.setTransformationMethod(PasswordTransformationMethod.getInstance());
            edit_text.setHint(R.string.passwd_hint);
        }
    }

    /**
     * Gets selection start.
     *
     * @return the selection start
     */
    public int getSelectionStart() {
        return edit_text.getSelectionStart();
    }

    /**
     * Sets selection.
     *
     * @param offset  the offset
     * @param offset1 the offset 1
     */
    public void setSelection(int offset, int offset1) {
        edit_text.setSelection(offset, offset1);
    }
}