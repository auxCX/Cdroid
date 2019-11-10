package com.seafile.seadroid2.ui.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.SeafEvent;
import com.seafile.seadroid2.data.SeafItem;
import com.seafile.seadroid2.ui.activity.BrowserActivity;
import com.seafile.seadroid2.ui.widget.CircleImageView;
import com.seafile.seadroid2.util.SystemSwitchUtils;
import com.seafile.seadroid2.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter for Activities tab
 */
public class ActivitiesItemAdapter extends BaseAdapter {
    /**
     * The constant DEBUG_TAG.
     */
    public static final String DEBUG_TAG = ActivitiesItemAdapter.class.getSimpleName();

    /**
     * The constant REFRESH_ON_NONE.
     */
    public static final int REFRESH_ON_NONE = 0;
    /**
     * The constant REFRESH_ON_PULL_DOWN.
     */
    public static final int REFRESH_ON_PULL_DOWN = 1;
    /**
     * The constant REFRESH_ON_PULL_UP.
     */
    public static final int REFRESH_ON_PULL_UP = 2;
    private int state = REFRESH_ON_NONE;

    private ArrayList<SeafEvent> items;
    private BrowserActivity mActivity;
    private ImageLoader loader;
    private DisplayImageOptions options;
    private boolean useNewActivity;

