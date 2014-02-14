package com.rvalerio.reversi.models;

import java.util.Random;
import java.util.Vector;

import com.rvalerio.reversi.scenes.GameScene;

import android.graphics.Point;

public class AIPlayer extends Player {
	protected Random rnd;
	protected Board board;
	protected int simulated_delay;
	protected Vector<Point> moves;
	protected boolean calculated = false;
	
	public AIPlayer(GameScene game) {
		super(game);
		this.board = parent.board;
		
		simulated_delay = 0;
		rnd = new Random();
	}
	

	public AIPlayer(Board board) {
		super(null);
		this.board = board;
		
		simulated_delay = 0;
		rnd = new Random();
	}
}
