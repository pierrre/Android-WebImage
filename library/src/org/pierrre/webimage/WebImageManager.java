package org.pierrre.webimage;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.http.client.HttpClient;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

public class WebImageManager implements WebImageRequest.Receiver {
	public static final String LOG_TAG = "WebImage";
	public static final String USER_AGENT = "WebImage";
	
	private static WebImageManager instance = null;
	
	private Application application;
	
	private UrlManager urlManager;
	private ReceiverManager receiverManager;
	
	private WebImageMemoryCache memoryCache;
	private WebImageFileCache fileCache;
	
	private HttpClient httpClient;
	
	private boolean memoryCacheEnabled;
	private boolean fileCacheEnabled;
	
	public static WebImageManager getInstance(Context context) {
		if (WebImageManager.instance == null) {
			WebImageManager.instance = WebImageManager.createWebImageManager(context);
		}
		
		return WebImageManager.instance;
	}
	
	private static WebImageManager createWebImageManager(Context context) {
		Context applicationContext = context.getApplicationContext();
		
		if (applicationContext instanceof Application) {
			return new WebImageManager((Application) applicationContext);
		} else {
			throw new RuntimeException("Initialization error");
		}
	}
	
	private WebImageManager(Application application) {
		this.application = application;
		
		this.urlManager = new UrlManager();
		this.receiverManager = new ReceiverManager();
		
		this.memoryCache = new WebImageMemoryCache();
		this.fileCache = new WebImageFileCache(this.application);
		
		this.httpClient = null;
		
		this.memoryCacheEnabled = true;
		this.fileCacheEnabled = true;
	}
	
	public WebImageMemoryCache getMemoryCache() {
		return this.memoryCache;
	}
	
	public void setMemoryCacheEnabled(boolean memoryCacheEnabled) {
		this.memoryCacheEnabled = memoryCacheEnabled;
		
		// Clear the memory cache if it's not used
		if (!memoryCacheEnabled) {
			this.memoryCache.clear();
		}
	}
	
	public boolean isMemoryCacheEnabled() {
		return this.memoryCacheEnabled;
	}
	
	public WebImageFileCache getFileCache() {
		return this.fileCache;
	}
	
	public void setFileCacheEnabled(boolean fileCacheEnabled) {
		this.fileCacheEnabled = fileCacheEnabled;
	}
	
	public boolean isFileCacheEnabled() {
		return this.fileCacheEnabled;
	}
	
	public void clearCache() {
		this.memoryCache.clear();
		this.fileCache.clear();
	}
	
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
	private HttpClient getHttpClient() {
		if (this.httpClient == null) {
			this.httpClient = this.createHttpClient();
		}
		
		return this.httpClient;
	}
	
	private HttpClient createHttpClient() {
		try {
			Class<?> androidHttpClientClass = Class.forName("android.net.http.AndroidHttpClient");
			Method newInstanceMethod = androidHttpClientClass.getMethod("newInstance", String.class);
			
			HttpClient httpClient = (HttpClient) newInstanceMethod.invoke(null, WebImageManager.USER_AGENT);
			
			return httpClient;
		} catch (Exception e) {
			throw new RuntimeException("AndroidHttpClient is not available, please provide a HttpClient with setHttpClient()", e);
		}
	}
	
	public boolean requestWebImage(String url, Receiver receiver) {
		this.removeReceiver(receiver);
		
		if (this.memoryCacheEnabled) {
			Bitmap bitmap = this.memoryCache.get(url);
			
			if (bitmap != null) {
				receiver.onWebImageResult(url, bitmap);
				
				return true;
			}
		}
		
		WebImageRequest request = this.urlManager.getRequest(url);
		
		if (request == null) {
			boolean lastRequestCompleted = this.receiverManager.getLastRequestCompleted(receiver);
			
			request = new WebImageRequest(url, this, this.getHttpClient(), this.fileCacheEnabled ? this.fileCache : null, !lastRequestCompleted);
			request.executeOnThreadPoolExecutor();
			
			this.urlManager.put(url, request);
		}
		
		this.receiverManager.put(receiver, request);
		
		return false;
	}
	
