package com.seafile.seadroid2.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.LazySodiumAndroid;
import com.goterl.lazycode.lazysodium.SodiumAndroid;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeafException;
import com.seafile.seadroid2.account.Account;
import com.seafile.seadroid2.crypto.Crypto;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.data.SeafRepoEncrypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import android.util.Log;

/**
 * The type Set password task.
 */
class SetPasswordTask extends TaskDialog.Task {
    /**
     * The constant DEBUG_TAG.
     */
    public static final String DEBUG_TAG = SetPasswordTask.class.getSimpleName();

    /**
     * The Repo id.
     */
    String repoID;
    /**
     * The Password.
     */
    String password;
    /**
     * The Data manager.
     */
    DataManager dataManager;

    /**
     * Instantiates a new Set password task.
     *
     * @param repoID      the repo id
     * @param password    the password
     * @param dataManager the data manager
     */
    public SetPasswordTask(String repoID, String password,
                           DataManager dataManager) {
        this.repoID = repoID;
        this.password = password;
        this.dataManager = dataManager;
    }

    @Override
    protected void runTask() {
        SeafRepoEncrypt repo = dataManager.getCachedRepoEncryptByID(repoID);
        System.out.println("HELLO");
        try {
            if (repo == null || !repo.canLocalDecrypt()) {
                dataManager.setPassword(repoID, password);
            } else {
                Crypto.verifyRepoPassword(repoID, password, repo.encVersion, repo.magic);
            }
        } catch (SeafException e) {
            setTaskException(e);
        }
    }
}

/**
 * The type Password dialog.
 */
public class PasswordDialog extends TaskDialog {
    /**
     * The constant DEBUG_TAG.
     */
    public static final String DEBUG_TAG = PasswordDialog.class.getCanonicalName();

    private static final String STATE_TASK_REPO_NAME = "set_password_task.repo_name";
    private static final String STATE_TASK_REPO_ID = "set_password_task.repo_id";
    private static final String STATE_TASK_PASSWORD = "set_password_task.password";
    private static final String STATE_ACCOUNT = "set_password_task.account";

    private EditText passwordText;
    private String repoID, repoName;
    private DataManager dataManager;
    private Account account;
    private String password;

    /**
     * Sets repo.
     *
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param account  the account
     */
    public void setRepo(String repoName, String repoID, Account account) {
        this.repoName = repoName;
        this.repoID = repoID;
        this.account = account;
    }

    private DataManager getDataManager() {
        if (dataManager == null) {
            dataManager = new DataManager(account);
        }

        return dataManager;
    }

    @Override
    protected View createDialogContentView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_password, null);
        passwordText = (EditText) view.findViewById(R.id.password);
        if (savedInstanceState != null) {
            repoName = savedInstanceState.getString(STATE_TASK_REPO_NAME);
            repoID = savedInstanceState.getString(STATE_TASK_REPO_ID);
            account = (Account)savedInstanceState.getParcelable(STATE_ACCOUNT);
        }

        if (password != null) {
            passwordText.setText(password);
        }

        return view;
    }

    @Override
    protected void onDialogCreated(Dialog dialog) {
        dialog.setTitle(repoName);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    protected void onSaveDialogContentState(Bundle outState) {
        outState.putString(STATE_TASK_REPO_NAME, repoName);
        outState.putString(STATE_TASK_REPO_ID, repoID);
        outState.putParcelable(STATE_ACCOUNT, account);
    }

    @Override
    protected void onValidateUserInput() throws Exception {
        String password = passwordText.getText().toString().trim();

        if (password.length() == 0) {
            String err = getActivity().getResources().getString(R.string.password_empty);
            throw new Exception(err);
        }
    }

    @Override
    protected void disableInput() {
        super.disableInput();
        passwordText.setEnabled(false);
    }

    @Override
    protected void enableInput() {
        super.enableInput();
        passwordText.setEnabled(true);
    }

    @Override
    protected SetPasswordTask prepareTask() {
        String password = passwordText.getText().toString().trim();
        SetPasswordTask task = new SetPasswordTask(repoID, password, getDataManager());
        return task;
    }

    @Override
    protected void onSaveTaskState(Bundle outState) {
        SetPasswordTask task = (SetPasswordTask)getTask();
        if (task != null) {
            outState.putString(STATE_TASK_PASSWORD, task.password);
        }
    }

    @Override
    protected SetPasswordTask onRestoreTaskState(Bundle outState) {
        if (outState == null) {
            return null;
        }

        String password = outState.getString(STATE_TASK_PASSWORD);
        if (password != null) {
            return new SetPasswordTask(repoID, password, getDataManager());
        } else {
            return null;
        }
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected boolean executeTaskImmediately() {
        return password != null;
    }


    @Override
    public void onTaskSuccess() {
        SeafRepoEncrypt repo = dataManager.getCachedRepoEncryptByID(repoID);
        String password = passwordText.getText().toString().trim();
        if (repo == null || !repo.canLocalDecrypt()) {
            dataManager.setRepoPasswordSet(repoID, password);
        } else {
            if (TextUtils.isEmpty(repo.magic))
                return;

            try {
                LazySodium lazySodium = new LazySodiumAndroid(new SodiumAndroid());
                byte[] repo_id = lazySodium.sodiumHex2Bin(repo.id.replace("-",""));
                final Pair<String, String> pair = Crypto.generateKey(password + repo.encKey, repo_id, repo.encVersion);
                dataManager.setRepoPasswordSet(repoID, pair.first, pair.second);
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                // TODO notify error
                e.printStackTrace();
            }
        }
        super.onTaskSuccess();
    }

    @Override
    protected String getErrorFromException(SeafException e) {
        if (e.getCode() == 400 || e.getCode() == SeafException.invalidPassword.getCode()) {
            return getString(R.string.wrong_password);
        }
        return e.getMessage();
    }
}
