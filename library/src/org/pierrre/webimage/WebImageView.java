package org.pierrre.webimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

public class WebImageView extends ImageView implements WebImageManager.Receiver {
	private WebImageManager manager;
	private String url;
	
	public WebImageView(Context context) {
		super(context);
		
		this.initWebImageView();
	}
	
	public WebImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.initWebImageView();
	}
	
	public WebImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		this.initWebImageView();
	}
	
	private void initWebImageView() {
		this.manager = WebImageManager.getInstance(this.getContext());
		this.url = null;
	}
	
	public void setImageUrl(String url) {
		this.resetWebImage();
		
		this.setImageBitmap(null);
		
		this.url = url;
		
		if (this.url != null) {
			this.manager.requestWebImage(this.url, this);
		}
	}
	
	protected void resetWebImage() {
		if (this.url != null) {
			this.url = null;
			
			this.manager.removeReceiver(this);
		}
	}
	
	@Override
	public void onWebImageResult(String url, Bitmap bitmap) {
		super.setImageBitmap(bitmap);
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		this.resetWebImage();
		
		super.setImageBitmap(bm);
	}
	
	@Override
	public void setImageDrawable(Drawable drawable) {
		this.resetWebImage();
		
		super.setImageDrawable(drawable);
	}
	
	@Override
	public void setImageResource(int resId) {
		this.resetWebImage();
		
		super.setImageResource(resId);
	}
	
	@Override
	public void setImageURI(Uri uri) {
		this.resetWebImage();
		
		super.setImageURI(uri);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		this.resetWebImage();
	}
}