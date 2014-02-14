package com.rvalerio.reversi;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
	private static final String TAG = GameThread.class.getSimpleName();
	
	Object mPauseLock;
    public boolean mPaused;
    private boolean mFinished;
	private boolean running;

	boolean started = false;
	
	private SurfaceHolder surfaceHolder;
	private Game gameView;

	private final static int 	MAX_FPS = 30;	
	private final static int	MAX_FRAME_SKIPS = 5;	
	private final static int	FRAME_PERIOD = 1000 / MAX_FPS;
	
	/* Stuff for stats */
	private DecimalFormat df = new DecimalFormat("0.##");
	// we'll be reading the stats every second
	private final static int 	STAT_INTERVAL = 1000; //ms
	// the average will be calculated by storing 
	// the last n FPSs
	private final static int	FPS_HISTORY_NR = 10;
	// the status time counter
	private long statusIntervalTimer	= 0l;
	// number of frames skipped since the game started
	private long totalFramesSkipped			= 0l;
	// number of frames skipped in a store cycle (1 sec)
	private long framesSkippedPerStatCycle 	= 0l;

	// number of rendered frames in an interval
	private int frameCountPerStatCycle = 0;
	private long totalFrameCount = 0l;
	// the last FPS values
	private double 	fpsStore[];
	// the number of times the stat has been read
	private long 	statsCount = 0;
	// the average FPS since the game started
	private double 	averageFps = 0.0;

	
	GameThread(SurfaceHolder holder, Game game) {
		super();
		this.surfaceHolder = holder;
		this.gameView = game;
		
		mPauseLock = new Object();
        mPaused = false;
        mFinished = false;
	}
	
	@Override
	public void start() {
		this.started = true;
		super.start();
	}
	
	public boolean isRunning() {
		return !this.mFinished;
	}
	
	public void setRunning(boolean running) {
		this.mFinished = !running;
	}
	
	public void setFinished(boolean finito) {
		this.mFinished = finito;
	}
	
	
	public void onPause() {
        synchronized (mPauseLock) {
            mPaused = true;
        }
    }
	
	public void onResume() {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }
	
	public void run() {
		Canvas canvas;

		initTimingElements();
		
		long beginTime;		// the time when the cycle begun
		long timeDiff;		// the time it took for the cycle to execute
		int sleepTime;		// ms to sleep (<0 if we're behind)
		int framesSkipped;	// number of frames being skipped 

		sleepTime = 0;

		while (!mFinished) {
            canvas = null;

			while( gameView.renderLocked() );
			
			try {
				canvas = this.surfaceHolder.lockCanvas();
				if(canvas == null) continue;
				
				synchronized (surfaceHolder) {
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;	// resetting the frames skipped
					
					this.gameView.update(beginTime);
					this.gameView.render(canvas);
					
					// calculate how long did the cycle take
					timeDiff = System.currentTimeMillis() - beginTime;
					// calculate sleep time
					sleepTime = (int)(FRAME_PERIOD - timeDiff);

					if (sleepTime > 0) {
						try {
							Thread.sleep(sleepTime);	
						} catch (InterruptedException e) {}
					}

					
					// if the game got delayed, update without rendering
					// to try to catch up
					//while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
						// we need to catch up
						//this.gameView.update(beginTime);
						//sleepTime += FRAME_PERIOD;
						//framesSkipped++;
					//}
					

					
					framesSkippedPerStatCycle += framesSkipped;
					//storeStats();
				}
			} finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}


            synchronized (mPauseLock) {
                while (mPaused) {
                    try {
                        mPauseLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

			
		}
	}

	
	private void storeStats() {
		frameCountPerStatCycle++;
		totalFrameCount++;
		// assuming that the sleep works each call to storeStats
		// happens at 1000/FPS so we just add it up
		statusIntervalTimer += FRAME_PERIOD;
		
		if (statusIntervalTimer >= STAT_INTERVAL) {
			// calculate the actual frames pers status check interval
			double actualFps = (double)(frameCountPerStatCycle / (STAT_INTERVAL / 1000));
			
			//stores the latest fps in the array
			fpsStore[(int) statsCount % FPS_HISTORY_NR] = actualFps;
			
			// increase the number of times statistics was calculated
			statsCount++;
			
			double totalFps = 0.0;
			// sum up the stored fps values
			for (int i = 0; i < FPS_HISTORY_NR; i++) {
				totalFps += fpsStore[i];
			}
			
			// obtain the average
			if (statsCount < FPS_HISTORY_NR) {
				// in case of the first 10 triggers
				averageFps = totalFps / statsCount;
			} else {
				averageFps = totalFps / FPS_HISTORY_NR;
			}
			// saving the number of total frames skipped
			totalFramesSkipped += framesSkippedPerStatCycle;
			// resetting the counters after a status record (1 sec)
			framesSkippedPerStatCycle = 0;
			statusIntervalTimer = 0;
			frameCountPerStatCycle = 0;
			gameView.setAvgFps("FPS: " + df.format(averageFps));
		}
	}

	private void initTimingElements() {
		// initialise timing elements
		fpsStore = new double[FPS_HISTORY_NR];
		for (int i = 0; i < FPS_HISTORY_NR; i++) {
			fpsStore[i] = 0.0;
		}
		
	}


	public void setSurfaceHolder(SurfaceHolder holder) {
		surfaceHolder = holder;
	}

}
