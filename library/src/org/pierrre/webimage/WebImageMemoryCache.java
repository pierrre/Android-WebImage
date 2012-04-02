package org.pierrre.webimage;

import org.pierrre.webimage.util.LruCache;

import android.graphics.Bitmap;

public class WebImageMemoryCache {
	private static final double DEFAULT_CACHE_SIZE_QUOTA = 1.0 / 8.0;
	
	private LruCache<String, Bitmap> bitmaps;
	
	public WebImageMemoryCache() {
		this.initBitmaps(this.getDefaultMaxSize());
	}
	
	public synchronized void set(String url, Bitmap bitmap) {
		this.bitmaps.put(url, bitmap);
	}
	
	public synchronized Bitmap get(String url) {
		return this.bitmaps.get(url);
	}
	
	public synchronized void clear() {
		this.bitmaps.evictAll();
	}
	
	public synchronized void setMaxSize(int maxSize) {
		this.clear();
		
		this.initBitmaps(maxSize);
	}
	
	public synchronized int getMaxSize() {
		return this.bitmaps.maxSize();
	}
	
	private int getDefaultMaxSize() {
		return (int) (Runtime.getRuntime().maxMemory() * WebImageMemoryCache.DEFAULT_CACHE_SIZE_QUOTA);
	}
	
	private void initBitmaps(int maxSize) {
		this.bitmaps = new UrlBitmapLruCache(maxSize);
	}
	
	private static class UrlBitmapLruCache extends LruCache<String, Bitmap> {
		public UrlBitmapLruCache(int maxSize) {
			super(maxSize);
		}
		
		@Override
		protected int sizeOf(String url, Bitmap bitmap) {
			return bitmap.getRowBytes() * bitmap.getHeight(); // Do the same thing as bitmap.getByteCount() (which is not available before API 12)
		}
	}
}
