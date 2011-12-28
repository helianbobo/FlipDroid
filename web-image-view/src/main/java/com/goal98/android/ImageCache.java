/* Copyright (c) 2009 Matthias Kaeppler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goal98.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.goal98.android.CacheHelper;

import java.io.*;

/**
 * Implements a cache capable of caching it.tika.mongodb.image files. It exposes helper methods to immediately
 * access binary it.tika.mongodb.image data as {@link android.graphics.Bitmap} objects.
 *
 * @author Matthias Kaeppler
 */
public class ImageCache extends com.goal98.android.AbstractCache<String, byte[]> {

    public ImageCache(int initialCapacity, long expirationInMinutes, int maxConcurrentThreads) {
        super("ImageCache", initialCapacity, expirationInMinutes, maxConcurrentThreads);
    }

    public synchronized void removeAllWithPrefix(String urlPrefix) {
        CacheHelper.removeAllWithStringPrefix(this, urlPrefix);
    }

    @Override
    public String getFileNameForKey(String imageUrl) {
        return CacheHelper.getFileNameFromUrl(imageUrl);
    }

    @Override
    protected byte[] readValueFromDisk(File file) throws IOException {
        BufferedInputStream istream = new BufferedInputStream(new FileInputStream(file));
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException("Cannot read files larger than " + Integer.MAX_VALUE + " bytes");
        }

        int imageDataLength = (int) fileSize;

        byte[] imageData = new byte[imageDataLength];
        istream.read(imageData, 0, imageDataLength);
        istream.close();

        return imageData;
    }

    public synchronized byte[] getBitmapBytes(Object elementKey) {
        byte[] imageData = super.get(elementKey);
        if (imageData == null) {
            return null;
        }
//        try {
//            final Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
//            return bitmap;
//        } catch (Throwable e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
        return imageData;
    }

    @Override
    protected  void writeValueToDisk(File file, byte[] imageData) throws IOException {
        BufferedOutputStream ostream = new BufferedOutputStream(new FileOutputStream(file));

        ostream.write(imageData);

        ostream.close();
    }
}
