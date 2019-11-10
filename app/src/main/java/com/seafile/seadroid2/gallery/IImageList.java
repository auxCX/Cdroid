package com.seafile.seadroid2.gallery;

import android.net.Uri;

import java.util.HashMap;

//
// ImageList and Image classes have one-to-one correspondence.
// The class hierarchy (* = abstract class):
//
//    IImageList
//    - BaseImageList (*)
//      - VideoList
//      - ImageList
//        - DrmImageList
//    - SingleImageList (contains UriImage)
//    - ImageListUber
//
//    IImage
//    - BaseImage (*)
//      - VideoObject
//      - Image
//        - DrmImage
//    - UriImage
//

/**
 * The interface of all image collections used in gallery.
 */
public interface IImageList {
    /**
     * Gets bucket ids.
     *
     * @return the bucket ids
     */
    HashMap<String, String> getBucketIds();

    /**
     * Returns the count of image objects.
     *
     * @return the number of images
     */
    int getCount();

    /**
     * Is empty boolean.
     *
     * @return true if the count of image objects is zero.
     */
    boolean isEmpty();

    /**
     * Returns the image at the ith position.
     *
     * @param i the position
     * @return the image at the ith position
     */
    IImage getImageAt(int i);

    /**
     * Returns the image with a particular Uri.
     *
     * @param uri the uri
     * @return the image with a particular Uri. null if not found.
     */
    IImage getImageForUri(Uri uri);

    /**
     * Remove image boolean.
     *
     * @param image the image
     * @return true if the image was removed.
     */
    boolean removeImage(IImage image);

    /**
     * Removes the image at the ith position.
     *
     * @param i the position
     * @return the boolean
     */
    boolean removeImageAt(int i);

    /**
     * Gets image index.
     *
     * @param image the image
     * @return the image index
     */
    int getImageIndex(IImage image);

    /**
     * Closes this list to release resources, no further operation is allowed.
     */
    void close();
}
