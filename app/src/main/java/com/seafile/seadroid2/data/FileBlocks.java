package com.seafile.seadroid2.data;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.common.collect.Lists;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Seafile file blocks
 */
public class FileBlocks implements Serializable {
    /**
     * The constant DEBUG_TAG.
     */
    public static final String DEBUG_TAG = "FileBlocks";

    /**
     * The Blocks.
     */
    public ArrayList<Block> blocks;

    /**
     * The Enc version.
     */
    public int encVersion;
    /**
     * The Blklist.
     */
    public String blklist;
    /**
     * The File id.
     */
    public String fileID;

    /**
     * Instantiates a new File blocks.
     */
    public FileBlocks() {
        blocks = new ArrayList<>();
    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public long getSize() {
        long size = 0L;
        for (Block block : blocks) {
            size += block.size;
        }
        return size;
    }

    /**
     * Gets finished.
     *
     * @return the finished
     */
    public long getFinished() {
        long finished = 0L;
        for (Block block : blocks) {
            finished += block.finished;
        }
        return finished;
    }

    /**
     * Gets block.
     *
     * @param blkId the blk id
     * @return the block
     */
    public Block getBlock(@NonNull String blkId) {
        for (Block block : blocks) {
            if (blkId.equals(block.blockId)) {
                return block;
            }
        }
        return null;
    }

    /**
     * From json file blocks.
     *
     * @param obj the obj
     * @return the file blocks
     * @throws JSONException the json exception
     */
    static FileBlocks fromJson(JSONObject obj) throws JSONException {
        FileBlocks blocks = new FileBlocks();
        blocks.blklist = obj.optString("blklist");
        blocks.fileID = obj.optString("file_id");
        blocks.blocks = getBlockIds(blocks.blklist);
        blocks.encVersion = obj.optInt("enc_version");
        return blocks;
    }

    private static ArrayList<Block> getBlockIds(String blklist) {
        if (TextUtils.isEmpty(blklist) || blklist.equals("[]"))
            return null;

        final List<String> blkIds = Arrays.asList(blklist.split("\\s*,\\s*"));

        ArrayList<Block> ids = Lists.newArrayList();
        for (String blkid : blkIds) {
            final String substring = blkid.substring(blkid.indexOf("\"") + 1, blkid.lastIndexOf("\""));
            Block block = new Block(substring, null, 0, 0);
            ids.add(block);
        }

        return ids;
    }

}
