package com.ort.borgplayer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.ort.borgplayer.activity.LyricsActivity;
import com.ort.borgplayer.activity.VoiceRecognitionActivity;
import com.ort.borgplayer.domain.MusicFile;
import com.ort.borgplayer.service.MusicService;
import com.ort.borgplayer.service.MusicService.MusicBinder;
import com.ort.borgplayer.widget.MusicListAdapter;


public class MainActivity extends Activity {

	private List<MusicFile> musicList;

	private ListView musicListView;

	private MusicService musicService;

	private Intent playIntent;

	private boolean musicBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		musicListView = (ListView) findViewById(R.id.lista_canciones);
		musicList = new ArrayList<MusicFile>();
		this.getMusicList();
		MusicListAdapter adapter = new MusicListAdapter(this, musicList);
		musicListView.setAdapter(adapter);
        Button btnSpeak = (Button) findViewById(R.id.btSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent textActivityIntent = new Intent(v.getContext(), VoiceRecognitionActivity.class);				
				startActivityForResult(textActivityIntent, 0);
			}
		});
       
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(playIntent == null){
			playIntent = new Intent(this, MusicService.class);
			startService(playIntent);
			bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
		}
	}

	// Conectar al service
	private ServiceConnection musicConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MusicBinder binder = (MusicBinder) service;
			//get service
			musicService = binder.getService();
			//pass list
			musicService.setList(musicList);
			musicBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			musicBound = false;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_shuffle:
			//shuffle
			break;
		case R.id.action_end:
			stopService(playIntent);
			musicService = null;
			System.exit(0);
			break;
		case R.id.action_lyrics:
			Intent intent = new Intent(this.getApplicationContext() , LyricsActivity.class);
			startActivityForResult(intent, 0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getMusicList() {
		ContentResolver musicResolver = getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
		if (musicCursor != null) {
			int titulo = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int id = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			int artista = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
			while (musicCursor.moveToNext()) {
				MusicFile file = new MusicFile();
				file.setId(musicCursor.getLong(id));
				file.setTitle(musicCursor.getString(titulo));
				file.setArtist(musicCursor.getColumnName(artista));
				musicList.add(file);
			}
		}
	}

	public void musicFileSelected(View view){
		musicService.setFile(Integer.parseInt(view.getTag(R.string.position).toString()));
		musicService.playFile();
	}

	@Override
	protected void onDestroy() {
		stopService(playIntent);
		musicService = null;
		super.onDestroy();
	}


}
