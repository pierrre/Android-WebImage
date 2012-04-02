package org.pierrre.webimage.sample.application;

import org.pierrre.webimage.WebImageManager;

import android.app.Application;

public class WebImageSampleApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		WebImageManager webImageManager = WebImageManager.getInstance(this);
		webImageManager.setMemoryCacheEnabled(true);
		webImageManager.setFileCacheEnabled(true);
	}
}
