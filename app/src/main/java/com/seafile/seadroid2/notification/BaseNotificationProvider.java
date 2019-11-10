package com.seafile.seadroid2.notification;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeadroidApplication;
import com.seafile.seadroid2.transfer.TransferManager;
import com.seafile.seadroid2.transfer.TransferService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * All downloading events will be represented by one downloading notification, at the same time all
 * uploading events will be represented by one uploading notification as well.
 * maintain state of downloading or uploading events and update the relevant notification.
 */
public abstract class BaseNotificationProvider {

    /**
     * The M notif builder.
     */
    protected NotificationCompat.Builder mNotifBuilder;

    /**
     * The M notif mgr.
     */
    protected NotificationManager mNotifMgr = (NotificationManager) SeadroidApplication.getAppContext().
            getSystemService(SeadroidApplication.getAppContext().NOTIFICATION_SERVICE);

    /**
     * The constant NOTIFICATION_MESSAGE_KEY.
     */
    public static final String NOTIFICATION_MESSAGE_KEY = "notification message key";
    /**
     * Creates an explicit flag for opening @{link com.seafile.seadroid2.ui.fragment.DownloadTaskFragment}
     * in @{link com.seafile.seadroid2.ui.activity.TransferActivity}
     */
    public static final String NOTIFICATION_OPEN_DOWNLOAD_TAB = "open download tab notification";
    /**
     * Creates an explicit flag for opening @{link com.seafile.seadroid2.ui.fragment.UploadTaskFragment}
     * in @{link com.seafile.seadroid2.ui.activity.TransferActivity}
     */
    public static final String NOTIFICATION_OPEN_UPLOAD_TAB = "open upload tab notification";

    /**
     * The constant NOTIFICATION_ID_DOWNLOAD.
     */
    public static final int NOTIFICATION_ID_DOWNLOAD = 1;
    /**
     * The constant NOTIFICATION_ID_UPLOAD.
     */
    public static final int NOTIFICATION_ID_UPLOAD = 2;
    /**
     * The constant NOTIFICATION_ID_MEDIA.
     */
    public static final int NOTIFICATION_ID_MEDIA = 3;

    /**
     * The Tx mgr.
     */
    protected TransferManager txMgr;
    /**
     * The Tx service.
     */
    protected TransferService txService;

    /**
     * Instantiates a new Base notification provider.
     *
     * @param transferManager the transfer manager
     * @param transferService the transfer service
     */
    public BaseNotificationProvider(TransferManager transferManager,
                                    TransferService transferService) {
        this.txMgr = transferManager;
        this.txService = transferService;
    }

    /**
     * calculate state
     *
     * @return {@code NotificationState.NOTIFICATION_STATE_FAILED}, when at least one task failed        {@code NotificationState.NOTIFICATION_STATE_CANCELLED}, when at least one task cancelled        {@code NotificationState.NOTIFICATION_STATE_PROGRESS}, when at least one task in progress        {@code NotificationState.NOTIFICATION_STATE_COMPLETED}, otherwise.
     */
    protected abstract NotificationState getState();

    /**
     * get notification id
     *
     * @return notificationID notification id
     */
    protected abstract int getNotificationID();

    /**
     * get notification title texts
     *
     * @return some           descriptions shown in notification title
     */
    protected abstract String getNotificationTitle();

    /**
     * update notification
     */
    public void updateNotification() {
        if (mNotifBuilder == null)
            notifyStarted();

        String progressInfo = getProgressInfo();
        String notifTitle = getNotificationTitle();
        int notifId = getNotificationID();
        int progress = getProgress();

        if (getState().equals(NotificationState.NOTIFICATION_STATE_PROGRESS)) {
            notifyProgress(notifId, notifTitle, progressInfo, progress);
        } else if (getState().equals(NotificationState.NOTIFICATION_STATE_COMPLETED_WITH_ERRORS)) {
            notifyCompletedWithErrors(notifId, notifTitle, progressInfo, progress);
        } else if (getState().equals(NotificationState.NOTIFICATION_STATE_COMPLETED)) {
            notifyCompleted(notifId, notifTitle, progressInfo);
        }
    }

    /**
     * start to show a notification
     */
    protected abstract void notifyStarted();

    /**
     * update notification when downloading or uploading in progress
     *
     * @param notificationID
     *          use to update the notification later on
     * @param title
     *          some descriptions shown in notification title
     * @param info
     *          some descriptions to indicate the upload status
     * @param progress
     *          progress value to update build-in progressbar
     *
     */
    private void notifyProgress(int notificationID,
                                String title,
                                String info,
                                int progress) {
        mNotifBuilder.setSmallIcon(R.drawable.icon);
        mNotifBuilder.setContentTitle(title);
        mNotifBuilder.setContentText(info);
        mNotifBuilder.setProgress(100, progress, false);
        mNotifMgr.notify(notificationID, mNotifBuilder.build());
    }

    /**
     * update notification when completed
     *
     * @param notificationID
     *          use to update the notification later on
     * @param title
     *          some descriptions shown in notification title
     *
     */
    private void notifyCompleted(int notificationID, String title, String info) {
        mNotifBuilder.setSmallIcon(R.drawable.icon);
        mNotifBuilder.setContentTitle(title);
        mNotifBuilder.setContentText(info);
        mNotifBuilder.setProgress(100, 100, false);
        mNotifBuilder.setAutoCancel(true);
        mNotifBuilder.setOngoing(false);
        mNotifMgr.notify(notificationID, mNotifBuilder.build());
        mNotifBuilder = null;
        cancelWithDelay(txService, 5000);
    }

    /**
     * Delay for a while before cancel notification in order user can see the result
     *
     * @param transferService the transfer service
     * @param delayInMillis   the delay in millis
     */
    public static void cancelWithDelay(final TransferService transferService,
            long delayInMillis) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!transferService.isTransferring()) {
                    transferService.stopForeground(true);
                }
            }
        }, delayInMillis);

    }

    /**
     * update notification when failed or cancelled
     *
     * @param notificationID use to update the notification later on
     * @param title          some descriptions shown in notification title
     * @param info           some descriptions to indicate the upload status
     * @param progress       progress value to update build-in progressbar
     */
    protected void notifyCompletedWithErrors(int notificationID, String title, String info, int progress) {
        mNotifBuilder.setSmallIcon(R.drawable.icon);
        mNotifBuilder.setContentTitle(title);
        mNotifBuilder.setContentText(info);
        mNotifBuilder.setProgress(100, progress, false);
        mNotifBuilder.setAutoCancel(true);
        mNotifBuilder.setOngoing(false);
        mNotifMgr.notify(notificationID, mNotifBuilder.build());
        mNotifBuilder = null;
        cancelWithDelay(txService, 5000);
    }

    /**
     * get downloading or uploading status
     *
     * @return texts          of downloading or uploading status
     */
    protected abstract String getProgressInfo();

    /**
     * get progress of transferred files
     *
     * @return progress progress
     */
    protected abstract int getProgress();

    /**
     * Clear notification from notification area
     */
    public void cancelNotification() {
        mNotifMgr.cancelAll();
        mNotifBuilder = null;
        cancelWithDelay(txService, 2000);
    }

    /**
     * The enum Notification state.
     */
    public enum NotificationState {
        /**
         * Notification state progress notification state.
         */
        NOTIFICATION_STATE_PROGRESS,
        /**
         * Notification state completed notification state.
         */
        NOTIFICATION_STATE_COMPLETED,
        /**
         * Notification state completed with errors notification state.
         */
        NOTIFICATION_STATE_COMPLETED_WITH_ERRORS
    }

}
