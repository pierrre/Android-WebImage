package org.pierrre.webimage;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.pierrre.tpeat.ThreadPoolExecutorAsyncTask;
import org.pierrre.webimage.util.InputStreamHelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class WebImageRequest extends ThreadPoolExecutorAsyncTask<Void, Void, Bitmap> {
	private static long HTTP_REQUEST_DELAY = 500;
	
	private String url;
	private Receiver receiver;
	private HttpClient httpClient;
	private WebImageFileCache cache;
	private boolean delayHttpRequest;
	
	private HttpGet httpGet;
	private Object httpGetMutex;
	
	public WebImageRequest(String url, Receiver receiver, HttpClient httpClient, WebImageFileCache cache, boolean delayHttpRequest) {
		super();
		
		this.url = url;
		this.receiver = receiver;
		this.httpClient = httpClient;
		this.cache = cache;
		this.delayHttpRequest = delayHttpRequest;
		
		this.httpGet = null;
		this.httpGetMutex = new Object();
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		Bitmap bitmap = null;
		
		if (this.cache != null) {
			byte[] data = this.cache.get(this.url);
			
			if (data != null) {
				bitmap = this.decodeBitmap(data);
			}
		}
		
		if (bitmap == null) {
			if (this.delayHttpRequest) {
				try {
					Thread.sleep(WebImageRequest.HTTP_REQUEST_DELAY);
				} catch (InterruptedException e) {
					return null;
				}
			}
			
			try {
				synchronized (this.httpGetMutex) {
					this.httpGet = new HttpGet(this.url);
				}
				
				HttpResponse httpResponse = this.httpClient.execute(this.httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				
				if (httpEntity != null) {
					byte[] data = InputStreamHelper.readFully(httpEntity.getContent());
					bitmap = this.decodeBitmap(data);
					
					if (this.cache != null && bitmap != null) {
						StatusLine statusLine = httpResponse.getStatusLine();
						
						if (statusLine != null) {
							int statusCode = statusLine.getStatusCode();
							
							if (statusCode == HttpStatus.SC_OK) {
								this.cache.set(this.url, data);
							} else {
								Log.e(WebImageManager.LOG_TAG, "Invalid http status code (" + statusCode + ") for image: " + this.url);
							}
						} else {
							Log.e(WebImageManager.LOG_TAG, "No http status line for image: " + this.url);
						}
					}
				} else {
					Log.e(WebImageManager.LOG_TAG, "No http entity for image: " + this.url);
				}
			} catch (IOException e) {
				if (!this.isCancelled()) {
					Log.e(WebImageManager.LOG_TAG, e.getMessage(), e);
				}
			}
		}
		
		return bitmap;
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		super.onPostExecute(bitmap);
		
		this.receiver.onWebImageRequestResult(this, bitmap);
	}
	
	public void cancelWebImageRequest() {
		this.cancel(true);
		
		synchronized (this.httpGetMutex) {
			if (this.httpGet != null) {
				this.httpGet.abort();
			}
		}
	}
	
	private Bitmap decodeBitmap(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	
	public static interface Receiver {
		public void onWebImageRequestResult(WebImageRequest request, Bitmap bitmap);
	}
}