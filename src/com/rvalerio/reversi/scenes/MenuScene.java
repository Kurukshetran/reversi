package com.rvalerio.reversi.scenes;

import java.util.Random;
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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;


public class MenuScene extends BaseScene {
	private enum State { SHOWING_MAIN_MENU, SHOWING_PLAY_MENU }
    
    private State state;
    
	private static final String TAG = MenuScene.class.getSimpleName();
	private int text_size;
	
	private Vector<Player> players;
	public Board board;
	public BoardRenderer boardRenderer;
	private int player_turn, num_players = 2;

	private int screen_smallest;
	private int padding, menu_padding, menu_height, selected_menu = -1;

	private ImageComponent background_top, background_bottom;
	private ButtonComponent [] menu_main_item, menu_play_item;
	private ButtonComponent more_info;
	private ButtonComponent mute_sound;
	private ButtonComponent select_ball;
	private Vector<ButtonComponent> reversi;
	
	private Bitmap im_sound_on, im_sound_off;

	
	public MenuScene(Context context, Game game) {
		super(context, game);
        
        this.background_top = new ImageComponent(context, R.drawable.wood_cr);
        this.background_bottom = new ImageComponent(context, R.drawable.wood_cr);

        this.more_info = null;
        this.mute_sound = null;
        

		this.num_players = 2;
		
    	board = new Board(8, 8, 2);
    	boardRenderer = new BoardRenderer(context.getResources(), board);
    
    	Player.reset();
    	
    	players = new Vector<Player>();
		players.add( new AIPlayerSimple( board ));
		players.add( new AIPlayerSimple( board ) );
		
		reversi = new Vector<ButtonComponent>();
	}
	
	
	@Override
	public void postCalcDimensions() {
        state = State.SHOWING_MAIN_MENU;
        transitionRollIn();
		// we start our animation after starting the menu scene
	}
	
	@Override
	public void oneTimeInit() {
        restart();
	}
	
	
	@Override
    public void calcDimensions() {
    	screen_smallest = screenW < screenH? screenW: screenH;

    	text_size = screen_smallest / 20;

		padding = text_size * 3 / 2;
		menu_height = text_size * 5 / 2;
		menu_padding = text_size / 2 + menu_height;

		//xPos = 0;
		//yPos = (int) (screenH * 30 / 100 + padding);

		int unit = screenW * 16 / 100;
		int bp = unit / 10;
    	int bx = unit - bp*2;
    	int by = unit - bp*2;

    	more_info = new ButtonComponent( 
    			context.getResources().getString( R.string.question_mark ),
    			Color.BLACK, Color.WHITE, text_size,
    			new Rect( screenW - bx - bp,
    					screenH - by - bp,
    					 screenW - bp,
    					 screenH - bp));

    	im_sound_on = BitmapFactory.decodeResource( context.getResources(), R.drawable.sound_on, null );
    	im_sound_off = BitmapFactory.decodeResource( context.getResources(), R.drawable.sound_off, null );
    	
    	mute_sound = new ButtonComponent( 
				gameView.sound.isMuted()? im_sound_off: im_sound_on,
    			Color.BLACK, Color.WHITE, text_size,
    			new Rect( bp,
    					screenH - by - bp,
    					 bp + bx,
    					 screenH - bp));

    	createMenuEntries();

    	//boardRenderer.setupPositions(screenW, screenW+screenW*32/100);
    	boardRenderer.setupPositions(screenW, screenH);

    	background_top.setSourceFullRect();
    	background_top.setPositionRect(0, 0, screenW, screenW*16/100);

    	background_bottom.setSourceFullRect();
    	background_bottom.setPositionRect(0, screenH - screenW*16/100, screenW, screenH);

    	int text_size = screenW * 6/100;
    	int spacingy = screenW*16 / 1000;
    	int spacingx = 0; //spacingy;
    	int ball_size = screenW * 16/100 - spacingy*2;
    	String reversi_str = context.getResources().getString(R.string.app_name);
    	int l = reversi_str.length();
    	
    	int reversi_x = screenW / 2 - (ball_size * l + spacingx * (l-1))/2;
    	int reversi_y = spacingy;
    	
    	int type = Cell.WHITE;
    	int tcolor = Color.BLACK;
    	for(int i=0; i<l; i++) {
    		type = type == Cell.WHITE? Cell.BLACK: Cell.WHITE;
    		tcolor = tcolor == Color.BLACK? Color.WHITE: Color.BLACK;
    		
    		String c = Character.toString(reversi_str.charAt(i));
	    	ButtonComponent b = new ButtonComponent(boardRenderer.pieces.get(type).image, 0xffffffff, tcolor, text_size, new Rect(reversi_x, reversi_y, reversi_x + ball_size, reversi_y + ball_size));
	    	b.setDrawRect(false);
	    	b.setText(c);
	    	reversi.add(b);
	    	
	    	reversi_x += spacingx + ball_size;
    	}
    	

	}


