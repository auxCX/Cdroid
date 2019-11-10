package com.seafile.seadroid2.gallery;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.InputStream;

/**
 * The interface of all images used in gallery.
 */
public interface IImage {
    /**
     * The constant THUMBNAIL_TARGET_SIZE.
     */
    static final int THUMBNAIL_TARGET_SIZE = 320;
    /**
     * The constant MINI_THUMB_TARGET_SIZE.
     */
    static final int MINI_THUMB_TARGET_SIZE = 96;
    /**
     * The constant THUMBNAIL_MAX_NUM_PIXELS.
     */
    static final int THUMBNAIL_MAX_NUM_PIXELS = 512 * 384;
    /**
     * The constant MINI_THUMB_MAX_NUM_PIXELS.
     */
    static final int MINI_THUMB_MAX_NUM_PIXELS = 128 * 128;
    /**
     * The constant UNCONSTRAINED.
     */
    static final int UNCONSTRAINED = -1;

    /**
     * Get the image list which contains this image.  @return the container
     */
    IImageList getContainer();

    /**
     * Get the bitmap for the full size image.  @param minSideLength the min side length
     *
     * @param maxNumberOfPixels the max number of pixels
     * @return the bitmap
     */
    Bitmap fullSizeBitmap(int minSideLength, int maxNumberOfPixels);

    /**
     * Full size bitmap bitmap.
     *
     * @param minSideLength     the min side length
     * @param maxNumberOfPixels the max number of pixels
     * @param rotateAsNeeded    the rotate as needed
     * @param useNative         the use native
     * @return the bitmap
     */
    Bitmap fullSizeBitmap(int minSideLength, int maxNumberOfPixels, boolean rotateAsNeeded, boolean useNative);

    /**
     * Gets degrees rotated.
     *
     * @return the degrees rotated
     */
    int getDegreesRotated();

    /**
     * The constant ROTATE_AS_NEEDED.
     */
    static final boolean ROTATE_AS_NEEDED = true;
    /**
     * The constant NO_ROTATE.
     */
    static final boolean NO_ROTATE = false;
    /**
     * The constant USE_NATIVE.
     */
    static final boolean USE_NATIVE = true;
    /**
     * The constant NO_NATIVE.
     */
    static final boolean NO_NATIVE = false;

    /**
     * Get the input stream associated with a given full size image.  @return the input stream
     */
    InputStream fullSizeImageData();

    /**
     * Full size image uri uri.
     *
     * @return the uri
     */
    Uri fullSizeImageUri();

    /**
     * Get the path of the (full size) image data.  @return the data path
     */
    String getDataPath();

    /**
     * Gets title.
     *
     * @return the title
     */
// Get the title of the image
    String getTitle();

    /**
     * Gets date taken.
     *
     * @return the date taken
     */
// Get metadata of the image
    long getDateTaken();

    /**
     * Gets mime type.
     *
     * @return the mime type
     */
    String getMimeType();

    /**
     * Gets width.
     *
     * @return the width
     */
    int getWidth();

    /**
     * Gets height.
     *
     * @return the height
     */
    int getHeight();

    /**
     * Is readonly boolean.
     *
     * @return the boolean
     */
// Get property of the image
    boolean isReadonly();

    /**
     * Is drm boolean.
     *
     * @return the boolean
     */
    boolean isDrm();

    /**
     * Thumb bitmap bitmap.
     *
     * @param rotateAsNeeded the rotate as needed
     * @return the bitmap
     */
// Get the bitmap of the medium thumbnail
    Bitmap thumbBitmap(boolean rotateAsNeeded);

    /**
     * Mini thumb bitmap bitmap.
     *
     * @return the bitmap
     */
// Get the bitmap of the mini thumbnail.
    Bitmap miniThumbBitmap();

    /**
     * Rotate image by boolean.
     *
     * @param degrees the degrees
     * @return the boolean
     */
// Rotate the image
    boolean rotateImageBy(int degrees);
}
