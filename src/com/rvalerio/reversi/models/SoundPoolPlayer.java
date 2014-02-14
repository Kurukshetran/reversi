package com.rvalerio.reversi.models;

import java.util.HashMap;

import com.rvalerio.reversi.R;
import com.rvalerio.reversi.R.raw;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPoolPlayer {
	private boolean play_sounds = true;
	
    private SoundPool mShortPlayer= null;
    private HashMap mSounds = new HashMap();

    public SoundPoolPlayer(Context pContext)
    {
        // setup Soundpool
        this.mShortPlayer = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);

        mSounds.put(R.raw.level_cleared, this.mShortPlayer.load(pContext, R.raw.level_cleared, 1));
    }
    
    
    public boolean isMuted() { return !play_sounds; }
    
    public void mute() { play_sounds = false; }
    
    public void unmute() { play_sounds = true; }

    public void playShortResource(int piResource) {
    	if(!play_sounds) return;
    	
        int iSoundId = (Integer) mSounds.get(piResource);
        this.mShortPlayer.play(iSoundId, 0.99f, 0.99f, 0, 0, 1);
    }

    // Cleanup
    public void release() {
        // Cleanup
        this.mShortPlayer.release();
        this.mShortPlayer = null;
    }
}