    protected void transitionChangeState() {
		if(state == State.SHOWING_MAIN_MENU) {
			if(selected_menu > -1) {
				menu_main_item[selected_menu].fg_color = Color.WHITE;
				menu_main_item[selected_menu].bg_color = Color.BLACK;
			}

			state = State.SHOWING_PLAY_MENU;
		} else {
			if(selected_menu > -1) {
				menu_play_item[selected_menu].fg_color = Color.WHITE;
				menu_play_item[selected_menu].bg_color = Color.BLACK;
			}

			state = State.SHOWING_MAIN_MENU;
		}
		
		selected_menu = -1;
    }
    

    
    private void createMenuEntries() {
    	int cx = screenW / 2;
    	int chw = text_size * 5;
    	int chh = menu_height / 2;

    	int []menu_main_string_ids = {R.string.play_1p, R.string.play_2p, R.string.rate_us, R.string.more_games, R.string.exit};
    	int []menu_play_string_ids = {R.string.easy, R.string.normal};

    	int main_length = menu_main_string_ids.length;
    	int play_length = menu_play_string_ids.length;
    	
    	int yPos1 = screenH / 2 - (menu_padding * main_length - text_size / 2)/2;
    	int yPos2 = screenH / 2 - (menu_padding * play_length - text_size / 2)/2;

        this.menu_main_item = new ButtonComponent[menu_main_string_ids.length];
        this.menu_play_item = new ButtonComponent[menu_play_string_ids.length];

    	for(int i=0; i<main_length; i++) {
	    	menu_main_item[i] = new ButtonComponent( 
	    			context.getResources().getString( menu_main_string_ids[i] ), 
	    			0x99000000, 0xffffffff, text_size, 
	    			new Rect(cx - chw, yPos1+menu_padding*i, cx + chw, yPos1+menu_padding*i + menu_height)
	    		);
    	}

    	for(int i=0; i<play_length; i++) {
	    	menu_play_item[i] = new ButtonComponent( 
	    			context.getResources().getString( menu_play_string_ids[i] ), 
	    			0x99000000, 0xffffffff, text_size, 
	    			new Rect(cx - chw, yPos2+menu_padding*i, cx + chw, yPos2+menu_padding*i + menu_height)
	    		);
    	}

    	int select_y = yPos2+menu_padding*play_length + text_size/2;
    	int ball_size = screenW / 5;

    	select_ball = new ButtonComponent(boardRenderer.pieces.get(Game.selected_ball).image, 0xffffffff, 0xffffffff, 0, new Rect(cx - ball_size/2, select_y, cx + ball_size/2, select_y + ball_size));
    	select_ball.setDrawRect(false);

    }
    
    
	
	public void restart() {
		board.setup();
		boardRenderer.winner = -1;
		boardRenderer.timer = -1;
		
		setPlayerTurn( 0 );
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
		super.update(time);

		if(board.isFinished()) {
			boardRenderer.updateBlinking();
			
			if( boardRenderer.finishedBlinking()) {
				restart();
			}

			return;
		}
		
		Point move = players.get(player_turn).getMove();

		if(move == null)
			return;
		
		board.changePieces( move.x, move.y, players.get(player_turn).type);

		changeTurn();

		if(board.isFinished()) {
			boardRenderer.blinkWinner();
		} else {
			if( board.getLegalMoves(players.get(player_turn).type).size() == 0 ) {
				changeTurn();
			}
		}
	}

    
    private void drawMenuMain(Canvas canvas) {
    	for(int i=0; i<menu_main_item.length; i++)
    		menu_main_item[i].render(canvas);
    }

    
    private void drawMenuPlayGame(Canvas canvas) {
    	for(int i=0; i<menu_play_item.length; i++)
    		menu_play_item[i].render(canvas);
    	
    	select_ball.render(canvas);
    }

    
    private void drawMenu(Canvas canvas) {
    	if(state == State.SHOWING_PLAY_MENU)
    		drawMenuPlayGame(canvas);
		else if(state == State.SHOWING_MAIN_MENU)
    		drawMenuMain(canvas);
    }
    
    
	@Override
	public void render(Canvas c) {
    	c.drawColor( 0xff3a7600 );

    	background_top.render(c);
    	background_bottom.render(c);
    	
    	for(int i=0; i<reversi.size(); i++)
    		reversi.get(i).render(c);
    	
    	boardRenderer.render(c);
    	
    	//more_info.render(c);
    	mute_sound.render(c);
    	
    	translatePush(c);
	    	drawMenu(c);
    	translatePop(c);

    	//long mem = getUsedMemorySize();
    	//Log.i("memory", "memory: "+mem);
	}

