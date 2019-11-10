package com.seafile.seadroid2.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.common.collect.Lists;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.data.SearchedFile;
import com.seafile.seadroid2.ui.activity.SearchActivity;
import com.seafile.seadroid2.util.Utils;

import java.util.List;

/**
 * Adapter for search list
 */
public class SearchAdapter extends BaseAdapter {

    private List<SearchedFile> items;
    private SearchActivity mActivity;

    /**
     * Instantiates a new Search adapter.
     *
     * @param activity the activity
     */
    public SearchAdapter(SearchActivity activity) {
        this.mActivity = activity;
        items = Lists.newArrayList();
    }

    /**
     * Sets items.
     *
     * @param data the data
     */
    public void setItems(List<SearchedFile> data) {
        this.items = data;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchedFile item = items.get(position);
        View view = convertView;
        Viewholder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.search_list_item, null);
            TextView path = (TextView) view.findViewById(R.id.search_item_path);
            TextView title = (TextView) view.findViewById(R.id.search_item_title);
            TextView subtitle = (TextView) view.findViewById(R.id.search_item_subtitle);
            ImageView icon = (ImageView) view.findViewById(R.id.search_item_icon);
            ImageView action = (ImageView) view.findViewById(R.id.search_item_action);
            viewHolder = new Viewholder(path, title, subtitle, icon, action);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Viewholder) convertView.getTag();
        }

        viewHolder.icon.setImageResource(item.getIcon());
        viewHolder.path.setText(filePath(item));
        viewHolder.title.setText(item.getTitle());
        viewHolder.subtitle.setText(item.getSubtitle());

        return view;
    }

    private String filePath(SearchedFile searchedFile) {
        String parentPath = Utils.getParentPath(searchedFile.getPath());
        SeafRepo seafRepo = mActivity.getDataManager().getCachedRepoByID(searchedFile.getRepoID());
        if (seafRepo != null)
            return Utils.pathJoin(seafRepo.getName(), parentPath);
        else
            return parentPath;
    }

    /**
     * Notify changed.
     */
    public void notifyChanged() {
        notifyDataSetChanged();
    }

    private class Viewholder {
        /**
         * The Path.
         */
        TextView path, /**
         * The Title.
         */
        title, /**
         * The Subtitle.
         */
        subtitle;
        /**
         * The Icon.
         */
        ImageView icon, /**
         * The Action.
         */
        action;

        /**
         * Instantiates a new Viewholder.
         *
         * @param path     the path
         * @param title    the title
         * @param subtitle the subtitle
         * @param icon     the icon
         * @param action   the action
         */
        public Viewholder(TextView path, TextView title, TextView subtitle, ImageView icon, ImageView action) {
            super();
            this.icon = icon;
            this.action = action;
            this.path = path;
            this.title = title;
            this.subtitle = subtitle;
        }
    }
}
