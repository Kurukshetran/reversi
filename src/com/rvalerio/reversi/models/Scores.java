package com.rvalerio.reversi.models;

import java.util.ArrayList;

import android.graphics.Rect;

import com.rvalerio.reversi.widgets.ButtonComponent;

public class Scores {
	public Integer numWins = 0, numTies = 0, numLoses = 0;
	private ButtonComponent ball;
	private ButtonComponent name;
	private ButtonComponent wins;
	private ButtonComponent ties;
	private ButtonComponent loses;
	
	public Scores() {
		ball = new ButtonComponent("", 0, 0, 0, new Rect());
    	name = new ButtonComponent("", 0, 0, 0, new Rect());
    	wins = new ButtonComponent("", 0, 0, 0, new Rect());
    	ties = new ButtonComponent("", 0, 0, 0, new Rect());
    	loses = new ButtonComponent("", 0, 0, 0, new Rect());
	}
}
