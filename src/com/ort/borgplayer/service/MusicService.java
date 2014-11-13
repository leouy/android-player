package com.ort.borgplayer.service;

import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.ort.borgplayer.MainActivity;
import com.ort.borgplayer.R;
import com.ort.borgplayer.domain.MusicFile;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, 
MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

	private MediaPlayer player;

	private List<MusicFile> musicList;

	private int musicPos;

	private final IBinder musicBind = new MusicBinder();

	private String musicTitle = "";

	private static final int NOTIFY_ID = 1;

	@Override
	public void onCreate() {
		super.onCreate();
		musicPos = 0;
		player = new MediaPlayer();
		initMusicPlayer();
	}

	public void initMusicPlayer(){
		player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
	}

	public void setList(List<MusicFile> musicList){
		this.musicList = musicList;
	}

	public class MusicBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return musicBind;
	}

	@Override
	public boolean onUnbind(Intent intent){
		player.stop();
		player.reset();
		player.release();
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		if(arg0.getCurrentPosition() > 0){
			arg0.reset();
			next();
		}
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		arg0.reset();
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onPrepared(MediaPlayer player) {
		Intent notIntent = new Intent(this, MainActivity.class);
		notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendInt = PendingIntent.getActivity(this, 0,
				notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification not = new Notification.Builder(this)
		.setContentIntent(pendInt)
		.setSmallIcon(R.drawable.play_not)
		.setTicker(musicTitle)
		.setOngoing(true)
		.setContentTitle("Playing")
		.setContentText(musicTitle)
		.getNotification();

		startForeground(NOTIFY_ID, not);
		player.start();
	}

	public void setFile(int listIndex){
		musicPos = listIndex;
	}

	public void playFile(){
		player.reset();
		MusicFile playFile = musicList.get(musicPos);
		musicTitle = playFile.getTitle();
		long current = playFile.getId();
		Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, current);
		try{
			player.setDataSource(getApplicationContext(), trackUri);
		}
		catch(Exception e){
			Log.e("MUSIC SERVICE", "Error setting data source", e);
		}
		player.prepareAsync();
	}

	@Override
	public void onDestroy() {
		stopForeground(true);
	}

	///////////////////////////////////////////////////////////
	// Activity playback methods
	///////////////////////////////////////////////////////////

	public int getPosition(){
		return player.getCurrentPosition();
	}

	public int getDuration(){
		return player.getDuration();
	}

	public boolean isPlaying(){
		return player.isPlaying();
	}

	public void pausePlayer(){
		player.pause();
	}

	public void seek(int posn){
		player.seekTo(posn);
	}

	public void go(){
		player.start();
	}

	public void previous(){
		musicPos--;
		if(musicPos < 0) 
			musicPos = musicList.size()-1 ;
		playFile();
	}

	public void next(){
		musicPos++;
		if(musicPos >= musicList.size()) 
			musicPos = 0;
		playFile();
	}

}
