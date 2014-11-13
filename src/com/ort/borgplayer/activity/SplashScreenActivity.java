package com.ort.borgplayer.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;

import com.ort.borgplayer.MainActivity;
import com.ort.borgplayer.R;

public class SplashScreenActivity extends Activity {
	
	// Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 6000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
 
        setContentView(R.layout.splash_screen);
 
        final MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.borg_flyby);
        mp.start();
        
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
 
            	mp.stop();
                // Start the next activity
                Intent mainIntent = new Intent().setClass(
                        SplashScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
 
                // Close the activity so the user won't able to go back this
                // activity pressing Back button
                finish();
            }
        };
 
        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }

}