    /**
     * Instantiates a new Activities item adapter.
     *
     * @param activity the activity
     */
    public ActivitiesItemAdapter(BrowserActivity activity) {
        this.mActivity = activity;
        items = Lists.newArrayList();
        loader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .extraForDownloader(mActivity.getAccount())
                .showStubImage(R.drawable.default_avatar)
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.default_avatar)
                .resetViewBeforeLoading()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }

    @Override
    public int getCount() {
        return items.size() + 1;
    }

    /**
     * Clear.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Add.
     *
     * @param entry the entry
     */
    public void add(SeafEvent entry) {
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Sets items.
     *
     * @param events         the events
     * @param useNewActivity the use new activity
     */
    public void setItems(List<SeafEvent> events, boolean useNewActivity) {
        this.useNewActivity = useNewActivity;
        items.clear();
        items.addAll(events);
    }

    private LinearLayout mFooterView;

    /**
     * Sets footer view loading.
     *
     * @param more the more
     */
    public void setFooterViewLoading(boolean more) {
        ProgressBar progress = (ProgressBar) mFooterView.findViewById(R.id.progressbar);
        TextView text = (TextView) mFooterView.findViewById(R.id.text);
        if (more) {
            mFooterView.setVisibility(View.VISIBLE);
            progress.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
            mFooterView.setVisibility(View.GONE);
            text.setVisibility(View.GONE);
        }
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * Gets footer view.
     *
     * @return the footer view
     */
    public View getFooterView() {
        return this.mFooterView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getCount() - 1) {
            this.mFooterView = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_load_more, null);
            switch (state) {
                case REFRESH_ON_NONE:
                case REFRESH_ON_PULL_DOWN:
                    setFooterViewLoading(false);
                    break;
                case REFRESH_ON_PULL_UP:
                    setFooterViewLoading(true);
                    break;
            }
            return mFooterView;
        }
        if (position < 0) {
            position = 0;
        }

        final SeafEvent item = items.get(position);
        View view = convertView;
        // TODO optimize by setting tags
        final ViewHolder viewHolder;

        view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_activities, null);
        RelativeLayout rl_old = (RelativeLayout) view.findViewById(R.id.rl_activities_old);

        TextView title = (TextView) view.findViewById(R.id.tv_activities_mod_desc);
        TextView nick = (TextView) view.findViewById(R.id.tv_activities_nick);
        TextView date = (TextView) view.findViewById(R.id.tv_activities_date);
        TextView repoName = (TextView) view.findViewById(R.id.tv_activities_repo_name);
        CircleImageView icon = (CircleImageView) view.findViewById(R.id.iv_activities_avatar);

        RelativeLayout rl_new = (RelativeLayout) view.findViewById(R.id.rl_activities_new);
        CircleImageView icon_url = (CircleImageView) view.findViewById(R.id.iv_activities_avatar_url);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_activities_name);
        TextView tv_state = (TextView) view.findViewById(R.id.tv_activities_state);
        TextView tv_desc = (TextView) view.findViewById(R.id.tv_activities_desc);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_activities_time);
        TextView tv_mod = (TextView) view.findViewById(R.id.tv_activities_mod);
        viewHolder = new ViewHolder(title, nick, date, repoName, icon, tv_name, tv_state, tv_desc, tv_time, tv_mod, icon_url, rl_old, rl_new);
        view.setTag(viewHolder);
        if (useNewActivity) {
            rl_old.setVisibility(View.GONE);
            rl_new.setVisibility(View.VISIBLE);
            item.setAvatar(item.getAvatar_url());
            viewHolder.tv_name.setText(item.getAuthor_name());
            loader.displayImage(item.getAvatar_url(), viewHolder.icon_url, options);
            viewHolder.tv_time.setText(SystemSwitchUtils.parseDateTime(item.getV_time()));
            viewHolder.tv_mod.setText(item.getRepo_name());
            viewHolder.tv_desc.setText(item.getPath());
            viewHolder.tv_state.setText(SystemSwitchUtils.obj_type(mActivity, item.getObj_type(), item.getOp_type()));

        } else {
            rl_old.setVisibility(View.VISIBLE);
            rl_new.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(item.getAvatar())) {
            final String avatar = parseAvatar(item.getAvatar());
            loader.displayImage(avatar, viewHolder.icon, options);
        } else {
            // show a place holder indicating the error
            loader.displayImage(item.getAvatar(), viewHolder.icon, options);
        }

        viewHolder.title.setText(item.getDesc());
        viewHolder.nick.setText(item.getNick());

        if (!TextUtils.isEmpty(item.getTime_relative())) {
            final String relative = parseRelativeTime(item.getTime_relative());
            viewHolder.date.setText(relative);
            viewHolder.date.setVisibility(View.VISIBLE);
        } else {
            viewHolder.date.setVisibility(View.GONE);
        }
        viewHolder.repoName.setText(item.getRepo_name());
        return view;
    }

    private String parseAvatar(@NonNull String avatar) {
        // <img src="/seahub/image-view/avatars/7/9/dc411b7a64a20963ccff32563e38d6/resized/36/bamboo_5.png" width="36" height="36" class="avatar" />
        String re1 = ".*?";   // Non-greedy match on filler
        String re2 = "(src)"; // Variable Name 1
        String re3 = ".*?";   // Non-greedy match on filler
        String re4 = "((?:\\/[\\w\\.\\-]+)+)";    // Unix Path 1

        Pattern p = Pattern.compile(re1 + re2 + re3 + re4, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(avatar);
        if (m.find()) {
            String avatarPath = m.group(2);
            return Utils.pathJoin(mActivity.getAccount().getServer(), avatarPath);
        } else return avatar;
    }

    private String parseRelativeTime(@NonNull String relativeTime) {
        String regex = "(<[^>]+>)";
        final String[] split = relativeTime.split(regex);
        if (split.length > 1) {
            return split[1];
        } else return relativeTime;
    }

    private class ViewHolder {
        /**
         * The Title.
         */
        TextView title, /**
         * The Nick.
         */
        nick, /**
         * The Date.
         */
        date, /**
         * The Repo name.
         */
        repoName;
        /**
         * The Icon.
         */
        ImageView icon, /**
         * The Icon url.
         */
        icon_url;
        /**
         * The Tv name.
         */
        TextView tv_name, /**
         * The Tv state.
         */
        tv_state, /**
         * The Tv desc.
         */
        tv_desc, /**
         * The Tv mod.
         */
        tv_mod, /**
         * The Tv time.
         */
        tv_time;
        /**
         * The Rl old.
         */
        RelativeLayout rl_old, /**
         * The Rl new.
         */
        rl_new;

        /**
         * Instantiates a new View holder.
         *
         * @param title    the title
         * @param nick     the nick
         * @param date     the date
         * @param repoName the repo name
         * @param icon     the icon
         * @param tv_name  the tv name
         * @param tv_state the tv state
         * @param tv_desc  the tv desc
         * @param tv_time  the tv time
         * @param tv_mod   the tv mod
         * @param icon_url the icon url
         * @param rl_old   the rl old
         * @param rl_new   the rl new
         */
        public ViewHolder(TextView title, TextView nick, TextView date, TextView repoName, ImageView icon, TextView tv_name,
                          TextView tv_state, TextView tv_desc, TextView tv_time, TextView tv_mod, ImageView icon_url, RelativeLayout rl_old, RelativeLayout rl_new) {
            super();
            this.icon = icon;
            this.title = title;
            this.nick = nick;
            this.date = date;
            this.repoName = repoName;
            this.icon_url = icon_url;
            this.tv_name = tv_name;
            this.tv_state = tv_state;
            this.tv_desc = tv_desc;
            this.tv_time = tv_time;
            this.tv_mod = tv_mod;
            this.rl_old = rl_old;
            this.rl_new = rl_new;
        }
    }

}
