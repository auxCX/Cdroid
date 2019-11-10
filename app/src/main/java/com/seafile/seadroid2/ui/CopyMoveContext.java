package com.seafile.seadroid2.ui;

import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.util.Utils;

import java.util.List;

/**
 * The type Copy move context.
 */
public class CopyMoveContext {
    /**
     * The enum Op.
     */
    public enum OP {
        /**
         * Copy op.
         */
        COPY,
        /**
         * Move op.
         */
        MOVE
    }

    /**
     * The Op.
     */
    public OP op;

    /**
     * The Dirents.
     */
    public List<SeafDirent> dirents;
    /**
     * The Src repo id.
     */
    public String srcRepoId;
    /**
     * The Src repo name.
     */
    public String srcRepoName;
    /**
     * The Src dir.
     */
    public String srcDir;
    /**
     * The Src fn.
     */
    public String srcFn;
    /**
     * The Isdir.
     */
    public boolean isdir;
    /**
     * flag to mark multiple selection & operations
     */
    public boolean batch;

    /**
     * The Dst repo id.
     */
    public String dstRepoId;
    /**
     * The Dst dir.
     */
    public String dstDir;

    /**
     * Constructor for a single file operations
     *
     * @param srcRepoId   the src repo id
     * @param srcRepoName the src repo name
     * @param srcDir      the src dir
     * @param srcFn       the src fn
     * @param isdir       the isdir
     * @param op          the op
     */
    public CopyMoveContext(String srcRepoId, String srcRepoName, String srcDir, String srcFn, boolean isdir, OP op) {
        this.srcRepoId = srcRepoId;
        this.srcRepoName = srcRepoName;
        this.srcDir = srcDir;
        this.srcFn = srcFn;
        this.isdir = isdir;
        this.op = op;
        this.batch = false;
    }

    /**
     * Constructor for multiple files operations
     *
     * @param srcRepoId   the src repo id
     * @param srcRepoName the src repo name
     * @param srcDir      the src dir
     * @param dirents     the dirents
     * @param op          the op
     */
    public CopyMoveContext(String srcRepoId, String srcRepoName, String srcDir, List<SeafDirent> dirents, OP op) {
        this.srcRepoId = srcRepoId;
        this.srcRepoName = srcRepoName;
        this.srcDir = srcDir;
        this.dirents = dirents;
        this.batch = true;
        this.op = op;
    }

    /**
     * Sets dest.
     *
     * @param dstRepoId the dst repo id
     * @param dstDir    the dst dir
     */
    public void setDest(String dstRepoId, String dstDir) {
        this.dstRepoId = dstRepoId;
        this.dstDir = dstDir;
    }

    /**
     * Is copy boolean.
     *
     * @return the boolean
     */
    public boolean isCopy() {
        return op == OP.COPY;
    }

    /**
     * Is move boolean.
     *
     * @return the boolean
     */
    public boolean isMove() {
        return op == OP.MOVE;
    }

    /**
     * Avoid copy/move a folder into its subfolder E.g. situations like:
     * <p>
     * srcDir: /
     * srcFn: dirX
     * dstDir: /dirX/dirY
     *
     * @return the boolean
     */
    public boolean checkCopyMoveToSubfolder() {
        if (isdir && srcRepoId.equals(dstRepoId)) {
            String srcFolder = Utils.pathJoin(srcDir, srcFn);
            return !dstDir.startsWith(srcFolder);
        }
        return true;
    }
}
