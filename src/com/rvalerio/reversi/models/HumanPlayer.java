package com.rvalerio.reversi.models;

import com.rvalerio.reversi.scenes.GameScene;

import android.graphics.Point;

public class HumanPlayer extends Player {

	public HumanPlayer(GameScene game) {
		super(game);
		
		is_human = true;
	}
	

	public Point getMove() {
		
		Point p = parent.boardRenderer.getTouchedSquare(parent.lastTouchUp.x, parent.lastTouchUp.y);

		if(p == null || !parent.boardRenderer.canPlayAt(p))
			return null;

		if(!parent.board.isLegalMove(p.x, p.y, this.type))
			return null;
		
		return p;
	}
}
