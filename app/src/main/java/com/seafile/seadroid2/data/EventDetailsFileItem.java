package com.seafile.seadroid2.data;

/**
 * The type Event details file item.
 */
public class EventDetailsFileItem {

    /**
     * The enum E type.
     */
    public enum EType {
        /**
         * File added e type.
         */
        FILE_ADDED,
        /**
         * File deleted e type.
         */
        FILE_DELETED,
        /**
         * File modified e type.
         */
        FILE_MODIFIED,
        /**
         * File renamed e type.
         */
        FILE_RENAMED,
        /**
         * Dir added e type.
         */
        DIR_ADDED,
        /**
         * Dir deleted e type.
         */
        DIR_DELETED
    }

    private String path;
    private EType eType;
    private SeafEvent event;

    /**
     * Instantiates a new Event details file item.
     *
     * @param event the event
     * @param path  the path
     * @param etype the etype
     */
    public EventDetailsFileItem(SeafEvent event, String path, EType etype) {
        this.path = path;
        this.eType = etype;
        this.event = event;
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets path.
     *
     * @param path the path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public EType geteType() {
        return eType;
    }

    /**
     * Gets event.
     *
     * @return the event
     */
    public SeafEvent getEvent() {
        return event;
    }

    /**
     * Sets event.
     *
     * @param event the event
     */
    public void setEvent(SeafEvent event) {
        this.event = event;
    }

    /**
     * Is file openable boolean.
     *
     * @return the boolean
     */
    public boolean isFileOpenable() {
        return eType == EType.FILE_ADDED ||
                eType == EType.FILE_MODIFIED ||
                eType == EType.FILE_RENAMED ||
                eType == EType.DIR_ADDED;
    }

    /**
     * Is dir boolean.
     *
     * @return the boolean
     */
    public boolean isDir() {
        return eType == EType.DIR_ADDED;
    }

}
