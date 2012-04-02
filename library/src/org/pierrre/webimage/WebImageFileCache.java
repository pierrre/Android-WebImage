package org.pierrre.webimage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.pierrre.webimage.util.InputStreamHelper;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class WebImageFileCache {
	public static final long DEFAULT_LIFETIME = 1000 * 60 * 60 * 24 * 3; // 3 days
	
	private File cacheDirectory;
	private MessageDigest messageDigest;
	
	private long lifetime;
	
	public WebImageFileCache(Context context) {
		File applicationCacheDirectory;
		try {
			applicationCacheDirectory = (File) Context.class.getMethod("getExternalCacheDir").invoke(context);
		} catch (Exception e) {
			applicationCacheDirectory = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/cache");
		}
		this.cacheDirectory = new File(applicationCacheDirectory, "webimage");
		
		try {
			this.messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 is not supported", e);
		}
		
		this.setLifetime(WebImageFileCache.DEFAULT_LIFETIME);
	}
	
	public boolean set(String url, byte[] data) {
		boolean saved = false;
		
		if (this.isExternalStorageAvailable()) {
			boolean cacheAvailable = true;
			
			if (!this.cacheDirectory.exists()) {
				cacheAvailable = this.cacheDirectory.mkdirs();
			}
			
			if (cacheAvailable) {
				try {
					FileOutputStream fos = new FileOutputStream(this.getCacheFile(url));
					
					try {
						fos.write(data);
						
						saved = true;
					} finally {
						fos.close();
					}
				} catch (IOException e) {
					Log.e(WebImageManager.LOG_TAG, e.getMessage(), e);
				}
			}
		}
		
		return saved;
	}
	
	public byte[] get(String url) {
		byte[] data = null;
		
		if (this.isExternalStorageAvailable()) {
			File file = this.getCacheFile(url);
			
			if (file.exists()) {
				if (System.currentTimeMillis() - file.lastModified() <= this.lifetime) {
					try {
						FileInputStream fis = new FileInputStream(file);
						
						try {
							data = InputStreamHelper.readFully(fis);
						} finally {
							fis.close();
						}
					} catch (IOException e) {
						Log.e(WebImageManager.LOG_TAG, e.getMessage(), e);
					}
				}
			}
		}
		
		return data;
	}
	
	public void clear() {
		if (this.isExternalStorageAvailable()) {
			this.deleteFile(this.cacheDirectory);
		}
	}
	
	public void setLifetime(long lifetime) {
		this.lifetime = lifetime;
	}
	
	public long getLifetime() {
		return this.lifetime;
	}
	
	private boolean deleteFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			
			if (files != null) {
				for (File f : files) {
					this.deleteFile(f);
				}
			}
		}
		
		return file.delete();
	}
	
	private File getCacheFile(String url) {
		return new File(this.cacheDirectory, this.hashUrl(url));
	}
	
	private String hashUrl(String url) {
		synchronized (this.messageDigest) {
			return String.format("%1$032X", new BigInteger(1, this.messageDigest.digest(url.getBytes())));
		}
	}
	
	private boolean isExternalStorageAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
}
