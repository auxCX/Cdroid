package com.seafile.seadroid2.play;

/**
 * Created by shuyu on 2016/12/7.
 */
public class SwitchVideoModel {
    private String url;
    private String name;

    /**
     * Instantiates a new Switch video model.
     *
     * @param name the name
     * @param url  the url
     */
    public SwitchVideoModel(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}