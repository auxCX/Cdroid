package com.seafile.seadroid2.data;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.goterl.lazycode.lazysodium.LazySodiumAndroid;
import com.goterl.lazycode.lazysodium.SodiumAndroid;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.SecretStream;
import com.goterl.lazycode.lazysodium.utils.Key;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeadroidApplication;
import com.seafile.seadroid2.SeafConnection;
import com.seafile.seadroid2.SeafException;
import com.seafile.seadroid2.account.Account;
import com.seafile.seadroid2.account.AccountInfo;
import com.seafile.seadroid2.crypto.Crypto;
import com.seafile.seadroid2.util.Utils;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The type Data manager.
 */
public class DataManager {
    private static final String DEBUG_TAG = "DataManager";
    private static final long SET_PASSWORD_INTERVAL = 59 * 60 * 1000; // 59 min
    // private static final long SET_PASSWORD_INTERVAL = 5 * 1000; // 5s

    /**
     * The constant PULL_TO_REFRESH_LAST_TIME_FOR_REPOS_FRAGMENT.
     */
// pull to refresh
    public static final String PULL_TO_REFRESH_LAST_TIME_FOR_REPOS_FRAGMENT = "repo fragment last update";
    /**
     * The constant PULL_TO_REFRESH_LAST_TIME_FOR_STARRED_FRAGMENT.
     */
    public static final String PULL_TO_REFRESH_LAST_TIME_FOR_STARRED_FRAGMENT = "starred fragment last update ";
    private static SimpleDateFormat ptrDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static Map<String, PasswordInfo> passwords = Maps.newHashMap();
    private static Map<String, Long> direntsRefreshTimeMap = Maps.newHashMap();
    /**
     * The constant REFRESH_EXPIRATION_MSECS.
     */
    public static final long REFRESH_EXPIRATION_MSECS = 10 * 60 * 1000; // 10 mins
    /**
     * The constant repoRefreshTimeStamp.
     */
    public static long repoRefreshTimeStamp = 0;

    /**
     * The constant BUFFER_SIZE.
     */
    public static final int BUFFER_SIZE = 2 * 1024 * 1024;
    /**
     * The constant PAGE_SIZE.
     */
    public static final int PAGE_SIZE = 25;

    private SeafConnection sc;
    private Account account;
    private static DatabaseHelper dbHelper;
    private static final StorageManager storageManager = StorageManager.getInstance();

    private List<SeafRepo> reposCache = null;


    private static LazySodiumAndroid lazySodium = new LazySodiumAndroid(new SodiumAndroid());



    /**
     * Instantiates a new Data manager.
     *
     * @param act the act
     */
    public DataManager(Account act) {
        account = act;
        sc = new SeafConnection(act);
        dbHelper = DatabaseHelper.getDatabaseHelper();
    }

    /**
     * Creates and returns a temporary file. It is guarantied that the file is unique and freshly
     * created. The caller has to delete that file himself.
     *
     * @return a newly created file.
     * @throws IOException if the file could not be created.
     */
    public static File createTempFile() throws IOException {
        return File.createTempFile("file-", ".tmp", storageManager.getTempDir());
    }

    /**
     * Creates and returns a temporary directory. It is guarantied that the directory is unique and
     * empty. The caller has to delete that directory himself.
     *
     * @return a newly created directory.
     * @throws IOException if the directory could not be created.
     */
    public static File createTempDir() throws IOException {
        String dirName = "dir-" + UUID.randomUUID();
        File dir = new File (storageManager.getTempDir(), dirName);
        if (dir.mkdir()) {
            return dir;
        } else {
            throw new IOException("Could not create temp directory");
        }
    }

    /**
     * Gets thumbnail link.
     *
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param filePath the file path
     * @param size     the size
     * @return the thumbnail link
     */
    public String getThumbnailLink(String repoName, String repoID, String filePath, int size) {
        File file = null;
        try {
            file = getLocalRepoFile(repoName, repoID, filePath);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }

        SeafRepo seafRepo = getCachedRepoByID(repoID);
        // encrypted repo doesn\`t support thumbnails
        if (seafRepo != null && seafRepo.encrypted)
            return null;

        // use locally cached file if available
        if (file.exists()) {
            return "file://" + file.getAbsolutePath();
        } else {
            try {
                String pathEnc = URLEncoder.encode(filePath, "UTF-8");
                return account.getServer() + String.format("api2/repos/%s/thumbnail/?p=%s&size=%s", repoID, pathEnc, size);
            } catch (UnsupportedEncodingException e) {
                return null;
            }

        }
    }

    /**
     * Gets thumbnail link.
     *
     * @param repoID   the repo id
     * @param filePath the file path
     * @param size     the size
     * @return the thumbnail link
     */
    public String getThumbnailLink(String repoID, String filePath, int size) {
        SeafRepo repo = getCachedRepoByID(repoID);
        if (repo != null)
            return getThumbnailLink(repo.getName(), repoID, filePath, size);
        else
            return null;
    }

    /**
     * Gets account info.
     *
     * @return the account info
     * @throws SeafException the seaf exception
     * @throws JSONException the json exception
     */
    public AccountInfo getAccountInfo() throws SeafException, JSONException {
        String json = sc.getAccountInfo();
        return parseAccountInfo(json);
    }

    private AccountInfo parseAccountInfo(String json) throws JSONException {
        JSONObject object = Utils.parseJsonObject(json);
        if (object == null)
            return null;

        return AccountInfo.fromJson(object, account.getServer());
    }

    /**
     * Gets server info.
     *
     * @return the server info
     * @throws SeafException the seaf exception
     * @throws JSONException the json exception
     */
    public ServerInfo getServerInfo() throws SeafException, JSONException {
        String json = sc.getServerInfo();
        return parseServerInfo(json);
    }

    private ServerInfo parseServerInfo(String json) throws JSONException {
        JSONObject object = Utils.parseJsonObject(json);
        if (object == null)
            return null;

        return ServerInfo.fromJson(object, account.getServer());
    }

    /**
     * Gets account.
     *
     * @return the account
     */
    public Account getAccount() {
        return account;
    }

