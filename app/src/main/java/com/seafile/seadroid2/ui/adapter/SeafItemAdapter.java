package com.seafile.seadroid2.ui.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeadroidApplication;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.data.SeafCachedFile;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.data.SeafGroup;
import com.seafile.seadroid2.data.SeafItem;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.transfer.DownloadTaskInfo;
import com.seafile.seadroid2.ui.AnimateFirstDisplayListener;
import com.seafile.seadroid2.ui.NavContext;
import com.seafile.seadroid2.ui.activity.BrowserActivity;
import com.seafile.seadroid2.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Seaf item adapter.
 */
public class SeafItemAdapter extends BaseAdapter {

    private ArrayList<SeafItem> items;
    private BrowserActivity mActivity;
    private boolean repoIsEncrypted;
    private boolean actionModeOn;

    private SparseBooleanArray mSelectedItemsIds;
    private List<Integer> mSelectedItemsPositions = Lists.newArrayList();
    private List<SeafDirent> mSelectedItemsValues = Lists.newArrayList();

    /**
     * DownloadTask instance container
     **/
    private List<DownloadTaskInfo> mDownloadTaskInfos;

    /**
     * Instantiates a new Seaf item adapter.
     *
     * @param activity the activity
     */
    public SeafItemAdapter(BrowserActivity activity) {
        mActivity = activity;
        items = Lists.newArrayList();
        mSelectedItemsIds = new SparseBooleanArray();
    }

