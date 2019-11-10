package com.seafile.seadroid2.fileschooser;

import java.io.File;
import java.io.FileFilter;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.SeafItem;
import com.seafile.seadroid2.util.Utils;

/**
 * The type Selectable file.
 */
public class SelectableFile implements SeafItem {

    private boolean selected;
    private File file;

    /**
     * Instantiates a new Selectable file.
     *
     * @param path the path
     */
    public SelectableFile(String path) {
        selected = false;
        file = new File(path);
    }

    /**
     * Instantiates a new Selectable file.
     *
     * @param file       the file
     * @param isSelected the is selected
     */
    public SelectableFile(File file, boolean isSelected) {
        this.file = file;
        selected = isSelected;
    }

    /**
     * Sets selected.
     *
     * @param isSelected the is selected
     */
    public void setSelected(boolean isSelected) {
        selected = isSelected;
    }

    /**
     * Is selected boolean.
     *
     * @return the boolean
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Is directory boolean.
     *
     * @return the boolean
     */
    public boolean isDirectory() {
        return file.isDirectory();
    }

    /**
     * Is file boolean.
     *
     * @return the boolean
     */
    public boolean isFile() {
        return file.isFile();
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return file.getName();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof SelectableFile)) {
            return false;
        }

        SelectableFile lhs = (SelectableFile) o;

        return file.equals(lhs.getFile()) && selected == lhs.isSelected();
    }

    /**
     * List files selectable file [ ].
     *
     * @param fileFilter the file filter
     * @return the selectable file [ ]
     */
    public SelectableFile[] listFiles(FileFilter fileFilter) {
        File[] files = file.listFiles(fileFilter);
        SelectableFile[] selectedFiles = new SelectableFile[files.length];
        for (int i = 0; i < files.length; ++i) {
            selectedFiles[i] = new SelectableFile(files[i], false);
        }
        return selectedFiles;
    }

    /**
     * Length long.
     *
     * @return the long
     */
    public long length() {
        return file.length();
    }

    /**
     * Gets absolute path.
     *
     * @return the absolute path
     */
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    /**
     * Gets file.
     *
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * Toggle selected.
     */
    public void toggleSelected() {
        selected = !selected;
    }

    @Override
    public String getTitle() {
        return getName();
    }

    @Override
    public String getSubtitle() {
        String timestamp = Utils.translateCommitTime(file.lastModified());
        if (isDirectory())
            return timestamp;
        return Utils.readableFileSize(file.length()) + ", " + timestamp;
    }

    @Override
    public int getIcon() {
        if (isDirectory())
            return R.drawable.folder;
        return Utils.getFileIcon(getTitle());
    }

}