    private File getFile4RepoCache(String repoID) {
        String filename = "repo-" + (account.server + account.email + repoID).hashCode() + ".dat";
        return new File(storageManager.getJsonCacheDir(), filename);
    }

    private File getFileForReposCache() {
        String filename = "repos-" + (account.server + account.email).hashCode() + ".dat";
        return new File(storageManager.getJsonCacheDir(), filename);
    }

    private File getFileForDirentCache(String dirID) {
        String filename = "dirent-" + dirID + ".dat";
        return new File(storageManager.getJsonCacheDir() + "/" + filename);
    }

    private File getFileForBlockCache(String blockId) {
        String filename = "block-" + blockId + ".dat";
        return new File(storageManager.getTempDir() + "/" + filename);
    }

    /**
     * The account directory structure of Seafile is like this:
     * <p>
     * StorageManager.getMediaDir()
     * |__ foo@gmail.com (cloud.seafile.com)
     * |__ Photos
     * |__ Musics
     * |__ ...
     * |__ foo@mycompany.com (seafile.mycompany.com)
     * |__ Documents
     * |__ Manuals
     * |__ ...
     * |__ ...
     * <p>
     * In the above directory, the user has used two accounts.
     * <p>
     * 1. One account has email "foo@gmail.com" and server
     * "cloud.seafile.com". Two repos, "Photos" and "Musics", has been
     * viewed.
     * <p>
     * 2. Another account has email "foo@mycompany.com", and server
     * "seafile.mycompany.com". Two repos, "Documents" and "Manuals", has
     * been viewed.
     *
     * @return the account dir
     */
    public String getAccountDir() {
        String username = account.getEmail();
        String server = Utils.stripSlashes(account.getServerHost());
        // strip port, like :8000 in 192.168.1.116:8000
        if (server.indexOf(":") != -1)
            server = server.substring(0, server.indexOf(':'));
        String p = String.format("%s (%s)", username, server);
        p = p.replaceAll("[^\\w\\d\\.@\\(\\) ]", "_");
        String accountDir = Utils.pathJoin(storageManager.getMediaDir().getAbsolutePath(), p);
        return accountDir;
    }

    /**
     * Get the top dir of a repo. If there are multiple repos with same name,
     * say "ABC", their top dir would be "ABC", "ABC (1)", "ABC (2)", etc. The
     * mapping (repoID, dir) is stored in a database table.
     */
    private synchronized String getRepoDir(String repoName, String repoID) throws RuntimeException {
        File repoDir;

        // Check if there is a record in database
        String uniqueRepoName = dbHelper.getRepoDir(account, repoID);
        if (uniqueRepoName != null) {
            // Has record in database
            repoDir = new File(getAccountDir(), uniqueRepoName);
            if (!repoDir.exists()) {
                if (!repoDir.mkdirs()) {
                    throw new RuntimeException("Could not create library directory " + repoDir);
                }
            }
            return repoDir.getAbsolutePath();
        }

        int i = 0;
        while (true) {
            if (i == 0) {
                uniqueRepoName = repoName;
            } else {
                uniqueRepoName = repoName + " (" + i + ")";
            }
            repoDir = new File(getAccountDir(), uniqueRepoName);
            if (!repoDir.exists() && !dbHelper.repoDirExists(account, uniqueRepoName)) {
                // This repo dir does not exist yet, we can use it
                break;
            }
            i++;
        }

        if (!repoDir.mkdirs()) {
            throw new RuntimeException("Could not create repo directory " + uniqueRepoName
                    + "Phone storage space is insufficient or too many " + uniqueRepoName + " directory in phone");
        }

        // Save the new mapping in database
        dbHelper.saveRepoDirMapping(account, repoID, uniqueRepoName);

        return repoDir.getAbsolutePath();
    }

    /**
     * Each repo is placed under [account-dir]/[repo-name]. When a
     * file is downloaded, it's placed in its repo, with its full path.
     *
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param path     the path
     * @return the local repo file
     * @throws RuntimeException the runtime exception
     */
    public File getLocalRepoFile(String repoName, String repoID, String path) throws RuntimeException {
        String localPath = Utils.pathJoin(getRepoDir(repoName, repoID), path);
        File parentDir = new File(Utils.getParentPath(localPath));
        if (!parentDir.exists()) {
            // TODO should check if the directory creation succeeds
            parentDir.mkdirs();
        }

        return new File(localPath);
    }

