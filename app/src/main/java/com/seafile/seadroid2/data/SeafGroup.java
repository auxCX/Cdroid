package com.seafile.seadroid2.data;

import com.google.common.collect.Lists;
import com.seafile.seadroid2.ui.adapter.SeafItemAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The type Seaf group.
 */
public class SeafGroup implements SeafItem {
    private String name;
    private List<SeafRepo> repos = Lists.newArrayList();

    /**
     * Instantiates a new Seaf group.
     *
     * @param name the name
     */
    public SeafGroup(String name) {
        this.name = name;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSubtitle() {
        return null;
    }

    @Override
    public int getIcon() {
        return 0;
    }

    /**
     * Gets repos.
     *
     * @return the repos
     */
    public List<SeafRepo> getRepos() {
        return repos;
    }

    /**
     * Add if absent.
     *
     * @param repo the repo
     */
    public void addIfAbsent(SeafRepo repo) {
        if (!repos.contains(repo))
            this.repos.add(repo);
    }

    /**
     * sort collections by repository name or last modified time
     *
     * @param type  the type
     * @param order the order
     */
    public void sortByType(int type, int order) {
        if (type == SeafItemAdapter.SORT_BY_NAME) {
            Collections.sort(repos, new SeafRepo.RepoNameComparator());
            if (order == SeafItemAdapter.SORT_ORDER_DESCENDING) {
                Collections.reverse(repos);
            }
        } else if (type == SeafItemAdapter.SORT_BY_LAST_MODIFIED_TIME) {
            Collections.sort(repos, new SeafRepo.RepoLastMTimeComparator());
            if (order == SeafItemAdapter.SORT_ORDER_DESCENDING) {
                Collections.reverse(repos);
            }
        }
    }

}
