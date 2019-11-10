package com.seafile.seadroid2.data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Block entity
 */
public class Block implements Serializable {

    /**
     * The Block id.
     */
    public String blockId;
    /**
     * The Path.
     */
    public String path;
    /**
     * The Size.
     */
    public long size;
    /**
     * The Finished.
     */
    public long finished;

    /**
     * Instantiates a new Block.
     *
     * @param blockId  the block id
     * @param path     the path
     * @param size     the size
     * @param finished the finished
     */
    public Block(String blockId, String path, long size, long finished) {
        this.blockId = blockId;
        this.path = path;
        this.size = size;
        this.finished = finished;
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
     * Gets size.
     *
     * @return the size
     */
    public float getSize() {
        return size;
    }

    /**
     * Sets size.
     *
     * @param size the size
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Gets finished.
     *
     * @return the finished
     */
    public float getFinished() {
        return finished;
    }

    /**
     * Sets finished.
     *
     * @param finished the finished
     */
    public void setFinished(long finished) {
        this.finished = finished;
    }

    /**
     * Gets block id.
     *
     * @return the block id
     */
    public String getBlockId() {

        return blockId;
    }

    /**
     * Sets block id.
     *
     * @param blockId the block id
     */
    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Block block = (Block) o;

        if (size != block.size) return false;
        if (finished != block.finished) return false;
        if (!blockId.equals(block.blockId)) return false;
        return path != null ? path.equals(block.path) : block.path == null;

    }

    @Override
    public int hashCode() {
        int result = blockId.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (int) (finished ^ (finished >>> 32));
        return result;
    }
}