    private List<SeafRepo> parseRepos(String json) {
        try {
            // may throw ClassCastException
            JSONArray array = Utils.parseJsonArray(json);
            if (array.length() == 0)
                return Lists.newArrayListWithCapacity(0);

            ArrayList<SeafRepo> repos = Lists.newArrayList();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                SeafRepo repo = SeafRepo.fromJson(obj);
                if (repo != null)
                    repos.add(repo);
            }
            return repos;
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, "parse json error");
            return null;
        } catch (Exception e) {
            // other exception, for example ClassCastException
            Log.e(DEBUG_TAG, "parseRepos exception");
            return null;
        }
    }

    private SeafRepoEncrypt parseRepoEncrypt(String json) {
        try {
            JSONObject object = Utils.parseJsonObject(json);
            return SeafRepoEncrypt.fromJson(object);
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, "parse json error");
            return null;
        } catch (Exception e) {
            // other exception, for example ClassCastException
            Log.e(DEBUG_TAG, "parseRepos exception");
            return null;
        }
    }


    /**
     * Gets block path by id.
     *
     * @param blkId the blk id
     * @return the block path by id
     */
    public String getBlockPathById(String blkId) {
        final File block = getFileForBlockCache(blkId);
        return block.getAbsolutePath();
    }

    /**
     * Gets cached repo by id.
     *
     * @param id the id
     * @return the cached repo by id
     */
    public SeafRepo getCachedRepoByID(String id) {
        List<SeafRepo> cachedRepos = getReposFromCache();
        if (cachedRepos == null) {
            return null;
        }

        for (SeafRepo repo: cachedRepos) {
            if (repo.getID().equals(id)) {
                return repo;
            }
        }

        return null;
    }

    /**
     * Gets cached repo encrypt by id.
     *
     * @param id the id
     * @return the cached repo encrypt by id
     */
    public SeafRepoEncrypt getCachedRepoEncryptByID(String id) {
        File cache = getFile4RepoCache(id);
        if (cache.exists()) {
            String json = Utils.readFile(cache);
            if (!TextUtils.isEmpty(json)) {
                return parseRepoEncrypt(json);
            }
        }
        return null;
    }


    /**
     * Gets repos from cache.
     *
     * @return the repos from cache
     */
    public List<SeafRepo> getReposFromCache() {
        if (reposCache != null)
            return reposCache;

        File cache = getFileForReposCache();
        if (cache.exists()) {
            String json = Utils.readFile(cache);
            if (json == null) {
                return null;
            }
            reposCache = parseRepos(json);
            return reposCache;
        }
        return null;
    }

    /**
     * Gets repos from server.
     *
     * @return the repos from server
     * @throws SeafException the seaf exception
     */
    public List<SeafRepo> getReposFromServer() throws SeafException {
        // First decide if use cache
        if (!Utils.isNetworkOn()) {
            throw SeafException.networkException;
        }

        String json = sc.getRepos();
        //Log.d(DEBUG_TAG, "get repos from server " + json);
        if (json == null)
            return null;

        reposCache = parseRepos(json);

        try {
            File cache = getFileForReposCache();
            Utils.writeFile(cache, json);
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "Could not write repo cache to disk.", e);
        }

        return reposCache;
    }

    private void getEncryptRepo(String repoID) throws SeafException {
        String json = sc.getEncryptRepo(repoID);
        //Save to Cache
        if (!TextUtils.isEmpty(json)) {
            try {
                File cache = getFile4RepoCache(repoID);
                Utils.writeFile(cache, json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveDirentContent(String repoID, String parentDir, String dirID, String content) {
        deleteOldDirentContent(repoID, parentDir);
        dbHelper.saveDirents(repoID, parentDir, dirID);

        try {
            File cache = getFileForDirentCache(dirID);
            Utils.writeFile(cache, content);
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "Could not write dirent cache to disk.", e);
        }
    }

    /**
     * Clean up old dirent cache for a directory where we have received new data.
     *
     * @param repoID
     * @param dir
     */
    private void deleteOldDirentContent(String repoID, String dir) {
        String dirID = dbHelper.getCachedDirents(repoID, dir);

        // identical directory content results in same dirID. So check if whether
        // the dirID is referenced multiple times before deleting it.
        if (dirID != null && dbHelper.getCachedDirentUsage(dirID) <= 1) {
            File file = getFileForDirentCache(dirID);
            file.delete();
        }
        // and finally delete the entry in the SQL table
        dbHelper.removeCachedDirents(repoID, dir);
    }

    /**
     * Gets file.
     *
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param path     the path
     * @param monitor  the monitor
     * @return the file
     * @throws SeafException the seaf exception
     */
    public synchronized File getFile(String repoName, String repoID, String path,
                                     ProgressMonitor monitor) throws SeafException {

        String cachedFileID = null;
        SeafCachedFile cf = getCachedFile(repoName, repoID, path);
        File localFile = getLocalRepoFile(repoName, repoID, path);
        // If local file is up to date, show it
        if (cf != null) {
            if (localFile.exists()) {
                cachedFileID = cf.fileID;
            }
        }

        Pair<String, File> ret = sc.getFile(repoID, path, localFile.getPath(), cachedFileID, monitor);

        String fileID = ret.first;
        if (fileID.equals(cachedFileID)) {
            // cache is valid
            return localFile;
        } else {
            File file = ret.second;
            addCachedFile(repoName, repoID, path, fileID, file);
            return file;
        }
    }

    /**
     * Gets file by blocks.
     *
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param path     the path
     * @param fileSize the file size
     * @param monitor  the monitor
     * @return the file by blocks
     * @throws SeafException            the seaf exception
     * @throws IOException              the io exception
     * @throws JSONException            the json exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public synchronized File getFileByBlocks(String repoName, String repoID, String path, long fileSize,
                        ProgressMonitor monitor) throws SeafException, IOException, JSONException, NoSuchAlgorithmException {

        String cachedFileID = null;
        SeafCachedFile cf = getCachedFile(repoName, repoID, path);
        File localFile = getLocalRepoFile(repoName, repoID, path);
        // If local file is up to date, show it
        if (cf != null) {
            if (localFile.exists()) {
                cachedFileID = cf.fileID;
            }
        }

        final String json = sc.getBlockDownloadList(repoID, path);
        JSONObject obj = new JSONObject(json);
        FileBlocks fileBlocks = FileBlocks.fromJson(obj);

        if (fileBlocks.fileID.equals(cachedFileID)) {
            // cache is valid
            Log.d(DEBUG_TAG, "cache is valid");
            return localFile;
        }

        final Pair<String, String> pair = getRepoEncKey(repoID);
        if (pair == null) {
            throw SeafException.decryptException;
        }
        final String encKey = pair.first;
        final String encIv = pair.second;
        if (TextUtils.isEmpty(encKey) || TextUtils.isEmpty(encIv)) {
            throw SeafException.decryptException;
        }

        if (fileBlocks.blocks == null) {
            if (!localFile.createNewFile()) {
                Log.w(DEBUG_TAG, "Failed to create file " + localFile.getName());
                return null;
            }
            Log.d(DEBUG_TAG, String.format("addCachedFile repoName %s, repoId %s, path %s, fileId %s", repoName, repoID, path, fileBlocks.fileID));
            addCachedFile(repoName, repoID, path, fileBlocks.fileID, localFile);
            return localFile;
        }
        byte[] tag = new byte[1];



        try {
            SecretStream.State state2 = null;
            boolean first = true;

            for (Block blk : fileBlocks.blocks) {
                File tempBlock = new File(storageManager.getTempDir(), blk.blockId);
                final Pair<String, File> block = sc.getBlock(repoID, fileBlocks, blk.blockId, tempBlock.getPath(), fileSize, monitor);
                final byte[] bytes = FileUtils.readFileToByteArray(block.second);
                if (first){
                    byte[] header = bytes;
                    state2 = lazySodium.cryptoSecretStreamInitPull(header, Key.fromBytes(encKey.getBytes()));
                    first = false;
                }else {
                    String decryptedMessage = lazySodium.cryptoSecretStreamPull(state2, new String(bytes), tag);
                    final byte[] decryptedBlock = Crypto.decrypt(bytes, encKey, encIv);
                    FileUtils.writeByteArrayToFile(localFile, decryptedBlock, true);
                }
            }
        } catch(SodiumException e){
            e.printStackTrace();
        }




        Log.d(DEBUG_TAG, String.format("addCachedFile repoName %s, repoId %s, path %s, fileId %s", repoName, repoID, path, fileBlocks.fileID));
        addCachedFile(repoName, repoID, path, fileBlocks.fileID, localFile);
        return localFile;
    }

    private List<SeafDirent> parseDirents(String json) {
        try {
            JSONArray array = Utils.parseJsonArray(json);
            if (array == null)
                return null;

            ArrayList<SeafDirent> dirents = Lists.newArrayList();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                SeafDirent de = SeafDirent.fromJson(obj);
                if (de != null)
                    dirents.add(de);
            }
            return dirents;
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, "Could not parse cached dirent", e);
            return null;
        }
    }

    private List<SeafStarredFile> parseStarredFiles(String json) {
        try {
            JSONArray array = Utils.parseJsonArray(json);
            if (array == null)
                return null;

            ArrayList<SeafStarredFile> starredFiles = Lists.newArrayList();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                SeafStarredFile sf = SeafStarredFile.fromJson(obj);
                if (sf != null)
                    starredFiles.add(sf);
            }
            return starredFiles;
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, "Could not parse cached starred files", e);
            return null;
        }
    }

    /**
     * Gets cached dirents.
     *
     * @param repoID the repo id
     * @param path   the path
     * @return the cached dirents
     */
    public List<SeafDirent> getCachedDirents(String repoID, String path) {
        String dirID = dbHelper.getCachedDirents(repoID, path);
        if (dirID == null) {
            return null;
        }

        File cache = getFileForDirentCache(dirID);
        if (!cache.exists()) {
            return null;
        }

        String json = Utils.readFile(cache);
        if (json == null) {
            return null;
        }

        return parseDirents(json);
    }

    /**
     * In four cases we need to visit the server for dirents
     * <p>
     * 1. No cached dirents
     * 2. User clicks "refresh" button.
     * 3. Download all dirents within a folder
     * 4. View starred or searched files in gallery without available local cache
     * <p>
     * In the second case, the local cache may still be valid.
     *
     * @param repoID the repo id
     * @param path   the path
     * @return the dirents from server
     * @throws SeafException the seaf exception
     */
    public List<SeafDirent> getDirentsFromServer(String repoID, String path) throws SeafException {

        // first fetch our cached dirent and read it
        String cachedDirID = dbHelper.getCachedDirents(repoID, path);
        String cachedContent = null;
        File cacheFile = getFileForDirentCache(cachedDirID);
        if (cacheFile.exists()) {
            cachedContent = Utils.readFile(cacheFile);
        }

        // if that didn't work, then we have no cache.
        if (cachedContent == null) {
            cachedDirID = null;
        }

        // fetch new dirents. ret.second will be null if the cache is still valid
        Pair<String, String> ret = sc.getDirents(repoID, path, cachedDirID);

        String content;
        if (ret.second != null) {
            String dirID = ret.first;
            content = ret.second;
            saveDirentContent(repoID, path, dirID, content);
        } else {
            content = cachedContent;
        }

        return parseDirents(content);
    }

    /**
     * Gets starred files.
     *
     * @return the starred files
     * @throws SeafException the seaf exception
     */
    public List<SeafStarredFile> getStarredFiles() throws SeafException {
        String starredFiles = sc.getStarredFiles();
        Log.v(DEBUG_TAG, "Save starred files: " + starredFiles);
        if (starredFiles == null) {
            return null;
        }
        dbHelper.saveCachedStarredFiles(account, starredFiles);
        return parseStarredFiles(starredFiles);
    }

    /**
     * Gets cached starred files.
     *
     * @return the cached starred files
     */
    public List<SeafStarredFile> getCachedStarredFiles() {
        String starredFiles = dbHelper.getCachedStarredFiles(account);
        Log.v(DEBUG_TAG, "Get cached starred files: " + starredFiles);
        if (starredFiles == null) {
            return null;
        }
        return parseStarredFiles(starredFiles);
    }


    /**
     * Gets cached file.
     *
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param path     the path
     * @return the cached file
     */
    public SeafCachedFile getCachedFile(String repoName, String repoID, String path) {
        SeafCachedFile cf = dbHelper.getFileCacheItem(repoID, path, this);
        return cf;
    }

    /**
     * Gets cached files.
     *
     * @return the cached files
     */
    public List<SeafCachedFile> getCachedFiles() {
        return dbHelper.getFileCacheItems(this);
    }

    /**
     * Add cached file.
     *
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param path     the path
     * @param fileID   the file id
     * @param file     the file
     */
    public void addCachedFile(String repoName, String repoID, String path, String fileID, File file) {
        if (file == null) {
            return;
        }
        // notify Android Gallery that a new file has appeared

        // file does not always reside in Seadroid directory structure (e.g. camera upload)
        if (file.exists())
            storageManager.notifyAndroidGalleryFileChange(file);

        SeafCachedFile item = new SeafCachedFile();
        item.repoName = repoName;
        item.repoID = repoID;
        item.path = path;
        item.fileID = fileID;
        item.accountSignature = account.getSignature();
        dbHelper.saveFileCacheItem(item, this);
    }

    /**
     * Remove cached file.
     *
     * @param cf the cf
     */
    public void removeCachedFile(SeafCachedFile cf) {
        // TODO should check if the file deletion succeeds
        cf.file.delete();
        dbHelper.deleteFileCacheItem(cf);
    }

    /**
     * Sets password.
     *
     * @param repoID the repo id
     * @param passwd the passwd
     * @throws SeafException the seaf exception
     */
    public void setPassword(String repoID, String passwd) throws SeafException {
        boolean success = sc.setPassword(repoID, passwd);
        //if password is true, to get encrypt repo info
        if (success) {
            getEncryptRepo(repoID);
        }
    }

    /**
     * Upload file.
     *
     * @param repoName      the repo name
     * @param repoID        the repo id
     * @param dir           the dir
     * @param filePath      the file path
     * @param monitor       the monitor
     * @param isUpdate      the is update
     * @param isCopyToLocal the is copy to local
     * @throws SeafException the seaf exception
     * @throws IOException   the io exception
     */
    public void uploadFile(String repoName, String repoID, String dir, String filePath,
                           ProgressMonitor monitor, boolean isUpdate, boolean isCopyToLocal) throws SeafException, IOException {
        uploadFileCommon(repoName, repoID, dir, filePath, monitor, isUpdate, isCopyToLocal);
    }

    private void uploadFileCommon(String repoName, String repoID, String dir,
                                  String filePath, ProgressMonitor monitor,
                                  boolean isUpdate, boolean isCopyToLocal) throws SeafException, IOException {
        String newFileID  = sc.uploadFile(repoID, dir, filePath, monitor,isUpdate);
        if (newFileID == null || newFileID.length() == 0) {
            return;
        }

        File srcFile = new File(filePath);
        String path = Utils.pathJoin(dir, srcFile.getName());
        File fileInRepo = null;
        try {
            fileInRepo = getLocalRepoFile(repoName, repoID, path);
        } catch (RuntimeException e) {
            e.printStackTrace();
            new SeafException(SeafException.OTHER_EXCEPTION, e.getMessage());
        }

        if (isCopyToLocal) {
            if (!isUpdate) {
                // Copy the uploaded file to local repo cache
                try {
                    Utils.copyFile(srcFile, fileInRepo);
                } catch (IOException e) {
                    return;
                }
            }
        }
        // Update file cache entry
        addCachedFile(repoName, repoID, path, newFileID, fileInRepo);
    }

    /**
     * Create new repo.
     *
     * @param repoName the repo name
     * @param password the password
     * @throws SeafException the seaf exception
     */
    public void createNewRepo(String repoName, String password) throws SeafException {
        sc.createNewRepo(repoName, "", password);
    }

    /**
     * Create new dir.
     *
     * @param repoID    the repo id
     * @param parentDir the parent dir
     * @param dirName   the dir name
     * @throws SeafException the seaf exception
     */
    public void createNewDir(String repoID, String parentDir, String dirName) throws SeafException {
        Pair<String, String> ret = sc.createNewDir(repoID, parentDir, dirName);
        if (ret == null) {
            return;
        }

        String newDirID = ret.first;
        String response = ret.second;

        // The response is the dirents of the parentDir after creating
        // the new dir. We save it to avoid request it again
        saveDirentContent(repoID, parentDir, newDirID, response);
    }

    /**
     * Create new file.
     *
     * @param repoID    the repo id
     * @param parentDir the parent dir
     * @param fileName  the file name
     * @throws SeafException the seaf exception
     */
    public void createNewFile(String repoID, String parentDir, String fileName) throws SeafException {
        Pair<String, String> ret = sc.createNewFile(repoID, parentDir, fileName);
        if (ret == null) {
            return;
        }

        String newDirID = ret.first;
        String response = ret.second;

        // The response is the dirents of the parentDir after creating
        // the new file. We save it to avoid request it again
        saveDirentContent(repoID, parentDir, newDirID, response);
    }

    /**
     * Gets local cached file.
     *
     * @param repoName the repo name
     * @param repoID   the repo id
     * @param filePath the file path
     * @param fileID   the file id
     * @return the local cached file
     */
    public File getLocalCachedFile(String repoName, String repoID, String filePath, String fileID) {
        File localFile = getLocalRepoFile(repoName, repoID, filePath);
        if (!localFile.exists()) {
            return null;
        }

        if (!Utils.isNetworkOn()) {
            return localFile;
        }

        SeafCachedFile cf = getCachedFile(repoName, repoID, filePath);
        if (cf != null && cf.fileID != null && cf.fileID.equals(fileID)) {
            return localFile;
        } else {
            return null;
        }
    }

    /**
     * Rename repo.
     *
     * @param repoID  the repo id
     * @param newName the new name
     * @throws SeafException the seaf exception
     */
    public void renameRepo(String repoID, String newName) throws SeafException {
        sc.renameRepo(repoID, newName);
    }

    /**
     * Delete repo.
     *
     * @param repoID the repo id
     * @throws SeafException the seaf exception
     */
    public void deleteRepo(String repoID) throws SeafException {
        sc.deleteRepo(repoID);
    }

    /**
     * Star.
     *
     * @param repoID the repo id
     * @param path   the path
     * @throws SeafException the seaf exception
     */
    public void star(String repoID, String path) throws SeafException {
        sc.star(repoID, path);
    }

    /**
     * Unstar.
     *
     * @param repoID the repo id
     * @param path   the path
     * @throws SeafException the seaf exception
     */
    public void unstar(String repoID, String path) throws SeafException {
        sc.unstar(repoID, path);
    }

    /**
     * Rename.
     *
     * @param repoID  the repo id
     * @param path    the path
     * @param newName the new name
     * @param isdir   the isdir
     * @throws SeafException the seaf exception
     */
    public void rename(String repoID, String path, String newName, boolean isdir) throws SeafException {
        Pair<String, String> ret = sc.rename(repoID, path, newName, isdir);
        if (ret == null) {
            return;
        }

        String newDirID = ret.first;
        String response = ret.second;

        // The response is the dirents of the parentDir after renaming the
        // file/folder. We save it to avoid request it again.
        saveDirentContent(repoID, Utils.getParentPath(path), newDirID, response);

        // TODO: delete or rename cached files, dirent cache, etc.
        /*
         * I think it is more simple and easier if we provide a "clear cache" button in Settings,
         * just like what we have done with thumbnail caches.
         * And hopefully it already exist.
         * Users can manually clear them if they are boring with those temp files.
         */
    }

    /**
     * Delete share link boolean.
     *
     * @param token the token
     * @return the boolean
     */
    public boolean deleteShareLink(String token) {
        try {
            return sc.deleteShareLink(token);
        } catch (SeafException e) {
            e.printStackTrace();
            return  false;
        }
    }


    /**
     * Gets share link.
     *
     * @param repoID the repo id
     * @param path   the path
     * @return the share link
     */
    public ArrayList<SeafLink> getShareLink(String repoID, String path) {
        ArrayList<SeafLink> list = Lists.newArrayListWithCapacity(0);
        try {
            String json = sc.getShareLink(repoID, path);
            if (json != null) {
                JSONArray jsonArray = Utils.parseJsonArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = null;
                    object = (JSONObject) jsonArray.get(i);
                    SeafLink seafLink = SeafLink.fromJson(object);
                    list.add(seafLink);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * Delete.
     *
     * @param repoID the repo id
     * @param path   the path
     * @param isdir  the isdir
     * @throws SeafException the seaf exception
     */
    public void delete(String repoID, String path, boolean isdir) throws SeafException{
        Pair<String, String> ret = sc.delete(repoID, path, isdir);
        if (ret == null){
            return;
        }

        String newDirID = ret.first;
        String response = ret.second;

        // The response is the dirents of the parentDir after deleting the
        // file/folder. We save it to avoid request it again
        saveDirentContent(repoID, Utils.getParentPath(path), newDirID, response);

        // TODO: isdir==true: recursively delete cached files, dirent cache, etc.
        /*
         * I think it is more simple and easier if we provide a "clear cache" button in Settings,
         * just like what we have done with thumbnail caches.
         * And hopefully it already exist.
         * Users can manually clear them if they are boring with those temp files.
         */

    }

    /**
     * Copy.
     *
     * @param srcRepoId the src repo id
     * @param srcDir    the src dir
     * @param srcFn     the src fn
     * @param dstRepoId the dst repo id
     * @param dstDir    the dst dir
     * @throws SeafException the seaf exception
     */
    public void copy(String srcRepoId, String srcDir, String srcFn,
                     String dstRepoId, String dstDir) throws SeafException {
        sc.copy(srcRepoId, srcDir, srcFn, dstRepoId, dstDir);

        // After copying, we need to refresh the destination list
        getDirentsFromServer(dstRepoId, dstDir);
    }

    /**
     * Move.
     *
     * @param srcRepoId the src repo id
     * @param srcDir    the src dir
     * @param srcFn     the src fn
     * @param dstRepoId the dst repo id
     * @param dstDir    the dst dir
     * @param batch     the batch
     * @throws SeafException the seaf exception
     */
    public void move(String srcRepoId, String srcDir, String srcFn, String dstRepoId, String dstDir,
                     boolean batch) throws SeafException {
        Pair<String, String> ret = null;
        if (batch) {
            sc.move(srcRepoId, srcDir, srcFn, dstRepoId, dstDir);
        } else {
            String srcPath = Utils.pathJoin(srcDir, srcFn);
            ret = sc.move(srcRepoId, srcPath, dstRepoId, dstDir);
        }

        // After moving, we need to refresh the destination list
        getDirentsFromServer(dstRepoId, dstDir);

        // We also need to refresh the original list
        getDirentsFromServer(srcRepoId, srcDir);

        if (ret == null) {
            return;
        }

        String newDirID = ret.first;
        String response = ret.second;

        // The response is the list of dst after moving the
        // file/folder. We save it to avoid request it again
        saveDirentContent(dstRepoId, dstDir, newDirID, response);

    }

    /**
     * Gets events.
     *
     * @param start          the start
     * @param useNewActivity the use new activity
     * @return the events
     * @throws SeafException the seaf exception
     */
    public SeafActivities getEvents(int start, boolean useNewActivity) throws SeafException {
        int moreOffset = 0;
        boolean more;
        if (!Utils.isNetworkOn()) {
            throw SeafException.networkException;
        }

        final String json = sc.getEvents(start, useNewActivity);

        if (json == null) return null;

        final List<SeafEvent> events = parseEvents(json);
        final JSONObject object = Utils.parseJsonObject(json);
        if (useNewActivity) {
            if (events.size() < PAGE_SIZE) {
                more = false;
            } else {
                moreOffset = start + 1;
                more = true;
            }
        } else {
            moreOffset = object.optInt("more_offset");
            more = object.optBoolean("more");
        }

        return new SeafActivities(events, moreOffset, more);

    }

    /**
     * Gets history changes.
     *
     * @param repoId   the repo id
     * @param commitId the commit id
     * @return the history changes
     * @throws SeafException the seaf exception
     */
    public String getHistoryChanges(String repoId, String commitId) throws SeafException {
        return sc.getHistoryChanges(repoId, commitId);
    }

    /**
     * Parse events list.
     *
     * @param json the json
     * @return the list
     */
    public List<SeafEvent> parseEvents(String json) {
        try {
            // may throw ClassCastException
            JSONArray array = Utils.parseJsonArrayByKey(json, "events");
            if (array.length() == 0)
                return Lists.newArrayListWithCapacity(0);

            ArrayList<SeafEvent> events = Lists.newArrayList();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                SeafEvent event = SeafEvent.fromJson(obj);
                if (event != null)
                    events.add(event);
            }
            return events;
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, "parse json error");
            return null;
        } catch (Exception e) {
            // other exception, for example ClassCastException
            Log.e(DEBUG_TAG, "parseEvents exception");
            return null;
        }
    }

    /**
     * Clear password.
     */
    public static void clearPassword() {
        passwords.clear();
    }

    /**
     * Complete remote wipe.
     *
     * @throws SeafException the seaf exception
     */
    public void completeRemoteWipe() throws SeafException {
        sc.completeRemoteWipe(account.token);
    }

    private static class PasswordInfo {
        /**
         * The Password.
         */
        String password; // password or encKey
        /**
         * The Timestamp.
         */
        long timestamp;

        /**
         * Instantiates a new Password info.
         *
         * @param password  the password
         * @param timestamp the timestamp
         */
        public PasswordInfo(String password, long timestamp) {
            this.password = password;
            this.timestamp = timestamp;
        }
    }

    /**
     * Gets repo password set.
     *
     * @param repoID the repo id
     * @return the repo password set
     */
    public boolean getRepoPasswordSet(String repoID) {
        final SeafRepoEncrypt seafRepo = getCachedRepoEncryptByID(repoID);
        if (seafRepo != null && seafRepo.canLocalDecrypt()) {
            Pair<String, String> info = dbHelper.getEnckey(repoID);
            return info != null
                    && !TextUtils.isEmpty(info.first)
                    && !TextUtils.isEmpty(info.second);
        }

        PasswordInfo passwordInfo = passwords.get(repoID);
        if (passwordInfo == null) {
            return false;
        }

        if (Utils.now() - passwordInfo.timestamp > SET_PASSWORD_INTERVAL) {
            return false;
        }

        return true;
    }

    /**
     * Sets repo password set.
     *
     * @param repoID the repo id
     * @param key    the key
     * @param iv     the iv
     */
    public void setRepoPasswordSet(String repoID, String key, String iv) {
        if (!TextUtils.isEmpty(repoID)
                && !TextUtils.isEmpty(key)
                && !TextUtils.isEmpty(iv)) {
            dbHelper.saveEncKey(key, iv, repoID);
        }
    }

    /**
     * Sets repo password set.
     *
     * @param repoID   the repo id
     * @param password the password
     */
    public void setRepoPasswordSet(String repoID, String password) {
        passwords.put(repoID, new PasswordInfo(password, Utils.now()));
    }

    /**
     * Gets repo password.
     *
     * @param repoID the repo id
     * @return the repo password
     */
    public String getRepoPassword(String repoID) {
        if (repoID == null) {
            return null;
        }

        final SeafRepoEncrypt seafRepo = getCachedRepoEncryptByID(repoID);
        if (seafRepo != null && seafRepo.canLocalDecrypt()) {
            final Pair<String, String> pair = dbHelper.getEnckey(repoID);
            if (pair == null)
                return null;
            else
                return pair.first;
        }

        PasswordInfo info = passwords.get(repoID);
        if (info == null) {
            return null;
        }

        return info.password;
    }

    private Pair<String, String> getRepoEncKey(String repoID) {
        if (repoID == null) {
            return null;
        }

        return dbHelper.getEnckey(repoID);
    }

    /**
     * calculate if refresh time is expired, the expiration is 10 mins
     *
     * @return the boolean
     */
    public boolean isReposRefreshTimeout() {
        if (Utils.now() < repoRefreshTimeStamp + REFRESH_EXPIRATION_MSECS) {
            return false;
        }

        return true;
    }

    /**
     * Is dirents refresh timeout boolean.
     *
     * @param repoID the repo id
     * @param path   the path
     * @return the boolean
     */
    public boolean isDirentsRefreshTimeout(String repoID, String path) {
        if (!direntsRefreshTimeMap.containsKey(Utils.pathJoin(repoID, path))) {
            return true;
        }
        long lastRefreshTime = direntsRefreshTimeMap.get(Utils.pathJoin(repoID, path));

        if (Utils.now() < lastRefreshTime + REFRESH_EXPIRATION_MSECS) {
            return false;
        }
        return true;
    }

    /**
     * Is starred files refresh timeout boolean.
     *
     * @return the boolean
     */
    public boolean isStarredFilesRefreshTimeout() {
        if (!direntsRefreshTimeMap.containsKey(PULL_TO_REFRESH_LAST_TIME_FOR_STARRED_FRAGMENT)) {
            return true;
        }
        long lastRefreshTime = direntsRefreshTimeMap.get(PULL_TO_REFRESH_LAST_TIME_FOR_STARRED_FRAGMENT);

        if (Utils.now() < lastRefreshTime + REFRESH_EXPIRATION_MSECS) {
            return false;
        }
        return true;
    }

    /**
     * Sets dirs refresh time stamp.
     *
     * @param repoID the repo id
     * @param path   the path
     */
    public void setDirsRefreshTimeStamp(String repoID, String path) {
        direntsRefreshTimeMap.put(Utils.pathJoin(repoID, path), Utils.now());
    }

    /**
     * Sets repos refresh time stamp.
     */
    public void setReposRefreshTimeStamp() {
        repoRefreshTimeStamp = Utils.now();
    }

    /**
     * Save last pull to refresh time.
     *
     * @param lastUpdateTime the last update time
     * @param whichFragment  the which fragment
     */
    public void saveLastPullToRefreshTime(long lastUpdateTime, String whichFragment) {
        direntsRefreshTimeMap.put(whichFragment, lastUpdateTime);
    }

    /**
     * Gets last pull to refresh time.
     *
     * @param whichFragment the which fragment
     * @return the last pull to refresh time
     */
    public String getLastPullToRefreshTime(String whichFragment) {

        if (!direntsRefreshTimeMap.containsKey(whichFragment)) {
            return null;
        }

        Long objLastUpdate = direntsRefreshTimeMap.get(whichFragment);
        if (objLastUpdate == null) return null;

        long lastUpdate = direntsRefreshTimeMap.get(whichFragment);

        long diffTime = new Date().getTime() - lastUpdate;
        int seconds = (int) (diffTime / 1000);
        if (diffTime < 0) {
            return null;
        }
        if (seconds <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(SeadroidApplication.getAppContext().getString(R.string.pull_to_refresh_last_update));

        if (seconds < 60) {
            sb.append(SeadroidApplication.getAppContext().getString(R.string.pull_to_refresh_last_update_seconds_ago, seconds));
        } else {
            int minutes = (seconds / 60);
            if (minutes > 60) {
                int hours = minutes / 60;
                if (hours > 24) {
                    Date date = new Date(lastUpdate);
                    sb.append(ptrDataFormat.format(date));
                } else {
                    sb.append(SeadroidApplication.getAppContext().getString(R.string.pull_to_refresh_last_update_hours_ago, hours));
                }

            } else {
                sb.append(SeadroidApplication.getAppContext().getString(R.string.pull_to_refresh_last_update_minutes_ago, minutes));
            }
        }
        return sb.toString();
    }

    /**
     * search on server
     *
     * @param query query text
     * @param page  pass 0 to disable page loading
     * @return json format strings of searched result
     * @throws SeafException the seaf exception
     */
    public String search(String query, int page) throws SeafException {
        String json = sc.searchLibraries(query, page);
        return json;
    }

    /**
     * Parse search result array list.
     *
     * @param json the json
     * @return the array list
     */
    public ArrayList<SearchedFile> parseSearchResult(String json) {
        if (json == null)
            return null;

        try {
            JSONArray array = Utils.parseJsonArrayByKey(json, "results");
            if (array == null)
                return null;

            ArrayList<SearchedFile> searchedFiles = Lists.newArrayList();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                SearchedFile sf = SearchedFile.fromJson(obj);
                if (sf != null)
                    searchedFiles.add(sf);
            }
            return searchedFiles;
        } catch (JSONException e) {
            return null;
        }
    }

    private FileBlocks chunkFile(String encKey, String enkIv, String filePath) throws IOException {

        File file = new File(filePath);
        InputStream in = null;
        DataInputStream dis = null;
        OutputStream out = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        int byteRead;
        byte[] header = lazySodium.randomBytesBuf(SecretStream.HEADERBYTES);
        SecretStream.State state = null;
        String ciphertext = null;
        FileBlocks seafBlock = new FileBlocks();
        try {
            final String hdid = Crypto.sha1(header);
            File blk = new File(storageManager.getTempDir(), hdid);
            out = new FileOutputStream(blk);
            out.write(header);
            out.close();
            Block block = new Block(hdid, blk.getAbsolutePath(), blk.length(), 0L);
            seafBlock.blocks.add(block);
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }


        try {
            state = lazySodium.cryptoSecretStreamInitPush(header, Key.fromPlainString(encKey));

            while((byteRead = dis.read(buffer, 0, BUFFER_SIZE)) != -1){
                if (byteRead < BUFFER_SIZE)
                    ciphertext = lazySodium.cryptoSecretStreamPush(state, new String(buffer), SecretStream.TAG_MESSAGE);
                else
                    ciphertext = lazySodium.cryptoSecretStreamPush(state, new String(buffer), SecretStream.TAG_FINAL);
                final String blkid = Crypto.sha1(ciphertext.getBytes());
                File blk = new File(storageManager.getTempDir(), blkid);
                Block block = new Block(blkid, blk.getAbsolutePath(), blk.length(), 0L);
                seafBlock.blocks.add(block);
                out = new FileOutputStream(blk);
                out.write(ciphertext.getBytes());
                out.close();
            }
        }catch(SodiumException e){
            e.printStackTrace();
        }catch (NoSuchAlgorithmException f){
            f.printStackTrace();
        }
        return seafBlock;


        /*File file = new File(filePath);
        InputStream in = null;
        DataInputStream dis;
        OutputStream out = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        FileBlocks seafBlock = new FileBlocks();
        try {
            in = new FileInputStream(file);
            dis = new DataInputStream(in);

            // Log.d(DEBUG_TAG, "file size " + file.length());
            int byteRead;
            while ((byteRead = dis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                byte[] cipher;
                if (byteRead < BUFFER_SIZE)
                    cipher = Crypto.encrypt(buffer, byteRead, encKey, enkIv);
                else
                    cipher = Crypto.encrypt(buffer, encKey, enkIv);

                final String blkid = Crypto.sha1(cipher);
                File blk = new File(storageManager.getTempDir(), blkid);
                Block block = new Block(blkid, blk.getAbsolutePath(), blk.length(), 0L);
                seafBlock.blocks.add(block);
                out = new FileOutputStream(blk);
                out.write(cipher);
                out.close();
            }

            in.close();

            return seafBlock;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (out != null) out.close();
            if (in != null) in.close();
        }*/
    }

    /**
     * Upload by blocks.
     *
     * @param repoName      the repo name
     * @param repoId        the repo id
     * @param dir           the dir
     * @param filePath      the file path
     * @param monitor       the monitor
     * @param isUpdate      the is update
     * @param isCopyToLocal the is copy to local
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws IOException              the io exception
     * @throws SeafException            the seaf exception
     */
    public void uploadByBlocks(String repoName, String repoId, String dir,
                               String filePath, ProgressMonitor monitor,
                               boolean isUpdate, boolean isCopyToLocal) throws NoSuchAlgorithmException, IOException, SeafException {
        uploadByBlocksCommon(repoName, repoId, dir, filePath, monitor, isUpdate, isCopyToLocal);
    }

    private void uploadByBlocksCommon(String repoName, String repoID, String dir, String filePath,
                                      ProgressMonitor monitor, boolean isUpdate, boolean isCopyToLocal) throws NoSuchAlgorithmException, IOException, SeafException {


        final Pair<String, String> pair = getRepoEncKey(repoID);
        final String encKey = pair.first;
        final String encIv = pair.second;
        // Log.d(DEBUG_TAG, "encKey " + encKey + " encIv " + encIv);
        if (TextUtils.isEmpty(encKey) || TextUtils.isEmpty(encIv)) {
            // TODO calculate them and continue
            throw SeafException.encryptException;
        }

        final FileBlocks chunkFile = chunkFile(encKey, encIv, filePath);
        if (chunkFile.blocks.isEmpty()) {
            throw SeafException.blockListNullPointerException;
        }

        String newFileID = sc.uploadByBlocks(repoID, dir, filePath, chunkFile.blocks, isUpdate, monitor);
        // Log.d(DEBUG_TAG, "uploadByBlocks " + newFileID);

        if (newFileID == null || newFileID.length() == 0) {
            return;
        }

        File srcFile = new File(filePath);
        String path = Utils.pathJoin(dir, srcFile.getName());
        File fileInRepo = getLocalRepoFile(repoName, repoID, path);

        if (isCopyToLocal) {
            if (!isUpdate) {
                // Copy the uploaded file to local repo cache
                try {
                    Utils.copyFile(srcFile, fileInRepo);
                } catch (IOException e) {
                    return;
                }
            }
        }
        // Update file cache entry
        addCachedFile(repoName, repoID, path, newFileID, fileInRepo);
    }
}
