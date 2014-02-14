package com.rvalerio.reversi.scenes;

import com.rvalerio.reversi.Game;

import android.content.Context;
import android.graphics.Canvas;

public class Scene {
	protected Game gameView;
	protected Context context;
	protected int screenW, screenH;

    protected Scene(Context context, Game game) {
		this.context = context;
        this.gameView = game;
    }
    
    public void init() {}
    
    public void oneTimeInit() {}
    
	
	public void preUpdate() {}
	public void update(long time) {}	
	public void postUpdate() {}

    public void preRender() {}
	public void render(Canvas c) {}
    public void postRender() {}
	
	public void postCalcDimensions() {}
    public void calcDimensions() {}

    public void setDimensions(int width, int height) {
		this.screenW = width;
		this.screenH = height;
		
		calcDimensions();
		postCalcDimensions();
	}
	


}
