package com.rvalerio.reversi.widgets;

import android.graphics.Canvas;
import android.graphics.Paint;

public class TextComponent {
	public final static int ALIGN_CENTER = 0, ALIGN_LEFT = 1, ALIGN_RIGHT = 2;

	private String text;
	private int color, size, align, x, y;
	private Paint paint;
	private boolean showing = true;
	
	public TextComponent(String text, int color, int size, int align) {
		this.text = text;
		this.color = color;
		this.size = size;
		this.align = align;
		
		paint = new Paint();
		paint.setColor(color);
		paint.setTextSize(size);
		
		switch(align) {
		case ALIGN_CENTER: paint.setTextAlign(Paint.Align.CENTER); break;
		case ALIGN_LEFT: paint.setTextAlign(Paint.Align.LEFT); break;
		case ALIGN_RIGHT: paint.setTextAlign(Paint.Align.RIGHT); break;
		}
	}
	
	
	public void show() {
		showing = true;
	}
	
	public void hide() {
		showing = false;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	
	public void setTextSize(int size) {
		paint.setTextSize(size);
	}
	
	public void render(Canvas c) {
		if(showing)
			c.drawText(text, x, y, paint);
	}
}
