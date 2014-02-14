package com.rvalerio.reversi.widgets;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

public class ButtonComponent {
	private String text;
	private Bitmap im;
	private boolean drawRect = true;
	public int text_size, bg_color, fg_color;
	private int cx, cy;
	public int radius = 10;

	private Rect im_src, im_dst;
	private int imw, imh;
	
	private Rect position;
	private RectF positionF;
	private Paint paint;
	
	public ButtonComponent(String text, int bg_color, int fg_color, int text_size, Rect position) {
		this.text = text;
		this.im = null;
		this.bg_color = bg_color;
		this.fg_color = fg_color;
		this.text_size = text_size;

		Typeface tf = Typeface.create( "Arial", Typeface.BOLD );
		paint = new Paint();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTypeface( tf );
		paint.setTextSize(text_size);
		
		this.setPosition(position);
	}
	
	
	public ButtonComponent(Bitmap im, int bg_color, int fg_color, int text_size, Rect position) {
		this.text = null;
		
		if(im != null) {
			this.im = im;
			this.imw = im.getWidth();
			this.imh = im.getHeight();
		}

		this.bg_color = bg_color;
		this.fg_color = fg_color;
		this.text_size = text_size;

		Typeface tf = Typeface.create( "Arial", Typeface.BOLD );
		paint = new Paint();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTypeface( tf );
		paint.setTextSize(text_size);
		
		this.setPosition(position);
	}
	
	
	public void setBitmap(Bitmap im) {
		this.im = im;
	}

	
	public void setDrawRect(boolean flag) {
		drawRect = flag;
	}
	
	public void setPosition(int left, int top, int right, int bottom) {
		setPosition( new Rect(left, top, right, bottom) );
	}

	
	public void setSourceRatio(float wr, float hr) {
		imw = (int) ((float) imw * wr);
		imh = (int) ((float) imh * hr);
		
		calculateDestRect(position);
	}
	
	
	public void calculateDestRect(Rect position) {
		im_src = new Rect(0, 0, imw, imh);
		int img_height = (position.bottom - position.top)*18/20;
		int img_width = (int) ((float) imw * (float) img_height / (float) imh);
		
		im_dst = new Rect(
				(position.left + position.right) / 2 - img_width / 2,
				(position.top + position.bottom) / 2 - img_height / 2,
				(position.left + position.right) / 2 + img_width / 2,
				(position.top + position.bottom) / 2 + img_height / 2
				);	
	}
	
	
	public void setPosition(Rect position) {
		this.position = new Rect(position);
		this.positionF = new RectF(position);
		
		cx = (position.left + position.right) / 2;
		cy = (position.top + position.bottom) / 2 - (int)((paint.descent() + paint.ascent()) / 2);
		
		if(im != null) {
			calculateDestRect(position);
		}
	}
	
	
	public void setTextSize(int size) {
		paint.setTextSize(size);
	}
	
	
	public void setText(String text) {
		this.text = text;
		
		this.setPosition(position);

	}
	
	
	public void setBgColor(int color) {
		bg_color = color;
	}

	
	public boolean isTouched(int x, int y) {
		return x >= position.left && x <= position.right
			&& y >= position.top && y <= position.bottom;
	}

	
	public void render(Canvas c, int radius) {
		paint.setColor(bg_color);
        if(drawRect)
        	c.drawRoundRect( positionF, radius, radius, paint);
        
        if(im != null) {
			c.drawBitmap(im, im_src, im_dst, paint);
        }
        
        if(text != null) {
			paint.setColor(fg_color);
			c.drawText(text, cx, cy, paint);
        }

	}
	
	
	public void render(Canvas c) {
		render(c, 10);
	}
}
