package com.rvalerio.reversi.models;

public class Cell {
	public final static int EMPTY = 0, BLACK = 1, WHITE = 2; 
	public int type;

	
	Cell() {
		type = Cell.EMPTY;
	}


	public int getType() { return type; }
	public boolean isEmpty() { return type == Cell.EMPTY; }
	public void setEmpty() { type = Cell.EMPTY; }
	
}
