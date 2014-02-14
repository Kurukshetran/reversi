package com.rvalerio.reversi.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.rvalerio.reversi.Game;

public class GameDataManager {
	public static void load(Game gameView) {
		if(gameView.gameData.readInt("play_sounds") == 0)
			gameView.sound.mute();
		
		//Game.highscore = gameView.gameData.readInt("highscore");
	}
	
	
	public static void save(Game gameView) {
		gameView.gameData.writeInt("play_sounds", gameView.sound.isMuted()? 0: 1);
		//gameView.gameData.writeInt("highscore", Game.highscore );
	}
	
}
