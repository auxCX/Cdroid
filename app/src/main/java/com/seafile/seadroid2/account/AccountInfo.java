package com.seafile.seadroid2.account;

import com.seafile.seadroid2.util.Utils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class used to manage Account information
 */
public class AccountInfo {
    private static final String DEBUG_TAG = "AccountInfo";

    /**
     * The constant SPACE_USAGE_SEPERATOR.
     */
    public static final String SPACE_USAGE_SEPERATOR = " / ";
    private long usage;
    private long total;
    private String email;
    private String server;
    private String name;

    private AccountInfo() {}

    /**
     * From json account info.
     *
     * @param accountInfo the account info
     * @param server      the server
     * @return the account info
     * @throws JSONException the json exception
     */
    public static  AccountInfo fromJson(JSONObject accountInfo, String server) throws JSONException {
        AccountInfo info = new AccountInfo();
        info.server = server;
        info.usage = accountInfo.getLong("usage");
        info.total = accountInfo.getLong("total");
        info.email = accountInfo.getString("email");
        info.name = accountInfo.optString("name");


        return info;
    }

    /**
     * Gets usage.
     *
     * @return the usage
     */
    public long getUsage() {
        return usage;
    }

    /**
     * Gets total.
     *
     * @return the total
     */
    public long getTotal() {
        return total;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets server.
     *
     * @return the server
     */
    public String getServer() {
        return server;
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
     * Gets space used.
     *
     * @return the space used
     */
    public String getSpaceUsed() {
        String strUsage = Utils.readableFileSize(usage);
        String strTotal = Utils.readableFileSize(total);
        return strUsage + SPACE_USAGE_SEPERATOR + strTotal;
    }

}
