package com.rvalerio.reversi.scenes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import com.rvalerio.reversi.R;
import com.rvalerio.reversi.Game;
import com.rvalerio.reversi.models.AIPlayerRandom;
import com.rvalerio.reversi.models.AIPlayerSimple;
import com.rvalerio.reversi.models.Board;
import com.rvalerio.reversi.models.BoardRenderer;
import com.rvalerio.reversi.models.Cell;
import com.rvalerio.reversi.models.HumanPlayer;
import com.rvalerio.reversi.models.Player;
import com.rvalerio.reversi.widgets.ButtonComponent;
import com.rvalerio.reversi.widgets.ImageComponent;
import com.rvalerio.reversi.widgets.Scores;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class GameScene extends BaseScene {
	private Vector<Player> players;
	private ButtonComponent ball;

	public Board board;
	public BoardRenderer boardRenderer;
	public ButtonComponent black_score, white_score;
	
	private int player_turn, num_players = 2;

	private final static int PLAYING = 0, FINISHED_GAME = 1;
	private int state;
	private boolean render_lock = false;
	private ImageComponent background_top, background_bottom;
	private TextPaint textPaint;
	private Typeface tf;
	private String message;

	public GameScene(Context context, Game game, Game.Difficulty difficulty, int num_players) {
		super(context, game);

		message = null;
		
		this.num_players = 2;
        this.textPaint = new TextPaint();
        tf = Typeface.create( "Arial", Typeface.BOLD );

    	board = new Board(8, 8, 2);
    	boardRenderer = new BoardRenderer(context.getResources(), board);
        this.background_top = new ImageComponent(context, R.drawable.wood_cr);
        this.background_bottom = new ImageComponent(context, R.drawable.wood_cr);

    	Player.reset();
    	
    	players = new Vector<Player>();

    	if(Game.selected_ball == Cell.BLACK)
    		players.add( new HumanPlayer(this) );
    	
    	if(num_players == 1) {
    		if(difficulty == Game.Difficulty.EASY) {
    			players.add( new AIPlayerRandom(this) );
    		} else if(difficulty == Game.Difficulty.INTERMEDIATE) {
    			players.add( new AIPlayerSimple(this) );
    		}
    	} else {
    		players.add( new HumanPlayer(this) );
    	}

    	if(Game.selected_ball == Cell.WHITE)
    		players.add( new HumanPlayer(this) );
	}


	@Override
	public void oneTimeInit() {
		restart();
	}

	@Override
	public void calcDimensions() {
    	boardRenderer.setupPositions(screenW, screenH);

    	int ballsize = screenW*16/100;
    	int spacing = screenW*2/100;
    	int textsize = screenW*7/100;
    	int y = screenW*1/100;

    	black_score = new ButtonComponent(boardRenderer.pieces.get(Cell.BLACK).image, 0xff387400, 0xffffffff, textsize, new Rect(spacing, y, spacing+ballsize, y+ballsize));
    	white_score = new ButtonComponent(boardRenderer.pieces.get(Cell.WHITE).image, 0xff387400, 0xff000000, textsize, new Rect(screenW - spacing - ballsize, y, screenW - spacing, y+ballsize));
    	
    	black_score.setDrawRect(false);
    	white_score.setDrawRect(false);
    	
    	background_top.setSourceFullRect();
    	background_top.setPositionRect(0, 0, screenW, screenW*16/100);

    	background_bottom.setSourceFullRect();
    	background_bottom.setPositionRect(0, screenH - screenW*16/100, screenW, screenH);

    	int text_size = screenW / 15;
    	textPaint.setARGB(255, 255, 255, 255);
    	textPaint.setTextAlign(Align.LEFT);
		textPaint.setTextSize( text_size );
		textPaint.setTypeface( tf );

    }

	
	public void restart() {
		message = null;

		board.setup();
		boardRenderer.winner = -1;
		boardRenderer.timer = -1;
		
		state = GameScene.PLAYING;

		setPlayerTurn( 0 );
		
		updateLegalMoves();
		
		updateCounters();
    }
	

	public void updateCounters() {
		int []count = board.getPiecesCount();
		black_score.setText( ""+count[Cell.BLACK] );
		white_score.setText( ""+count[Cell.WHITE] );
	}

	
	public void updateLegalMoves() {
		if(!players.get(player_turn).isHuman())
			return;
		
		boardRenderer.next_turn = players.get(player_turn).type;
		boardRenderer.legalMoves = board.getLegalMoves( boardRenderer.next_turn );
	}
	
	public boolean isPlayerTurn(int player) {
		return player_turn == player;
	}
	
	public void setPlayerTurn(int player) {
		player_turn = player;
	}
	
	public void changeTurn() {
		player_turn++;
		player_turn %= 2;
	}


	@Override
	public void update(long time) {
		if(board.isFinished()) {
			if( !boardRenderer.finishedBlinking()) {
				boardRenderer.updateBlinking();
			}
			
			return;
		}

		Point move = players.get(player_turn).getMove();

		if(move == null)
			return;
		
		Vector<Point> changed = board.changePieces( move.x, move.y, players.get(player_turn).type);
		boardRenderer.setChanged( changed );
		
		updateCounters();
		
		changeTurn();
		
		boardRenderer.legalMoves.clear();
		
		if(board.isFinished()) {
			state = GameScene.FINISHED_GAME;
			int winner = board.getWinner();
			
			if(num_players == 2) {
				switch(winner) {
				case 0:
					message = context.getResources().getString(R.string.player_tie);
					break;
				case 1:
					message = context.getResources().getString(R.string.black_wins);
					break;
				case 2:
					message = context.getResources().getString(R.string.white_wins);
					break;
				}
			} else {
				switch(winner) {
				case 0:
					message = context.getResources().getString(R.string.player_tie);
					break;
				case 1:
					message = context.getResources().getString(R.string.player_wins);
					break;
				case 2:
					message = context.getResources().getString(R.string.player_loses);
					break;
				}

			}
					
					
			boardRenderer.blinkWinner();
			gameView.sound.playShortResource(R.raw.level_cleared);
		} else {
			if( board.getLegalMoves(players.get(player_turn).type).size() == 0 ) {
				changeTurn();
			}

			updateLegalMoves();
		}
	}
	
	@Override
	public void render(Canvas c) {
		while(render_lock);
		
		render_lock = true;
		
		c.drawColor( 0xff387400 );

    	background_top.render(c);
    	background_bottom.render(c);

    	black_score.render(c);
    	white_score.render(c);

    	boardRenderer.render(c);
    	if(message != null)
    		drawMessage(c, message);
    	
    	render_lock = false;
	}

	

    private void drawMessage(Canvas canvas, String text) {
    	Paint menuRectPaint = new Paint();
    	menuRectPaint.setStyle(Paint.Style.FILL);
    	menuRectPaint.setColor( Color.argb(127, 0, 0, 0) );

    	Rect box = new Rect(
    			screenW * 2 / 20,
    			screenH * 7 / 20,
    			screenW * 18 / 20,
    			screenH * 12 / 20);

    	text += "\n\n"+context.getResources().getString( R.string.tap_to_restart );

    	int padding = screenW / 30;
    	int textX = box.left + padding;
    	int textW = box.right - textX - padding * 2;
    	StaticLayout mTextLayout = new StaticLayout(text, textPaint, textW, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
    	int textH = mTextLayout.getHeight();
    	
    	box.top = screenH / 2 - textH / 2 - padding;
    	box.bottom = box.top + textH + padding*2;
    	int textY = box.top + padding;

    	RectF boxf = new RectF(box);
    	
    	int height = box.bottom - box.top;

        canvas.drawRoundRect( boxf, 10, 10, menuRectPaint);
        
    	canvas.save();
    	canvas.translate(textX, textY);
    		mTextLayout.draw(canvas);
    	canvas.restore();

    }

	public boolean onBackPressed() {
		gameView.setScene(Game.Scenes.MENU);
		
		return true;
	}
	
	
	public void checkBoard() {
		if(board.isFinished()) {
			state = GameScene.FINISHED_GAME;
		}
	}
	
	@Override
	public void touchUp( int x, int y) {
		super.touchUp(x, y);
		
		if(state == GameScene.FINISHED_GAME) {
			restart();
			return;
		}
		
	}

	
}