    /**
     * sort files type
     */
    public static final int SORT_BY_NAME = 9;
    /**
     * sort files type
     */
    public static final int SORT_BY_LAST_MODIFIED_TIME = 10;
    /**
     * sort files order
     */
    public static final int SORT_ORDER_ASCENDING = 11;
    /**
     * sort files order
     */
    public static final int SORT_ORDER_DESCENDING = 12;

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * To refresh downloading status of {@link com.seafile.seadroid2.ui.fragment.ReposFragment#mListView},
     * use this method to update data set.
     * <p>
     * This method should be called after the "Download folder" menu was clicked.
     *
     * @param newList the new list
     */
    public void setDownloadTaskList(List<DownloadTaskInfo> newList) {
        if (!equalLists(newList, mDownloadTaskInfos)) {
            this.mDownloadTaskInfos = newList;
            // redraw the list
            notifyDataSetChanged();
        }
    }

    /**
     * Compare two lists
     *
     * @param newList
     * @param oldList
     * @return true if the two lists are equal,
     * false, otherwise.
     */
    private boolean equalLists(List<DownloadTaskInfo> newList, List<DownloadTaskInfo> oldList) {
        if (newList == null && oldList == null)
            return true;

        if ((newList == null && oldList != null)
                || newList != null && oldList == null
                || newList.size() != oldList.size())
            return false;

        return newList.equals(oldList);
    }

    /**
     * Add entry.
     *
     * @param entry the entry
     */
    public void addEntry(SeafItem entry) {
        items.add(entry);
        // Collections.sort(items);
        notifyDataSetChanged();
    }

    /**
     * Add.
     *
     * @param entry the entry
     */
    public void add(SeafItem entry) {
        items.add(entry);
    }

    /**
     * Notify changed.
     */
    public void notifyChanged() {
        notifyDataSetChanged();
    }

    @Override
    public SeafItem getItem(int position) {
        return items.get(position);
    }

    /**
     * Sets items.
     *
     * @param dirents the dirents
     */
    public void setItems(List<SeafDirent> dirents) {
        items.clear();
        items.addAll(dirents);
        this.mSelectedItemsIds.clear();
        this.mSelectedItemsPositions.clear();
        this.mSelectedItemsValues.clear();
    }

    /**
     * Deselect all items.
     */
    public void deselectAllItems() {
        mSelectedItemsIds.clear();
        mSelectedItemsPositions.clear();
        mSelectedItemsValues.clear();
        notifyDataSetChanged();
    }

    /**
     * Select all items.
     */
    public void selectAllItems() {
        mSelectedItemsIds.clear();
        mSelectedItemsPositions.clear();
        mSelectedItemsValues.clear();
        for (int i = 0; i < items.size(); i++) {
            mSelectedItemsIds.put(i, true);
            mSelectedItemsPositions.add(i);
            mSelectedItemsValues.add((SeafDirent) items.get(i));
        }
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Clear.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Are all items selectable boolean.
     *
     * @return the boolean
     */
    public boolean areAllItemsSelectable() {
        return false;
    }

    /**
     * Is enable boolean.
     *
     * @param position the position
     * @return the boolean
     */
    public boolean isEnable(int position) {
        SeafItem item = items.get(position);
        return !(item instanceof SeafGroup);
    }

    /**
     * Is clickable boolean.
     *
     * @param position the position
     * @return the boolean
     */
    public boolean isClickable(int position) {
        SeafItem item = items.get(position);
        return !(item instanceof SeafGroup);
    }

    public int getViewTypeCount() {
        return 2;
    }

    public int getItemViewType(int position) {
        SeafItem item = items.get(position);
        if (item instanceof SeafGroup)
            return 0;
        else
            return 1;
    }

    private View getRepoView(final SeafRepo repo, View convertView, ViewGroup parent) {
        View view = convertView;
        Viewholder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_entry, null);
            TextView title = (TextView) view.findViewById(R.id.list_item_title);
            TextView subtitle = (TextView) view.findViewById(R.id.list_item_subtitle);
            ImageView multiSelect = (ImageView) view.findViewById(R.id.list_item_multi_select_btn);
            ImageView icon = (ImageView) view.findViewById(R.id.list_item_icon);
            RelativeLayout action = (RelativeLayout) view.findViewById(R.id.expandable_toggle_button);
            ImageView downloadStatusIcon = (ImageView) view.findViewById(R.id.list_item_download_status_icon);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.list_item_download_status_progressbar);
            viewHolder = new Viewholder(title, subtitle, multiSelect, icon, action, downloadStatusIcon, progressBar);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Viewholder) convertView.getTag();
        }

        viewHolder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.showRepoBottomSheet(repo);
            }
        });

        viewHolder.multiSelect.setVisibility(View.GONE);
        viewHolder.downloadStatusIcon.setVisibility(View.GONE);
        viewHolder.progressBar.setVisibility(View.GONE);
        viewHolder.title.setText(repo.getTitle());
        viewHolder.subtitle.setText(repo.getSubtitle());
        viewHolder.icon.setImageResource(repo.getIcon());
        if (repo.hasWritePermission()) {
            viewHolder.action.setVisibility(View.VISIBLE);
        } else {
            viewHolder.action.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    private View getGroupView(SeafGroup group) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.group_item, null);
        TextView tv = (TextView) view.findViewById(R.id.textview_groupname);
        String groupTitle = group.getTitle();
        if ("Organization".equals(groupTitle)) {
            groupTitle = mActivity.getString(R.string.shared_with_all);
        }
        tv.setText(groupTitle);
        return view;
    }

    private View getDirentView(final SeafDirent dirent, View convertView, ViewGroup parent, final int position) {
        View view = convertView;
        final Viewholder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_entry, null);
            TextView title = (TextView) view.findViewById(R.id.list_item_title);
            TextView subtitle = (TextView) view.findViewById(R.id.list_item_subtitle);
            ImageView icon = (ImageView) view.findViewById(R.id.list_item_icon);
            ImageView multiSelect = (ImageView) view.findViewById(R.id.list_item_multi_select_btn);
            RelativeLayout action = (RelativeLayout) view.findViewById(R.id.expandable_toggle_button);
            ImageView downloadStatusIcon = (ImageView) view.findViewById(R.id.list_item_download_status_icon);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.list_item_download_status_progressbar);
            viewHolder = new Viewholder(title, subtitle, multiSelect, icon, action, downloadStatusIcon, progressBar);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Viewholder) convertView.getTag();
        }

        viewHolder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dirent.isDir())
                    mActivity.showDirBottomSheet(dirent.getTitle(), (SeafDirent) getItem(position));
                else
                    mActivity.showFileBottomSheet(dirent.getTitle(), (SeafDirent) getItem(position));
            }
        });

        if (actionModeOn) {
            viewHolder.multiSelect.setVisibility(View.VISIBLE);
            if (mSelectedItemsIds.get(position)) {
                viewHolder.multiSelect.setImageResource(R.drawable.multi_select_item_checked);
            } else
                viewHolder.multiSelect.setImageResource(R.drawable.multi_select_item_unchecked);

            viewHolder.multiSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mSelectedItemsIds.get(position)) {
                        viewHolder.multiSelect.setImageResource(R.drawable.multi_select_item_checked);
                        mSelectedItemsIds.put(position, true);
                        mSelectedItemsPositions.add(position);
                        mSelectedItemsValues.add(dirent);
                    } else {
                        viewHolder.multiSelect.setImageResource(R.drawable.multi_select_item_unchecked);
                        mSelectedItemsIds.delete(position);
                        mSelectedItemsPositions.remove(Integer.valueOf(position));
                        mSelectedItemsValues.remove(dirent);
                    }

                    mActivity.onItemSelected();
                }
            });
        } else
            viewHolder.multiSelect.setVisibility(View.GONE);

        viewHolder.title.setText(dirent.getTitle());
        if (dirent.isDir()) {
            viewHolder.downloadStatusIcon.setVisibility(View.GONE);
            viewHolder.progressBar.setVisibility(View.GONE);

            viewHolder.subtitle.setText(dirent.getSubtitle());

            if (repoIsEncrypted) {
                viewHolder.action.setVisibility(View.GONE);
            } else
                viewHolder.action.setVisibility(View.VISIBLE);

            viewHolder.icon.setImageResource(dirent.getIcon());
        } else {
            viewHolder.downloadStatusIcon.setVisibility(View.GONE);
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.action.setVisibility(View.VISIBLE);
            setFileView(dirent, viewHolder, position);
        }

        return view;
    }

    /**
     * use to refresh view of {@link com.seafile.seadroid2.ui.fragment.ReposFragment #mPullRefreshListView}
     * <p>
     * <h5>when to show download status icons</h5>
     * if the dirent is a file and already cached, show cached icon.</br>
     * if the dirent is a file and waiting to download, show downloading icon.</br>
     * if the dirent is a file and is downloading, show indeterminate progressbar.</br>
     * ignore directories and repos.</br>
     *
     * @param dirent
     * @param viewHolder
     * @param position
     */
    private void setFileView(SeafDirent dirent, Viewholder viewHolder, int position) {
        NavContext nav = mActivity.getNavContext();
        DataManager dataManager = mActivity.getDataManager();
        String repoName = nav.getRepoName();
        String repoID = nav.getRepoID();
        String filePath = Utils.pathJoin(nav.getDirPath(), dirent.name);
        if (repoName == null || repoID == null)
            return;

        File file = null;
        try {
            file = dataManager.getLocalRepoFile(repoName, repoID, filePath);
        } catch (RuntimeException e) {
            mActivity.showShortToast(mActivity, mActivity.getResources().getString(R.string.storage_space_insufficient));
            e.printStackTrace();
            return;
        }
        boolean cacheExists = false;

        if (file.exists()) {
            SeafCachedFile cf = dataManager.getCachedFile(repoName, repoID, filePath);
            String subtitle = null;
            subtitle = dirent.getSubtitle();
            if (cf != null) {
                cacheExists = true;
            }
            // show file download finished
            viewHolder.downloadStatusIcon.setVisibility(View.VISIBLE);
            viewHolder.downloadStatusIcon.setImageResource(R.drawable.list_item_download_finished);
            viewHolder.subtitle.setText(subtitle);
            viewHolder.progressBar.setVisibility(View.GONE);

        } else {
            int downloadStatusIcon = R.drawable.list_item_download_waiting;
            if (mDownloadTaskInfos != null) {
                for (DownloadTaskInfo downloadTaskInfo : mDownloadTaskInfos) {
                    // use repoID and path to identify the task
                    if (downloadTaskInfo.repoID.equals(repoID)
                            && downloadTaskInfo.pathInRepo.equals(filePath)) {
                        switch (downloadTaskInfo.state) {
                            case INIT:
                            case FAILED:
                                downloadStatusIcon = R.drawable.list_item_download_waiting;
                                viewHolder.downloadStatusIcon.setVisibility(View.VISIBLE);
                                viewHolder.progressBar.setVisibility(View.GONE);
                                break;
                            case CANCELLED:
                                viewHolder.downloadStatusIcon.setVisibility(View.GONE);
                                viewHolder.progressBar.setVisibility(View.GONE);
                                break;
                            case TRANSFERRING:
                                viewHolder.downloadStatusIcon.setVisibility(View.GONE);
                                viewHolder.progressBar.setVisibility(View.VISIBLE);
                                break;
                            case FINISHED:
                                downloadStatusIcon = R.drawable.list_item_download_finished;
                                viewHolder.downloadStatusIcon.setVisibility(View.VISIBLE);
                                viewHolder.progressBar.setVisibility(View.GONE);
                                break;
                            default:
                                downloadStatusIcon = R.drawable.list_item_download_waiting;
                                break;
                        }
                    }
                }
            } else {
                viewHolder.downloadStatusIcon.setVisibility(View.GONE);
                viewHolder.progressBar.setVisibility(View.GONE);
            }

            viewHolder.downloadStatusIcon.setImageResource(downloadStatusIcon);
            viewHolder.subtitle.setText(dirent.getSubtitle());
        }

        if (Utils.isViewableImage(file.getName())) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .extraForDownloader(dataManager.getAccount())
                    .delayBeforeLoading(500)
                    .resetViewBeforeLoading(true)
                    .showImageOnLoading(R.drawable.file_image)
                    .showImageForEmptyUri(R.drawable.file_image)
                    .showImageOnFail(R.drawable.file_image)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
            String url = dataManager.getThumbnailLink(repoName, repoID, filePath, getThumbnailWidth());
            if (url == null) {
                viewHolder.icon.setImageResource(dirent.getIcon());
            } else {
                ImageLoader.getInstance().displayImage(url, viewHolder.icon, options, animateFirstListener);
            }
        } else {
            viewHolder.icon.setImageResource(dirent.getIcon());
        }

    }

    private View getCacheView(SeafCachedFile item, View convertView, ViewGroup parent) {
        View view = convertView;
        Viewholder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_entry, null);
            TextView title = (TextView) view.findViewById(R.id.list_item_title);
            TextView subtitle = (TextView) view.findViewById(R.id.list_item_subtitle);
            ImageView multiSelect = (ImageView) view.findViewById(R.id.list_item_multi_select_btn);
            ImageView icon = (ImageView) view.findViewById(R.id.list_item_icon);
            RelativeLayout action = (RelativeLayout) view.findViewById(R.id.expandable_toggle_button);
            ImageView downloadStatusIcon = (ImageView) view.findViewById(R.id.list_item_download_status_icon);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.list_item_download_status_progressbar);
            viewHolder = new Viewholder(title, subtitle, multiSelect, icon, action, downloadStatusIcon, progressBar);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Viewholder) convertView.getTag();
        }

        viewHolder.downloadStatusIcon.setVisibility(View.VISIBLE);
        viewHolder.downloadStatusIcon.setImageResource(R.drawable.list_item_download_finished);
        viewHolder.progressBar.setVisibility(View.GONE);
        viewHolder.title.setText(item.getTitle());
        viewHolder.subtitle.setText(item.getSubtitle());
        viewHolder.icon.setImageResource(item.getIcon());
        viewHolder.action.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SeafItem item = items.get(position);
        if (item instanceof SeafRepo) {
            return getRepoView((SeafRepo) item, convertView, parent);
        } else if (item instanceof SeafGroup) {
            return getGroupView((SeafGroup) item);
        } else if (item instanceof SeafCachedFile) {
            return getCacheView((SeafCachedFile) item, convertView, parent);
        } else {
            return getDirentView((SeafDirent) item, convertView, parent, position);
        }
    }

    /**
     * Sets action mode on.
     *
     * @param actionModeOn the action mode on
     */
    public void setActionModeOn(boolean actionModeOn) {
        this.actionModeOn = actionModeOn;
    }

    /**
     * Toggle selection.
     *
     * @param position the position
     */
    public void toggleSelection(int position) {
        if (mSelectedItemsIds.get(position)) {
            // unselected
            mSelectedItemsIds.delete(position);
            mSelectedItemsPositions.remove(Integer.valueOf(position));
            mSelectedItemsValues.remove(items.get(position));
        } else {
            mSelectedItemsIds.put(position, true);
            mSelectedItemsPositions.add(position);
            mSelectedItemsValues.add((SeafDirent) items.get(position));
        }

        mActivity.onItemSelected();
        notifyDataSetChanged();
    }

    /**
     * Gets checked item count.
     *
     * @return the checked item count
     */
    public int getCheckedItemCount() {
        return mSelectedItemsIds.size();
    }

    /**
     * Gets selected items values.
     *
     * @return the selected items values
     */
    public List<SeafDirent> getSelectedItemsValues() {
        return mSelectedItemsValues;
    }

    private class Viewholder {
        /**
         * The Title.
         */
        TextView title, /**
         * The Subtitle.
         */
        subtitle;
        /**
         * The Icon.
         */
        ImageView icon, /**
         * The Multi select.
         */
        multiSelect, /**
         * The Download status icon.
         */
        downloadStatusIcon; // downloadStatusIcon used to show file downloading status, it is invisible by
        /**
         * The Progress bar.
         */
// default
        ProgressBar progressBar;
        /**
         * The Action.
         */
        RelativeLayout action;

        /**
         * Instantiates a new Viewholder.
         *
         * @param title              the title
         * @param subtitle           the subtitle
         * @param multiSelect        the multi select
         * @param icon               the icon
         * @param action             the action
         * @param downloadStatusIcon the download status icon
         * @param progressBar        the progress bar
         */
        public Viewholder(TextView title,
                          TextView subtitle,
                          ImageView multiSelect,
                          ImageView icon,
                          RelativeLayout action,
                          ImageView downloadStatusIcon,
                          ProgressBar progressBar
        ) {
            super();
            this.icon = icon;
            this.multiSelect = multiSelect;
            this.action = action;
            this.title = title;
            this.subtitle = subtitle;
            this.downloadStatusIcon = downloadStatusIcon;
            this.progressBar = progressBar;
        }
    }

    private int getThumbnailWidth() {
        return (int) SeadroidApplication.getAppContext().getResources().getDimension(R.dimen.lv_icon_width);
    }

    /**
     * Sets encrypted repo.
     *
     * @param encrypted the encrypted
     */
    public void setEncryptedRepo(boolean encrypted) {
        repoIsEncrypted = encrypted;
    }

    /**
     * Sorts the given list by type of {@link #SORT_BY_NAME} or {@link #SORT_BY_LAST_MODIFIED_TIME},
     * and by order of {@link #SORT_ORDER_ASCENDING} or {@link #SORT_ORDER_DESCENDING}
     *
     * @param type  the type
     * @param order the order
     */
    public void sortFiles(int type, int order) {
        List<SeafGroup> groups = Lists.newArrayList();
        List<SeafCachedFile> cachedFiles = Lists.newArrayList();
        List<SeafDirent> folders = Lists.newArrayList();
        List<SeafDirent> files = Lists.newArrayList();
        SeafGroup group = null;

        for (SeafItem item : items) {
            if (item instanceof SeafGroup) {
                group = (SeafGroup) item;
                groups.add(group);
            } else if (item instanceof SeafRepo) {
                if (group == null)
                    continue;
                group.addIfAbsent((SeafRepo) item);
            } else if (item instanceof SeafCachedFile) {
                cachedFiles.add(((SeafCachedFile) item));
            } else {
                if (((SeafDirent) item).isDir())
                    folders.add(((SeafDirent) item));
                else
                    files.add(((SeafDirent) item));
            }
        }

        items.clear();

        // sort SeafGroups and SeafRepos
        for (SeafGroup sg : groups) {
            sg.sortByType(type, order);
            items.add(sg);
            items.addAll(sg.getRepos());
        }

        // sort SeafDirents
        if (type == SORT_BY_NAME) {
            // sort by name, in ascending order
            Collections.sort(folders, new SeafDirent.DirentNameComparator());
            Collections.sort(files, new SeafDirent.DirentNameComparator());
            if (order == SORT_ORDER_DESCENDING) {
                Collections.reverse(folders);
                Collections.reverse(files);
            }
        } else if (type == SORT_BY_LAST_MODIFIED_TIME) {
            // sort by last modified time, in ascending order
            Collections.sort(folders, new SeafDirent.DirentLastMTimeComparator());
            Collections.sort(files, new SeafDirent.DirentLastMTimeComparator());
            if (order == SORT_ORDER_DESCENDING) {
                Collections.reverse(folders);
                Collections.reverse(files);
            }
        }
        // Adds the objects in the specified collection to this ArrayList
        items.addAll(cachedFiles);
        items.addAll(folders);
        items.addAll(files);
    }
}

