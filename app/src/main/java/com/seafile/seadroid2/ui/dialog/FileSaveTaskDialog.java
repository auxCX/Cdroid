package com.seafile.seadroid2.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeafException;
import com.seafile.seadroid2.util.Utils;
import com.yydcdut.markdown.MarkdownEditText;

import java.io.File;
import java.io.IOException;

/**
 * The type File save task.
 */
class FileSaveTask extends TaskDialog.Task {
    /**
     * The Path.
     */
    String path;
    /**
     * The M markdown edit text.
     */
    MarkdownEditText mMarkdownEditText;

    /**
     * Instantiates a new File save task.
     *
     * @param path              the path
     * @param mMarkdownEditText the m markdown edit text
     */
    FileSaveTask(String path, MarkdownEditText mMarkdownEditText) {
        this.path = path;
        this.mMarkdownEditText = mMarkdownEditText;
    }

    @Override
    protected void runTask() {
        try {
            Utils.writeFile(new File(path), mMarkdownEditText.getText().toString());
        } catch (IOException e) {
            setTaskException(new SeafException(SeafException.OTHER_EXCEPTION, "File save failed"));
        }
    }
}

/**
 * The type File save task dialog.
 */
public class FileSaveTaskDialog extends TaskDialog {
    /**
     * The Path.
     */
    String path;
    /**
     * The M markdown edit text.
     */
    MarkdownEditText mMarkdownEditText;

    /**
     * Init.
     *
     * @param path              the path
     * @param mMarkdownEditText the m markdown edit text
     */
    public void init(String path, MarkdownEditText mMarkdownEditText) {
        this.path = path;
        this.mMarkdownEditText = mMarkdownEditText;
    }


    @Override
    protected View createDialogContentView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_file_save, null);
        return view;
    }


    @Override
    protected void onDialogCreated(Dialog dialog) {
        super.onDialogCreated(dialog);
        dialog.setTitle(getString(R.string.editor_file_save_title));
    }


    @Override
    protected FileSaveTask prepareTask() {
        FileSaveTask task = new FileSaveTask(path, mMarkdownEditText);
        return task;
    }
}

