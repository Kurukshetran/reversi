package com.rvalerio.reversi.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import android.graphics.Point;

public class Board {
	public int width, height, num_players;
	private Cell [][]board;
	private Vector<Point> check_directions;
	
	public Board(int w, int h, int players) {
		width = w;
		height = h;
		
		board = new Cell[w][h];
		num_players = players;
		
		check_directions = new Vector<Point>();
		check_directions.add( new Point(-1, 0) );
		check_directions.add( new Point(-1, -1) );
		check_directions.add( new Point(0, -1) );
		check_directions.add( new Point(1, -1) );
		check_directions.add( new Point(1, 0) );
		check_directions.add( new Point(1, 1) );
		check_directions.add( new Point(0, 1) );
		check_directions.add( new Point(-1, 1) );
	}
	
	
	public void setup() {
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				board[y][x] = new Cell();
			}
		}
		
		board[height/2-1][width/2-1].type = 2;
		board[height/2-1][width/2].type = 1;
		board[height/2][width/2-1].type = 1;
		board[height/2][width/2].type = 2;
	}
	
	
	public boolean canPlay() {
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				if(board[y][x].isEmpty()) return true;
			}
		}
		
		return false;
	}
	
	
	public boolean isFinished() {
		return getLegalMoves(Cell.BLACK).size() == 0 && getLegalMoves(Cell.WHITE).size() == 0;
	}



	public Vector<Point> getEmptySlots() {
		Vector<Point> empty_slots = new Vector<Point>();
		
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				if( getCellAt(x,y).isEmpty() ) {
					empty_slots.add( new Point(x, y) );
				}
			}
		}
		
		return empty_slots;
	}
	
	public boolean inBounds(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	public boolean isLegalMoveHelper(int x, int y, int dx, int dy, int player_type) {
		if(!inBounds(x,y))
			return false;

		if(!getCellAt(x, y).isEmpty())
			return false;
		
		int x_next = x + dx;
		int y_next = y + dy;

		if(!inBounds(x_next,y_next))
			return false;
		
		if(getCellAt(x_next, y_next).type == player_type)
			return false;
		
		for(int i=y_next,  j=x_next; ; i+=dy, j+=dx) {
			if(!inBounds(j, i))
				return false;

			if(getCellAt(j, i).isEmpty())
				return false;
			
			if(getCellAt(j, i).type == player_type)
				return true;
		}
	}
	

	public boolean isLegalMove(int x, int y, int player_type) {
		boolean hasMove = false;

		if(!getCellAt(x, y).isEmpty())
			return false;
		
		for(int i=0; i<check_directions.size(); i++) {
			Point d = check_directions.get(i);
			hasMove = hasMove || isLegalMoveHelper(x, y, d.x, d.y, player_type);
		}
		
		return hasMove;
		
	}


	public Vector<Point> changePieces(int x, int y, int player_type) {
		Vector<Point> changed = new Vector<Point>();
		
		int biggest = width > height? width: height;
		boolean hasMove = false;

		if(!getCellAt(x, y).isEmpty())
			return changed;

		board[y][x].type = player_type;

		Vector<Point> directions = new Vector<Point>();
		Vector<Point> switchable_lines = new Vector<Point>();
		

		for(int j=0; j<check_directions.size(); j++) {
			Point d = check_directions.get(j);
			Cell cell = getCellAt(x+d.x, y+d.y);

			if(cell != null && !cell.isEmpty() && cell.type != player_type) {
				directions.add( new Point(d) );
			}
		}
		
		
		for(int i=2; i<biggest; i++) {
			for(int j=directions.size()-1; j>=0; j--) {
				Point d = directions.get(j);
				Cell cell = getCellAt(x+d.x*i, y+d.y*i);
				
				if(cell == null || cell.isEmpty()) {
					directions.remove(j);
					continue;
				}
				
				if(cell.type == player_type) {
					directions.remove(j);
					switchable_lines.add(d);
				}
			}
		}
		
		
		for(int i=1; i<biggest; i++) {
			for(int j=switchable_lines.size()-1; j>=0; j--) {
				Point d = switchable_lines.get(j);
				Cell cell = getCellAt(x+d.x*i, y+d.y*i);
				
				if(cell.type == player_type) {
					switchable_lines.remove(j);
					continue;
				}
				
				changed.add( new Point( x+d.x*i, y+d.y*i));
				cell.type = player_type;
			}
		}
		
		return changed;
	}

	
	public Vector<Point> getLegalMoves(int player_type) {
		Vector<Point> moves = getEmptySlots();
		Vector<Point> valid_moves = new Vector<Point>();
		
		Point move;
		
		for(int m=0; m<moves.size(); m++) {
			move = moves.get(m);

			if(isLegalMove(move.x, move.y, player_type)) {
				valid_moves.add( move );
			}
		}
		
		return valid_moves;
	}
	
	public int getWinner() {
		int []types = getPiecesCount(); 
		
		if(types[1] == types[2])
			return 0;
		
		return types[1] > types[2]? 1: 2;
	}

	
	public int[] getPiecesCount() {
		int []types = new int[num_players+1]; 
		
		for(int i=0; i<=num_players; i++) {
			types[i] = 0;
		}
		
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				types[ getCellAt(x, y).type ]++;
			}
		}
		
		return types;
		
	}

	
	public Cell getCellAt(int x, int y) {
		if(x < 0 || x >= width || y < 0 || y >= height)
			return null;
		
		return board[y][x];
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
}
