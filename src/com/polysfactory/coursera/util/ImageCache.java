
package com.polysfactory.coursera.util;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

public class ImageCache {
    private final static Map<String, SoftReference<Bitmap>> cache = Collections
            .synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());

    public static void put(String key, Bitmap bitmap) {
        cache.put(key, new SoftReference<Bitmap>(bitmap));
    }

    public static Bitmap get(String key) {
        SoftReference<Bitmap> ref = cache.get(key);
        if (ref == null) {
            return null;
        }
        return ref.get();
    }
}
