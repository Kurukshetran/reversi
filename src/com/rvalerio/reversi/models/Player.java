package com.rvalerio.reversi.models;

import android.graphics.Point;

import com.rvalerio.reversi.scenes.GameScene;
import com.rvalerio.reversi.widgets.Scores;

public class Player {
	public static final int WHITE = 2, BLACK = 1;
	public static int current_type = 1;
	public static int current_number = 0;

	public GameScene parent;
	public int number;
	public int type;
	public boolean is_human = false;
	public Scores score;
	
	public static void reset() {
		current_number = 0;
		current_type = 1;
	}
	
	public Player(GameScene game) {
		parent = game;
		
		number = current_number++;
		type = current_type++;
		
		score = new Scores();
	}
	
	public boolean isHuman() {
		return is_human;
	}
	
	
	public Point getMove() {
		return null;
	}
}
