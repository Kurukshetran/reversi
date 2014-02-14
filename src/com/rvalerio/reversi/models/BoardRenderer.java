package com.rvalerio.reversi.models;

import java.util.ArrayList;
import java.util.Vector;

import com.rvalerio.reversi.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.util.Log;

public class BoardRenderer {
	private Board board;
	private int[][] turning;
	public Vector<PieceRenderable> pieces;
	public Vector<Point> legalMoves;
	public int next_turn;
	private Bitmap empty;
	private Rect emptyRect;
	public int winner = -1;
	public int timer = -1;
	
	private Rect [][]positions;
	private Paint paint, transparentPaint;

	
	public BoardRenderer(Resources res, Board board) {
		this.board = board;
		this.turning = new int[board.height][board.width];
		for(int y=0; y<board.height; y++) {
			for(int x=0; x<board.width; x++) {
				turning[y][x] = 0;
			}
		}
		
		legalMoves = new Vector<Point>();
		next_turn = -1;
		paint = new Paint();
		transparentPaint = new Paint();
		transparentPaint.setAlpha(100);
		
		pieces = new Vector<PieceRenderable>();
		pieces.add( new PieceRenderable() );
		//pieces.add( new PieceRenderable(BitmapFactory.decodeResource(res, R.drawable.piece1)) );
		//pieces.add( new PieceRenderable(BitmapFactory.decodeResource(res, R.drawable.piece2)) );
		pieces.add( new PieceRenderable(BitmapFactory.decodeResource(res, R.drawable.chip_b)) );
		pieces.add( new PieceRenderable(BitmapFactory.decodeResource(res, R.drawable.chip_w)) );
		
		positions = new Rect[ board.getHeight() ][  board.getWidth() ];

		empty = BitmapFactory.decodeResource(res, R.drawable.cell);
		emptyRect = new Rect(0, 0, empty.getWidth(), empty.getHeight());
	}
	
	public void setChanged( Vector<Point> changed ) {
		for(int i=0; i<changed.size(); i++) {
			Point p = changed.get(i);
			if( board.getCellAt(p.x, p.y).type == Cell.BLACK ) {
				turning[p.y][p.x] = 1;
			} else {
				turning[p.y][p.x] = -1;
			}
		}
	}

	
	public void setupPositions(int screenW, int screenH) {
		int screenCX = screenW / 2;
		int screenCY = screenH / 2;

		int screenSmallest = screenW < screenH? screenW: screenH;
		
		int spacingX = 0; //screenSmallest * 3 / 100;
		int spacingY = 0; //screenSmallest * 3 / 100;
		
		int cx, cy, bw = board.getWidth(), bh = board.getHeight();
		int totalSpacingX = (bw-1) * spacingX;
		int totalSpacingY = (bh-1) * spacingY;
		
		int cellW = (screenSmallest - totalSpacingX)  * (96/bw) / 100;
		int cellH = cellW;

		int w = totalSpacingX + bw * cellW;
		int h = totalSpacingY + bh * cellH;
		//int board_sx = screenCX - w / 2;
		//int board_sy = (screenW - bw*cellW)/2;
		int board_sx = screenCX - w / 2;
		int board_sy = screenCY - h / 2;

		int offsetX;
		int offsetY = 0;
		
		for(int y=0; y<bh; y++) {
			offsetX = 0;
			
			for(int x=0; x<bw; x++) {
				cx = board_sx + offsetX;
				cy = board_sy + offsetY;
				
				positions[y][x] = new Rect(cx, cy, cx + cellW, cy + cellH);
				
				offsetX += cellW + spacingX;
			}			

			offsetY += cellH + spacingY;
		}
	}
	

	public boolean isInsideRect(Rect r, int x, int y) {
		return x >= r.left && x <= r.right && y >= r.top && y <= r.bottom;
	}
	
	
	public Point getTouchedSquare(int tx, int ty) {
	
		for(int y=0; y<board.getHeight(); y++) {
			for(int x=0; x<board.getWidth(); x++) {
				if( isInsideRect( positions[y][x], tx, ty))
					return new Point(x, y);
			}
		}

		return null;
	}
	
	
	public boolean touchedSquare(int x, int y) {
		return getTouchedSquare(x, y) instanceof Point;
	}

	
	public boolean canPlayAt(Point p) {
		return p instanceof Point && board.getCellAt( p.x, p.y ).isEmpty();
	}
	
	
	public boolean canPlayAt(int x, int y) {
		Point p = getTouchedSquare(x, y);

		return canPlayAt(p);
	}

	
	public void playAt(Point p, int type) {
		if( p instanceof Point && canPlayAt( p ) ) {
			board.getCellAt( p.x, p.y ).type = type;
		}
	}
	
	
	public void blinkWinner() {
		if(winner == -1) {
			winner = board.getWinner();
			timer = 90;
		}
	}
	
	public boolean finishedBlinking() {
		return timer == 0;
	}
	
	public void updateBlinking() {
		--timer;
	}
	
	
	public void renderCell(Canvas c, Paint paint, int x, int y) {
		Cell cell = board.getCellAt(x, y);

		PieceRenderable piece = pieces.get(cell.type);
		if(piece.image == null)
			return;

		if(turning[y][x] == 0) {
			c.drawBitmap( piece.image, piece.source, positions[y][x], paint);
		} else if(turning[y][x] > 0 ) {
			Rect newPos = new Rect(positions[y][x]);
			int width = newPos.right-newPos.left;
			int offset = width - Math.abs(turning[y][x]-15) * width / 15;

			newPos.left += offset;
			newPos.right -= offset;
			
			if(turning[y][x] >= 15)
				piece = pieces.get(Cell.BLACK);
			else
				piece = pieces.get(Cell.WHITE);
				
			c.drawBitmap( piece.image, piece.source, newPos, paint);
			turning[y][x] = (turning[y][x]+3)%31;
			
		} else {
			Rect newPos = new Rect(positions[y][x]);
			int width = newPos.right-newPos.left;
			int offset = width - Math.abs(turning[y][x]+15) * width / 15;

			newPos.left += offset;
			newPos.right -= offset;
			
			if(turning[y][x] <= -15)
				piece = pieces.get(Cell.WHITE);
			else
				piece = pieces.get(Cell.BLACK);
				
			c.drawBitmap( piece.image, piece.source, newPos, paint);
			turning[y][x] = (turning[y][x]-3)%31;
		}
	}
	
	
	public void render(Canvas c) {
		for(int y=0; y<board.getHeight(); y++) {
			for(int x=0; x<board.getWidth(); x++) {
				c.drawBitmap( empty, emptyRect, positions[y][x], paint);
				
				if(winner != -1 && (winner == 0 || winner == board.getCellAt(x, y).type) && (timer / 10) % 2 == 1)
					continue;
				
				renderCell( c, paint, x, y);
			}
		}
		
	
		for(int i=0; i<legalMoves.size(); i++) {
			Point p = legalMoves.get(i);
			PieceRenderable piece = pieces.get(next_turn);
			
			if( piece.image != null)
				c.drawBitmap( piece.image, piece.source, positions[p.y][p.x], transparentPaint);
		}
	}
}
