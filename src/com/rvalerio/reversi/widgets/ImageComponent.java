package com.rvalerio.reversi.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class ImageComponent {
	private Rect position_rect, source_rect;
	private Bitmap im;
	private Paint paint;
	
	public ImageComponent(Bitmap im, Rect pos, Rect src) {
		this.paint = new Paint();
		this.im = im;
		this.position_rect = pos;
		this.source_rect = src;
	}


	public ImageComponent(Bitmap im) {
		this.paint = new Paint();
		this.im = im;
	}
	
	
	public ImageComponent(Context context, int resource) {
		this.paint = new Paint();
		this.im = BitmapFactory.decodeResource(context.getResources(), resource);
		this.position_rect = new Rect();
		this.source_rect = new Rect();
	}
	
	public void setSourceFullRect() {
		this.source_rect = new Rect(0, 0, im.getWidth(), im.getHeight());
	}

	
	public void setSourceRect(int left, int top, int right, int bottom) {
		this.source_rect = new Rect(left, top, right, bottom);
	}

	
	public void setPositionRect(int left, int top, int right, int bottom) {
		this.position_rect = new Rect(left, top, right, bottom);
	}

	

	
	public int imageWidth() {
		return im.getWidth();
	}

	
	public int imageHeight() {
		return im.getHeight();
	}

	
	public float getRatio2() {
		return (float) (source_rect.right - source_rect.left) / (source_rect.bottom - source_rect.top);
	}

	
	public float getRatio() {
		return (float) im.getWidth() / im.getHeight();
	}
	

	public void render(Canvas c) {
		c.drawBitmap(im, source_rect, position_rect, paint);
	}
}
