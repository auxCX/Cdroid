package com.seafile.seadroid2.data;

import com.seafile.seadroid2.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * The type Block info bean.
 */
public class BlockInfoBean {
    /**
     * The Blk ids.
     */
    public List<String> blkIds;
    /**
     * The Rawblksurl.
     */
    public String rawblksurl;
    /**
     * The Commiturl.
     */
    public String commiturl;

    /**
     * From json block info bean.
     *
     * @param json the json
     * @return the block info bean
     * @throws JSONException the json exception
     */
    public static BlockInfoBean fromJson(String json) throws JSONException {
        BlockInfoBean bean = new BlockInfoBean();
        JSONObject obj = Utils.parseJsonObject(json);
        bean.rawblksurl = obj.optString("rawblksurl");
        bean.commiturl = obj.optString("commiturl");
        JSONArray jsonArray = obj.optJSONArray("blklist");
        bean.blkIds = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            bean.blkIds.add(jsonArray.getString(i));
        }
        return bean;
    }

    /**
     * Instantiates a new Block info bean.
     */
    public BlockInfoBean() {
    }
}
