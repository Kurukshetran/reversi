package com.rvalerio.reversi.models;

import com.rvalerio.reversi.R;

import android.content.Context;
import android.content.SharedPreferences;


public class GameData {
    private SharedPreferences mPrefsManager = null;

    public GameData(Context pContext)
    {
    	mPrefsManager = pContext.getSharedPreferences( pContext.getResources().getString(R.string.preferences_key), Context.MODE_PRIVATE);
    }


    public void writeLong(String key, Long value) {
    	SharedPreferences.Editor editor = mPrefsManager.edit();
    	editor.putLong(key, value);
    	editor.commit();
    }
    
    public void writeInt(String key, Integer value) {
    	SharedPreferences.Editor editor = mPrefsManager.edit();
    	editor.putInt(key, value);
    	editor.commit();
    }
    
    public Long readLong(String key) {
    	return mPrefsManager.getLong(key, -1L);
    }
    
    public Integer readInt(String key) {
    	return mPrefsManager.getInt(key, -1);
    }
}