package com.seafile.seadroid2.ui.dialog;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seafile.seadroid2.util.ConcurrentAsyncTask;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeafException;


/**
 * Basic class for dialog which get input from user and carries out some
 * operation in the background.
 */
public abstract class TaskDialog extends DialogFragment {
    /**
     * The type Task dialog listener.
     */
    public static abstract class TaskDialogListener {
        /**
         * On task success.
         */
        public void onTaskSuccess() {
        }

        /**
         * On task failed.
         *
         * @param e the e
         */
        public void onTaskFailed(SeafException e) {
        }

        /**
         * On task cancelled.
         */
        public void onTaskCancelled() {
        }
    }

    private static final String DEBUG_TAG = "TaskDialog";
    private static final String STATE_ERROR_TEXT = "task_dialog.error_text";
    private static final String TASK_STATE_SAVED = "task_dialog.task_saved";

    // The AsyncTask instance
    private Task task;

    // The error message
    private TextView errorText;

    // The spinning wheel to show loading
    private ProgressBar loading;
    private ProgressBar loadingProgress;

    //
    private Button okButton;
    private Button cancelButton;

    // The content area of the dialog
    private View contentView;

    private TaskDialogListener mListener;

    /**
     * The Is progress horizontal.
     */
    protected boolean isProgressHorizontal = false;

    /**
     * Create the content area of the dialog
     *
     * @param inflater           the inflater
     * @param savedInstanceState The saved dialog state. Most of the time subclasses don't need to make use of it, since the state of UI widgets is restored by the base class.
     * @return The created view
     */
    protected abstract View createDialogContentView(LayoutInflater inflater,
                                                    Bundle savedInstanceState);

    /**
     * Create the AsyncTask
     *
     * @return the task
     */
    protected abstract Task prepareTask();

    /**
     * Return the content area view of the dialog
     *
     * @return the content view
     */
    protected View getContentView() {
        return contentView;
    }


    /**
     * If true, execute the task without clicking the OK btn;
     *
     * @return the boolean
     */
    protected boolean executeTaskImmediately() {
        return false;
    }

    /**
     * This hook method is called right after the dialog is built.
     *
     * @param dialog the dialog
     * @prarm dialog
     */
    protected void onDialogCreated(Dialog dialog) {
    }

    /**
     * Save the content you are interested
     *
     * @param outState the out state
     */
    protected void onSaveDialogContentState(Bundle outState) {
    }

    /**
     * Gets task.
     *
     * @return the task
     */
    protected Task getTask() {
        return task;
    }

