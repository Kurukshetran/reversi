package com.rvalerio.reversi.models;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class PieceRenderable {
	public Bitmap image;
	public Rect source;

	public PieceRenderable() {}

	public PieceRenderable(Bitmap im) {
		image = im;
		source = new Rect(0, 0, im.getWidth(), im.getHeight());
	}
}
