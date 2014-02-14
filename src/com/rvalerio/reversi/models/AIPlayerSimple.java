package com.rvalerio.reversi.models;

import java.util.Vector;

import com.rvalerio.reversi.scenes.GameScene;
import android.graphics.Point;


public class AIPlayerSimple extends AIPlayer {
	
	public AIPlayerSimple(GameScene game) {
		super(game);
	}
	
	public AIPlayerSimple(Board board) {
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
		
		Vector<Point> chosen = new Vector<Point>();

		// priority is:
		// play corner
		// play edge except the cells right before corners
		// play  anywhere inside x >= 2 && x <= width - 2,
		//                       y >= 2 && y <= height -2

		
		// check for corner moves, highest valued
		for(int i=0; i<moves.size(); i++) {
			Point move = moves.get(i);
			if(move.x == 0 && move.y == 0)
				chosen.add( move );
			
			if(move.x == board.width-1 && move.y == 0)
				chosen.add( move );
			
			if(move.x == board.width-1 && move.y == board.height-1)
				chosen.add( move );
			
			if(move.x == 0 && move.y == board.height-1)
				chosen.add( move );
		}

		
		if(chosen.size() > 0) {
			moves.clear();
			return chosen.get( rnd.nextInt(chosen.size()) );
		}
		

		// check for side moves, except ones adjacent to corner
		for(int i=0; i<moves.size(); i++) {
			Point move = moves.get(i);
			
			if((move.y == 0 || move.y == board.height-1) && (move.x > 1 || move.x < board.width-2)
				|| (move.y > 1 || move.y < board.height-2) && (move.x == 0 || move.x == board.width-1))
				chosen.add( move );
		}
		

		if(chosen.size() > 0) {
			moves.clear();
			return chosen.get( rnd.nextInt(chosen.size()) );
		}
		
		// check for side moves, except ones adjacent to corner
		for(int i=0; i<moves.size(); i++) {
			Point move = moves.get(i);
			
			if((move.x > 1 && move.x < board.width - 1 && move.y > 1 && move.y < board.height -1))
				chosen.add( move );
		}

		if(chosen.size() > 0) {
			moves.clear();
			return chosen.get( rnd.nextInt(chosen.size()) );
		}

		// check for side moves, except ones adjacent to corner
		for(int i=0; i<moves.size(); i++) {
			Point move = moves.get(i);
			
			if((move.y == 1 || move.y == board.height-2) && (move.x > 1 || move.x < board.width-2)
				|| (move.y > 1 || move.y < board.height-2) && (move.x == 1 || move.x == board.width-2))
				chosen.add( move );
		}
		

		if(chosen.size() > 0) {
			moves.clear();
			return chosen.get( rnd.nextInt(chosen.size()) );
		}

		Point move = moves.get( rnd.nextInt(moves.size()) );
		moves.clear();
		
		return move;
	}
}
