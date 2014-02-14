package com.rvalerio.reversi;

import com.rvalerio.reversi.models.AppRater;
import com.rvalerio.reversi.models.Cell;
import com.rvalerio.reversi.models.GameData;
import com.rvalerio.reversi.models.GameDataManager;
import com.rvalerio.reversi.models.SoundPoolPlayer;
import com.rvalerio.reversi.scenes.*;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class Game extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = Game.class.getSimpleName();

	public enum Scenes { SAME, LOADING, MENU, GAME };
	public enum Difficulty { EASY, INTERMEDIATE, ADVANCED, EXPERT };

	public static Difficulty difficulty = Difficulty.EASY;
	public static int num_players = 1;

	public BaseScene scene;
	public SoundPoolPlayer sound;
	public GameData gameData;
	public Context context;
    public int width, height;
	public static int selected_ball = Cell.BLACK;

	private GameThread thread;
	private String avgFps;
	public boolean sound_played = false;
	public boolean render_lock = false;

	
    public Game(Context context) {
    	super(context);

    	getHolder().addCallback(this);
    	
    	this.context = context;

        sound = new SoundPoolPlayer(context);
        gameData = new GameData(context);

        GameDataManager.load(this);

    	setScene( Scenes.MENU );

    	thread = new GameThread(getHolder(), this);
    	setFocusable(true);
    }

    
    public void setPlayersNumber(int n) {
    	num_players = n;
    }
    

    public void exitGame() {
    	GameDataManager.save(this);

    	cleanUp();
    	((Reversi) context ).finish();
    }
    
    
	public void setAvgFps(String avgFps) {
		this.avgFps = avgFps;
	}


    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	scene.onTouchEvent(event);

    	return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	boolean flag = false;
    	
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
		    flag = scene.onBackPressed();
		}
		
    	if(!flag)
    		return super.onKeyDown(keyCode, event);
    	
    	return true;
    }
    
    
    public void onBackPressed() {
    	scene.onBackPressed();
    }

    
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;

		scene.setDimensions(width, height);
		thread.setSurfaceHolder(holder);
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Rect rect = holder.getSurfaceFrame();
		
		width = rect.right;
		height = rect.bottom;
		
		this.scene.setDimensions( rect.right, rect.bottom );

		thread.setRunning(true);
		thread.setSurfaceHolder(holder);

		if(!thread.started)
			thread.start();
		
	}


	public void pause() {
		synchronized (thread) {
			thread.onPause();
		}
	}
	
	public void resume() {
		synchronized (thread) {
			thread.onResume();
		}
	}
	
	public void cleanUp() {
		boolean retry = true;
		
		thread.setFinished(true);
		
		while(retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				
			}
		}
	}

	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}
	
	
	public void update(long time) {
		scene.update(time);
	}
	
	
	public void render(Canvas canvas) {
		scene.render(canvas);
		
		displayFps(canvas, avgFps);
	}


	private void displayFps(Canvas canvas, String fps) {
		if (canvas != null && fps != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText(fps, this.getWidth() - 50, 20, paint);
		}
	}

	

	
	public boolean renderLocked() {
		return render_lock;
	}
	
	
	public void getRenderLock() {
		while( render_lock );
		
		render_lock = true;
	}
	
	
	public void releaseRenderLock() {
		render_lock = false;
	}

	
	public void setScene(Scenes scene) {
		getRenderLock();
		
		switch(scene) {
		case GAME:
			this.scene = new GameScene(context, this, difficulty, num_players);
			break;
		case MENU:
			this.scene = new MenuScene(context, this);
			break;
		default:

		}

		this.scene.setDimensions( width, height );
		this.scene.oneTimeInit();
		
		releaseRenderLock();
	}
}