	public static long getUsedMemorySize() {

	    long freeSize = 0L;
	    long totalSize = 0L;
	    long usedSize = -1L;
	    try {
	        Runtime info = Runtime.getRuntime();
	        freeSize = info.freeMemory();
	        totalSize = info.totalMemory();
	        usedSize = totalSize - freeSize;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return usedSize;

	}
	
	public boolean onBackPressed() {
		if(isTransitioning()) return true;
		
		if(state == State.SHOWING_PLAY_MENU) {
			transitionStart( Game.Scenes.SAME );
		}
		
		return true;
	}

	
	private void checkPlayMenuPress(int x, int y) {
		int selected = -1;

		if(select_ball.isTouched(x, y)) {
			if(Game.selected_ball == Cell.BLACK)
				Game.selected_ball = Cell.WHITE;
			else
				Game.selected_ball = Cell.BLACK;
			
	    	select_ball.setBitmap( boardRenderer.pieces.get(Game.selected_ball).image );
	    	
			
			return;
		}
		
		for(int i=0; i<menu_play_item.length; i++) {
			if( menu_play_item[i].isTouched(x, y) ) {
				selected = i;
				selected_menu = i;

				menu_play_item[i].bg_color = Color.WHITE;
				menu_play_item[i].fg_color = Color.BLACK;

				break;
			}
		}
		
		if(selected < 0) return;

		switch(selected) {
		case 0:
			Game.difficulty = Game.Difficulty.EASY;
			break;
		case 1:
			Game.difficulty = Game.Difficulty.INTERMEDIATE;
			break;
		default:
			
		}

		transitionStart(Game.Scenes.GAME);
		
	}
	
	
	private void setPlayersNumber( int n ) {
		gameView.setPlayersNumber( n );
	}
	
	
	private void checkMainMenuPress(int x, int y) {
		int selected = -1;
		
		for(int i=0; i<menu_main_item.length; i++) {
			if( menu_main_item[i].isTouched(x, y) ) {
				selected = i;
				
				selected_menu = i;
				menu_main_item[i].bg_color = Color.WHITE;
				menu_main_item[i].fg_color = Color.BLACK;
				break;
			}
		}
		
		if(selected < 0) return;
		
		switch(selected) {
		case 0:
			setPlayersNumber(1);
			transitionStart( Game.Scenes.SAME );

			break;
		case 1:
			setPlayersNumber(2);
			transitionStart( Game.Scenes.GAME );

			break;
		case 2:
			String appName = "com.rvalerio.reversi";
			
			try {
			    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appName)));
			} catch (android.content.ActivityNotFoundException anfe) {
				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+appName)));
			}

			break;
		case 3:
			String devName = "Ricardo Valério";
			
			try {
			    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:"+devName)));
			} catch (android.content.ActivityNotFoundException anfe) {
				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=pub:"+devName)));
			}

			break;
		case 4:
			exitGame();
			break;
		default:
			
		}
	}

	
	public void checkMuteSoundPress(int x, int y) {
		if( mute_sound.isTouched(x, y) ) {
			if( gameView.sound.isMuted() ) {
				gameView.sound.unmute();
				mute_sound.setBitmap(im_sound_on);
			} else {
				gameView.sound.mute();
				mute_sound.setBitmap(im_sound_off);
			}
		}
	}
	
	
	public void checkMoreInfoPress(int x, int y) {
/*
		if( more_info.isTouched(x, y) )
			transitionStart(Game.SCENE_ABOUT);
*/
	}
	
	
	public void touchDown(int x, int y) {
		if(isTransitioning()) return;

		checkMoreInfoPress(x, y);

		checkMuteSoundPress(x, y);

		if(state == State.SHOWING_MAIN_MENU)
			checkMainMenuPress(x, y);
		else if(state == State.SHOWING_PLAY_MENU)
			checkPlayMenuPress(x, y);

	}
	
}
