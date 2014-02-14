package com.rvalerio.reversi;

import com.rvalerio.reversi.Game;
import com.rvalerio.reversi.models.AppRater;
import com.rvalerio.reversi.models.GameDataManager;

import android.app.Activity;
import android.os.Bundle;


public class Reversi extends Activity {
    private Game game;

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        	AppRater.app_launched(this);

            game = new Game(this);
            setContentView(game);
    }


	@Override
	public void onBackPressed() {
		game.onBackPressed();
	}
	

	@Override
	public void onPause() {
		super.onPause();
		
    	GameDataManager.save(game);

    	game.pause();
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		
    	game.resume();
	}

}
