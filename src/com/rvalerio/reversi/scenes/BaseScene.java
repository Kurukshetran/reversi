package com.rvalerio.reversi.scenes;

import com.rvalerio.reversi.Game;
import com.rvalerio.reversi.Game.Scenes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;

public class BaseScene extends Scene {
	protected int xPos, yPos;
	protected int transition_x = 0, transition_vx = 0, transition_accelx = -5;
	protected Scenes next_screen = Game.Scenes.SAME;
    protected boolean transitioning = false;

    public Point lastTouchUp, lastTouchDown, lastTouchMove;
    
    protected BaseScene(Context context, Game game) {
    	super(context, game);
    	
    	lastTouchUp = new Point(-1, -1);
    	lastTouchDown = new Point(-1, -1);
    	lastTouchMove = new Point(-1, -1);
    }
    
	
    protected boolean isTransitioning() {
    	return transitioning;
    }
    
	protected void transitionRollIn() {
		transitionStart(Game.Scenes.SAME);
		
		transition_x = -screenW;
        transition_vx = screenW / 5;
	}


	protected void transitionStart(Scenes selected) {
		next_screen = selected;
		
		transitioning = true;
		transition_x = 0;
		transition_vx = 0;
	}

	
	protected void transitionEnd() {
		if(next_screen == Game.Scenes.SAME) return;
		
		gameView.setScene(next_screen);
	}
	
	protected void transitionChangeState() { }
	
	protected void transitionUpdate() {
		if(!transitioning) return;

		xPos = 0;

		transition_vx += transition_accelx;
		transition_x += transition_vx;
		xPos += transition_x;

		if(xPos <= -screenW) {
			if(next_screen != Scenes.SAME) transitionEnd();
			
			transitionChangeState();
			
			transition_vx += transition_accelx;
			transition_vx *= -1;
		} else if(transition_vx > 0 && transition_vx < -transition_accelx || xPos >= 0) {
			xPos = 0;
			transitioning = false;
			transition_x = 0;
			transition_vx = 0;
		}
		
	}
	
	
	protected void translatePush(Canvas c) {
		c.save();
		c.translate(xPos, 0);
	}


	protected void translatePop(Canvas c) {
		c.restore();
	}

	
	public void update(long time) {
		transitionUpdate();
	}
	
	public void postUpdate() {
		lastTouchUp.x = -1;
		lastTouchDown.y = -1;
		
		lastTouchMove.x = -1;
		lastTouchMove.y = -1;
		
		lastTouchUp.x = -1;
		lastTouchDown.y = -1;
	}
	
	public void touchDown(int x, int y) {
		lastTouchDown.x = x;
		lastTouchDown.y = y;
	}
	
	public void touchMove(int x, int y) {
		lastTouchMove.x = x;
		lastTouchMove.y = y;
	}
	
	public void touchUp(int x, int y) {
		lastTouchUp.x = x;
		lastTouchUp.y = y;
	}
	
	public void onTouchEvent(MotionEvent event) {
		if(isTransitioning()) return;
		
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN: touchDown(x, y); break;
			case MotionEvent.ACTION_MOVE: touchMove(x, y); break;
			case MotionEvent.ACTION_UP: touchUp(x, y); break;
		}
	}
	
	public boolean onBackPressed() {
		return false;
	}
	
	
	public void exitGame() {
		gameView.exitGame();
	}

}