	public void removeReceiver(Receiver receiver) {
		WebImageRequest request = this.receiverManager.remove(receiver);
		
		if (request != null) {
			this.receiverManager.setLastRequestCompleted(receiver, false);
			
			WeakHashMap<Receiver, Object> receivers = this.receiverManager.getReceivers(request);
			
			receivers.remove(receiver);
			
			if (receivers.isEmpty()) {
				request.cancelWebImageRequest();
				
				this.receiverManager.remove(request);
				
				this.urlManager.remove(request);
			}
		}
	}
	
	@Override
	public void onWebImageRequestResult(WebImageRequest request, Bitmap bitmap) {
		String url = this.urlManager.remove(request);
		
		if (url != null && this.memoryCacheEnabled && bitmap != null) {
			this.memoryCache.set(url, bitmap);
		}
		
		WeakHashMap<Receiver, Object> receivers = this.receiverManager.remove(request);
		
		if (receivers != null) {
			Set<Receiver> receiverSet = receivers.keySet();
			
			for (Receiver receiver : receiverSet) {
				this.receiverManager.remove(receiver);
				
				this.receiverManager.setLastRequestCompleted(receiver, true);
				
				if (url != null) {
					receiver.onWebImageResult(url, bitmap);
				}
			}
		}
	}
	
	private static class UrlManager {
		private HashMap<String, WebImageRequest> requestsByUrl;
		private IdentityHashMap<WebImageRequest, String> urlsByRequest;
		
		public UrlManager() {
			this.requestsByUrl = new HashMap<String, WebImageRequest>();
			this.urlsByRequest = new IdentityHashMap<WebImageRequest, String>();
		}
		
		public void put(String url, WebImageRequest request) {
			this.urlsByRequest.put(request, url);
			
			this.requestsByUrl.put(url, request);
		}
		
		public String remove(WebImageRequest request) {
			String url = this.urlsByRequest.remove(request);
			
			if (url != null) {
				this.requestsByUrl.remove(url);
			}
			
			return url;
		}
		
		public WebImageRequest getRequest(String url) {
			return this.requestsByUrl.get(url);
		}
	}
	
	private static class ReceiverManager {
		private WeakHashMap<Receiver, WebImageRequest> requestsByReceiver;
		private IdentityHashMap<WebImageRequest, WeakHashMap<Receiver, Object>> receiversByRequest;
		private Object dummy;
		
		private WeakHashMap<Receiver, Boolean> receiversLastRequestCompleted;
		
		public ReceiverManager() {
			this.requestsByReceiver = new WeakHashMap<Receiver, WebImageRequest>();
			this.receiversByRequest = new IdentityHashMap<WebImageRequest, WeakHashMap<Receiver, Object>>();
			this.dummy = new Object();
			
			this.receiversLastRequestCompleted = new WeakHashMap<Receiver, Boolean>();
		}
		
		public void put(Receiver receiver, WebImageRequest request) {
			WeakHashMap<Receiver, Object> receivers = this.receiversByRequest.get(request);
			
			if (receivers == null) {
				receivers = new WeakHashMap<Receiver, Object>();
				this.receiversByRequest.put(request, receivers);
			}
			
			receivers.put(receiver, this.dummy);
			
			this.requestsByReceiver.put(receiver, request);
		}
		
		public WeakHashMap<Receiver, Object> remove(WebImageRequest request) {
			return this.receiversByRequest.remove(request);
		}
		
		public WebImageRequest remove(Receiver receiver) {
			return this.requestsByReceiver.remove(receiver);
		}
		
		public WeakHashMap<Receiver, Object> getReceivers(WebImageRequest request) {
			return this.receiversByRequest.get(request);
		}
		
		public void setLastRequestCompleted(Receiver receiver, boolean complete) {
			this.receiversLastRequestCompleted.put(receiver, complete);
		}
		
		public boolean getLastRequestCompleted(Receiver receiver) {
			Boolean complete = this.receiversLastRequestCompleted.get(receiver);
			
			if (complete == null) {
				complete = true;
			}
			
			return complete;
		}
	}
	
	public static interface Receiver {
		public void onWebImageResult(String url, Bitmap bitmap);
	}
}
