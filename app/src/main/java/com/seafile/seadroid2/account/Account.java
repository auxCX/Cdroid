package com.seafile.seadroid2.account;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.common.base.Objects;
import com.seafile.seadroid2.BuildConfig;
import com.seafile.seadroid2.util.Utils;

import java.util.ArrayList;

/**
 * The type Account.
 */
public class Account implements Parcelable, Comparable<Account> {
    private static final String DEBUG_TAG = "Account";

    /**
     * Type of the account (currently there is only one type)
     */
    public final static String ACCOUNT_TYPE = BuildConfig.ACCOUNT_TYPE;

    /**
     * The Server.
     */
// The full URL of the server, like 'http://gonggeng.org/seahub/' or 'http://gonggeng.org/'
    public final String server;
    /**
     * The Name.
     */
    public final String name;

    /**
     * The Email.
     */
    public final String email;

    /**
     * The Is shib.
     */
    public final Boolean is_shib;

    /**
     * The Token.
     */
    public String token;
    /**
     * The Session key.
     */
    public String sessionKey;

    private ArrayList<String> updateList;

    /**
     * Instantiates a new Account.
     *
     * @param server  the server
     * @param email   the email
     * @param name    the name
     * @param token   the token
     * @param is_shib the is shib
     */
    public Account(String server, String email, String name, String token, Boolean is_shib) {
        this.name = name;
        this.server = server;
        this.email = email;
        this.token = token;
        this.is_shib = is_shib;
    }

    /**
     * Instantiates a new Account.
     *
     * @param name       the name
     * @param server     the server
     * @param email      the email
     * @param token      the token
     * @param is_shib    the is shib
     * @param sessionKey the session key
     */
    public Account(String name, String server, String email, String token, Boolean is_shib, String sessionKey) {
        this.server = server;
        this.name = name;
        this.email = email;
        this.token = token;
        this.sessionKey = sessionKey;
        this.is_shib = is_shib;
    }


    /**
     * Gets server host.
     *
     * @return the server host
     */
    public String getServerHost() {
        String s = server.substring(server.indexOf("://") + 3);
        return s.substring(0, s.indexOf('/'));
    }

    /**
     * Gets server domain name.
     *
     * @return the server domain name
     */
    public String getServerDomainName() {
        String dn = getServerHost();
        // strip port, like :8000 in 192.168.1.116:8000
        if (dn.contains(":"))
            dn = dn.substring(0, dn.indexOf(':'));
        return dn;
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
     * Get name string.
     *
     * @return the string
     */
    public String getName(){
        return name;
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
     * Gets server no protocol.
     *
     * @return the server no protocol
     */
    public String getServerNoProtocol() {
        String result = server.substring(server.indexOf("://") + 3);
        if (result.endsWith("/"))
            result = result.substring(0, result.length() - 1);
        return result;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Is https boolean.
     *
     * @return the boolean
     */
    public boolean isHttps() {
        return server.startsWith("https");
    }

    /**
     * Is shib boolean.
     *
     * @return the boolean
     */
    public boolean isShib() {
        return is_shib;
    }

    /**
     * Gets session key.
     *
     * @return the session key
     */
    public String getSessionKey() {
        return sessionKey;
    }

    /**
     * Sets session key.
     *
     * @param sessionKey the session key
     */
    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(server, email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || (obj.getClass() != this.getClass()))
            return false;

        Account a = (Account) obj;
        if (a.server == null || a.email == null || a.token == null )
            return false;

        return a.server.equals(this.server) && a.email.equals(this.email);
    }

    /**
     * Gets signature.
     *
     * @return the signature
     */
    public String getSignature() {
        return String.format("%s (%s)", getServerNoProtocol(), email);
    }

    /**
     * Gets display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        String server = Utils.stripSlashes(getServerHost());
        return Utils.assembleUserName(name, email, server);
    }

    /**
     * Gets android account.
     *
     * @return the android account
     */
    public android.accounts.Account getAndroidAccount() {
        return new android.accounts.Account(getSignature(), ACCOUNT_TYPE);
    }

    /**
     * Has valid token boolean.
     *
     * @return the boolean
     */
    public boolean hasValidToken() {
        return !TextUtils.isEmpty(token);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.server);
        out.writeString(this.name);
        out.writeString(this.email);
        out.writeString(this.token);
        out.writeString(this.sessionKey);
        out.writeValue(this.is_shib);
    }

    /**
     * The constant CREATOR.
     */
    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    /**
     * Instantiates a new Account.
     *
     * @param in the in
     */
    protected Account(Parcel in) {
        this.server = in.readString();
        this.name = in.readString();
        this.email = in.readString();
        this.token = in.readString();
        this.sessionKey = in.readString();
        this.is_shib = (Boolean) in.readValue(Boolean.class.getClassLoader());

       // Log.d(DEBUG_TAG, String.format("%s %s %s %b", server, email, token ,is_shib));
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("server", server)
                .add("user", email)
                .add("name", name)
                .add("sessionKey", sessionKey)
                .toString();
    }

    @Override
    public int compareTo(Account other) {
        return this.toString().compareTo(other.toString());
    }
}
