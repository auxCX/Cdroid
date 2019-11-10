package com.seafile.seadroid2.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.common.collect.Lists;
import com.seafile.seadroid2.SeadroidApplication;
import com.seafile.seadroid2.data.SeafRepo;

import java.util.List;

/**
 * Base ReposAdapter
 */
public abstract class ReposAdapter extends BaseAdapter {

    /**
     * The Repos.
     */
    protected List<SeafRepo> repos = Lists.newArrayList();
    /**
     * The Only show writable repos.
     */
    protected boolean onlyShowWritableRepos;
    /**
     * The Encrypted repo id.
     */
    protected String encryptedRepoId;

    /**
     * Instantiates a new Repos adapter.
     *
     * @param onlyShowWritableRepos the only show writable repos
     * @param encryptedRepoId       the encrypted repo id
     */
    public ReposAdapter(boolean onlyShowWritableRepos, String encryptedRepoId) {
        this.onlyShowWritableRepos = onlyShowWritableRepos;
        this.encryptedRepoId = encryptedRepoId;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Are all repos selectable boolean.
     *
     * @return the boolean
     */
    public boolean areAllReposSelectable() {
        return false;
    }

    /**
     * Gets data.
     *
     * @return the data
     */
    public  List<SeafRepo> getData() {
        return repos;
    }

    /**
     * Sets repos.
     *
     * @param seafRepos the seaf repos
     */
    public void setRepos(List<SeafRepo> seafRepos) {
        repos.clear();
        for (SeafRepo repo: seafRepos) {
            if (onlyShowWritableRepos && !repo.hasWritePermission()) {
                continue;
            }
            if (encryptedRepoId != null && !repo.id.equals(encryptedRepoId)) {
                continue;
            }
            repos.add(repo);
        }
        notifyDataSetChanged();
    }

    /**
     * Gets child layout.
     *
     * @return the child layout
     */
    protected abstract int getChildLayout();

    /**
     * Gets child title id.
     *
     * @return the child title id
     */
    protected abstract int getChildTitleId();

    /**
     * Gets child sub title id.
     *
     * @return the child sub title id
     */
    protected abstract int getChildSubTitleId();

    /**
     * Gets child icon id.
     *
     * @return the child icon id
     */
    protected abstract int getChildIconId();

    /**
     * Gets child action id.
     *
     * @return the child action id
     */
    protected abstract int getChildActionId();

    /**
     * Gets child seaf repo.
     *
     * @param position the position
     * @return the child seaf repo
     */
    protected abstract SeafRepo getChildSeafRepo(int position);

    /**
     * Show repo selected icon.
     *
     * @param position  the position
     * @param imageView the image view
     */
    protected abstract void showRepoSelectedIcon(int position, ImageView imageView);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Viewholder viewHolder;

        SeafRepo repo = getChildSeafRepo(position);

        if (convertView == null) {
            view = LayoutInflater.from(SeadroidApplication.getAppContext())
                    .inflate(getChildLayout(), null);
            TextView title = (TextView) view.findViewById(getChildTitleId());
            TextView subtitle = (TextView) view.findViewById(getChildSubTitleId());
            ImageView icon = (ImageView) view.findViewById(getChildIconId());
            ImageView action = (ImageView) view.findViewById(getChildActionId());
            viewHolder = new Viewholder(title, subtitle, icon, action);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Viewholder) convertView.getTag();
        }

        viewHolder.title.setText(repo.getTitle());
        viewHolder.subtitle.setText(repo.getSubtitle());
        viewHolder.icon.setImageResource(repo.getIcon());
        viewHolder.action.setVisibility(View.INVISIBLE);

        showRepoSelectedIcon(position, viewHolder.action);
        return view;
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
         * The Action.
         */
        action;

        /**
         * Instantiates a new Viewholder.
         *
         * @param title    the title
         * @param subtitle the subtitle
         * @param icon     the icon
         * @param action   the action
         */
        public Viewholder(TextView title, TextView subtitle, ImageView icon, ImageView action) {
            super();
            this.icon = icon;
            this.action = action;
            this.title = title;
            this.subtitle = subtitle;
        }
    }
}