    @Override
    public void onStop() {
        Log.d(DEBUG_TAG, "onStop");
        super.onStop();

        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            Log.d(DEBUG_TAG, "cancel the task");
            task.cancel(true);
        }

    }

    /**
     * Save the state of the background task so that we can restore the task
     * when recreating this dialog. For example, when screen rotation
     *
     * @param outState the out state
     */
    protected void onSaveTaskState(Bundle outState) {
    }

    /**
     * Recreate the background task when this dialog is recreated.
     *
     * @param savedInstanceState the saved instance state
     * @return The background task if it should be restored. Or null to indicate that you don't want to recreate the task.
     */
    protected Task onRestoreTaskState(Bundle savedInstanceState) {
        return null;
    }

    /**
     * Check if the user input is valid. It is called when the "OK" button is
     * clicked.
     *
     * @throws Exception with the error message if there is error in user input
     */
    protected void onValidateUserInput() throws Exception {
    }

    /**
     * On task success.
     */
    public void onTaskSuccess() {
        getDialog().dismiss();
        if (mListener != null) {
            mListener.onTaskSuccess();
        }
    }

    /**
     * Get a readable message to display from an exception. Subclasses should
     * override this method if needed.
     *
     * @param e the e
     * @return the error from exception
     */
    protected String getErrorFromException(SeafException e) {
        return e.getMessage();
    }

    /**
     * On task failed.
     *
     * @param e the e
     */
    public void onTaskFailed(SeafException e) {
        hideLoading();
        hideLoadingPro();
        showError(getErrorFromException(e));
        enableInput();
        if (mListener != null) {
            mListener.onTaskFailed(e);
        }
    }

    /**
     * Sets task dialog lisenter.
     *
     * @param listener the listener
     */
    public void setTaskDialogLisenter(TaskDialogListener listener) {
        mListener = listener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        onSaveDialogContentState(outState);
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            outState.putBoolean(TASK_STATE_SAVED, true);
            onSaveTaskState(outState);
            task.cancel(true);
        }

        outState.putString(STATE_ERROR_TEXT, errorText.getText().toString());

        super.onSaveInstanceState(outState);
    }
    /** optional dialog title layout */
    // private TextView mTitle;
    /** optional alert dialog image */
    // private ImageView mIcon;
    /** optional message displayed below title if title exists*/
    // private TextView mMessage;
    /** The colored holo divider. You can set its color with the setDividerColor method */
    // private View mDivider;
    /** optional custom panel image */
    private FrameLayout mCustom;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout view = (LinearLayout)inflater.inflate(R.layout.seafile_dialog_layout, null);
        mCustom = (FrameLayout) view.findViewById(R.id.customPanel);
        contentView = createDialogContentView(inflater, savedInstanceState);
        if (contentView != null) {
            mCustom.setVisibility(View.VISIBLE);
            mCustom.addView(contentView, 0);
        }

        errorText = (TextView) view.findViewById(R.id.error_message);
        loading = (ProgressBar) view.findViewById(R.id.loading);
        loadingProgress = (ProgressBar) view.findViewById(R.id.loading_horizontal);

        if (savedInstanceState != null) {
            String error = savedInstanceState.getString(STATE_ERROR_TEXT);
            if (error != null && error.length() > 0) {
                errorText.setText(error);
                errorText.setVisibility(View.VISIBLE);
            }
        }

        builder.setView(view);

        if (hasOkButton()) {
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
                    task.cancel(true);
                }
                if (mListener != null) {
                    mListener.onTaskCancelled();
                }
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                if (hasOkButton()) {
                    okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    //okButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.dialog_btn_txt_size));
                    cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    //cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.dialog_btn_txt_size));
                    View.OnClickListener onOKButtonClickedListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                onValidateUserInput();
                            } catch (Exception e) {
                                showError(e.getMessage());
                                return;
                            }

                            task = prepareTask();
                            executeTask();
                        }
                    };
                    okButton.setOnClickListener(onOKButtonClickedListener);
                }

                if (savedInstanceState != null) {
                    dialog.onRestoreInstanceState(savedInstanceState);
                    restoreTask(savedInstanceState);
                }

                if (executeTaskImmediately()) {
                    task = prepareTask();
                    executeTask();
                }
            }
        });

        onDialogCreated(dialog);

        return dialog;
    }

    private void restoreTask(Bundle savedInstanceState) {
        // Restore the AsyncTask and execute it
        boolean taskSaved = savedInstanceState.getBoolean(TASK_STATE_SAVED);
        if (!taskSaved) {
            return;
        }

        task = onRestoreTaskState(savedInstanceState);
        if (task != null) {
            executeTask();
        }
    }

    /**
     * Show loading.
     */
    protected void showLoading() {
        loading.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), android.R.anim.fade_in));
        loading.setVisibility(View.VISIBLE);
    }

    /**
     * Show loading pro.
     */
    protected void showLoadingPro() {
        loadingProgress.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
        loadingProgress.setVisibility(View.VISIBLE);
    }


    /**
     * Sets progress.
     *
     * @param progress the progress
     */
    protected void setProgress(int progress) {
        if (loadingProgress != null) {
            loadingProgress.setProgress(progress);
        }
    }

    /**
     * Hide loading.
     */
    protected void hideLoading() {
        loading.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), android.R.anim.fade_out));
        loading.setVisibility(View.INVISIBLE);
    }

    /**
     * Hide loading pro.
     */
    protected void hideLoadingPro() {
        loadingProgress.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
        loadingProgress.setVisibility(View.INVISIBLE);
    }

    /**
     * Show error.
     *
     * @param error the error
     */
    protected void showError(String error) {
        errorText.setText(error);
        errorText.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), android.R.anim.fade_in));
        errorText.setVisibility(View.VISIBLE);
    }

    /**
     * Hide error.
     */
    protected void hideError() {
        errorText.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), android.R.anim.fade_out));
        errorText.setVisibility(View.GONE);
    }

    /**
     * Error is visible boolean.
     *
     * @return the boolean
     */
    protected boolean errorIsVisible() {
       return errorText.getVisibility() == View.VISIBLE;
    }

    private boolean hasOkButton() {
        return !executeTaskImmediately();
    }

    /**
     * Disable input.
     */
    protected void disableInput() {
        if (hasOkButton()) {
            okButton.setEnabled(false);
        }
    }

    /**
     * Disable cancel.
     */
    protected void disableCancel() {
        cancelButton.setEnabled(false);
    }

    /**
     * Enable input.
     */
    protected void enableInput() {
        if (hasOkButton()) {
            okButton.setEnabled(true);
        }
    }

    private void executeTask() {
        disableInput();
        if (errorIsVisible()){
            hideError();
        }
        if (isProgressHorizontal) {
            showLoadingPro();
        } else {
            showLoading();
        }
        task.setTaskDialog(this);
        ConcurrentAsyncTask.execute(task);
    }

    /**
     * The type Task.
     */
    public static abstract class Task extends AsyncTask<Void, Long, Void> {
        private SeafException err;
        private TaskDialog dlg;

        /**
         * Carries out the background task.
         */
        protected abstract void runTask();

        /**
         * Progress.
         *
         * @param progress the progress
         */
        public void progress(int progress) {
            this.dlg.setProgress(progress);
        }

        /**
         * Sets task dialog.
         *
         * @param dlg the dlg
         */
        public void setTaskDialog(TaskDialog dlg) {
            this.dlg = dlg;
        }

        /**
         * Subclass should call this method to set the exception
         *
         * @param e The exception raised during {@link #runTask()}
         */
        protected void setTaskException(SeafException e) {
            err = e;
        }

        /**
         * Gets task exception.
         *
         * @return the task exception
         */
        public SeafException getTaskException() {
            return err;
        }

        @Override
        public Void doInBackground(Void... params) {
            runTask();
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            if (err != null) {
                dlg.onTaskFailed(err);
            } else {
                dlg.onTaskSuccess();
            }
        }
    }
}
