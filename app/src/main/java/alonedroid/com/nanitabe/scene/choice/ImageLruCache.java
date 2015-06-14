package alonedroid.com.nanitabe.scene.choice;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

public class ImageLruCache implements ImageLoader.ImageCache {
    private LruCache<String, Bitmap> mMemoryCache;

    public ImageLruCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mMemoryCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mMemoryCache.put(url, bitmap);
    }
}