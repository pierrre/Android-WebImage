package org.pierrre.webimage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class WebImageDrawable extends Drawable implements WebImageManager.Receiver {
	private WebImageManager manager;
	private Paint paint;
	private String url;
	private Bitmap bitmap;
	
	public WebImageDrawable(WebImageManager manager) {
		super();
		
		this.manager = manager;
		
		this.paint = new Paint();
		this.paint.setFilterBitmap(true);
		
		this.url = null;
		this.bitmap = null;
	}
	
	public void setImageUrl(String url) {
		this.resetWebImage();
		
		this.bitmap = null;
		
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
		this.bitmap = bitmap;
		
		this.invalidateSelf();
	}
	
	@Override
	public void draw(Canvas canvas) {
		if (this.bitmap != null) {
			canvas.drawBitmap(this.bitmap, null, this.getBounds(), this.paint);
		}
	}
	
	@Override
	public int getIntrinsicWidth() {
		if (this.bitmap != null) {
			return this.bitmap.getWidth();
		} else {
			return super.getIntrinsicWidth();
		}
	}
	
	@Override
	public int getIntrinsicHeight() {
		if (this.bitmap != null) {
			return this.bitmap.getHeight();
		} else {
			return super.getIntrinsicHeight();
		}
	}
	
	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}
	
	@Override
	public void setAlpha(int alpha) {
		this.paint.setAlpha(alpha);
	}
	
	@Override
	public void setColorFilter(ColorFilter cf) {
		this.paint.setColorFilter(cf);
	}
}
