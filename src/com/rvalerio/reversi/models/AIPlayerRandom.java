package com.rvalerio.reversi.models;

import com.rvalerio.reversi.scenes.GameScene;
import android.graphics.Point;


public class AIPlayerRandom extends AIPlayer {
	public AIPlayerRandom(GameScene game) {
		super(game);
	}
	
	public AIPlayerRandom(Board board) {
		super(board);
	}
	
	public Point getMove() {
		if(!calculated) { 
			moves = board.getLegalMoves(this.type);
			calculated = true;
		}
		
		if(moves.size() <= 0)
			return null;
		
		if(simulated_delay < 30) {
			simulated_delay += 1;
			return null;
		}
		
		simulated_delay = 0;
		calculated = false;
		
		Point p = moves.get(rnd.nextInt(moves.size()));
		
		moves.clear();
		
		return p;
	}
}
