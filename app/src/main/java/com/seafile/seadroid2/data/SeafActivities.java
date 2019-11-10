package com.seafile.seadroid2.data;

import java.util.List;

/**
 * Seafile Activities data model
 */
public class SeafActivities {

    /**
     * The Events.
     */
    public List<SeafEvent> events;

    /**
     * The More.
     */
    public boolean more;

    /**
     * The Offset.
     */
    public int offset;

    /**
     * Gets events.
     *
     * @return the events
     */
    public List<SeafEvent> getEvents() {
        return events;
    }

    /**
     * Sets events.
     *
     * @param events the events
     */
    public void setEvents(List<SeafEvent> events) {
        this.events = events;
    }

    /**
     * Is more boolean.
     *
     * @return the boolean
     */
    public boolean isMore() {
        return more;
    }

    /**
     * Sets more.
     *
     * @param more the more
     */
    public void setMore(boolean more) {
        this.more = more;
    }

    /**
     * Gets offset.
     *
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets offset.
     *
     * @param offset the offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Instantiates a new Seaf activities.
     *
     * @param events the events
     * @param offset the offset
     * @param more   the more
     */
    public SeafActivities(List<SeafEvent> events, int offset, boolean more) {
        this.events = events;
        this.offset = offset;
        this.more = more;
    }
}
