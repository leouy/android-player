package com.ort.borgplayer.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;

import com.ort.borgplayer.R;
import com.ort.borgplayer.domain.MusicFile;
import com.ort.borgplayer.service.MusicService;
import com.ort.borgplayer.service.MusicService.MusicBinder;
import com.ort.borgplayer.util.LocationHelper;
import com.ort.borgplayer.util.MusicFileComparator;
import com.ort.borgplayer.widget.MusicController;
import com.ort.borgplayer.widget.MusicListAdapter;

public class MusicActivity extends Activity implements MediaPlayerControl {

	private List<MusicFile> musicList;

	private ListView musicListView;

	private Button btnGeoLoc;
	
	private MusicService musicService;

	private Intent playIntent;

	private MusicController musicController;

	private boolean appPaused = false;

	private boolean musicPaused = false;

	private boolean musicBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list);
		musicListView = (ListView) findViewById(R.id.lista_canciones);
		musicList = new ArrayList<MusicFile>();
		btnGeoLoc = (Button) findViewById(R.id.localizarCancion);		
		this.getMusicList();
		MusicListAdapter adapter = new MusicListAdapter(this, musicList);
		musicListView.setAdapter(adapter);
		
		btnGeoLoc.setOnClickListener(onGeoBotonClick);
		Collections.sort(musicList, new MusicFileComparator());
		this.setController();
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

	@Override
	public void onPause() {
		super.onPause();
		appPaused = true;
	}

	@Override
	protected void onResume(){
		super.onResume();
		if(appPaused){
			setController();
			appPaused = false;
		}
	}

	@Override
	protected void onStop() {
		musicController.hide();
		super.onStop();
	}

	// Conectar al service
	private ServiceConnection musicConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MusicBinder binder = (MusicBinder) service;
			musicService = binder.getService();
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
		String artist = this.getIntent().getExtras().getString("artistName");
		String voiceRecogn = this.getIntent().getExtras().getString("voiceInput");
		
		ContentResolver musicResolver = getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String whereClause = "";
		if(artist != null){
			 whereClause = "artist = '" + artist +"'";
		}else{
			 whereClause = "artist like '%" + voiceRecogn +"%' or title like '%" + voiceRecogn +"%'";
		}
		
		Cursor musicCursor = musicResolver.query(musicUri, null, whereClause, null, null);
		if (musicCursor != null) {
			int titulo = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int id = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			int artista = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
			while (musicCursor.moveToNext()) {
				MusicFile file = new MusicFile();
				file.setId(musicCursor.getLong(id));
				file.setTitle(musicCursor.getString(titulo));
				file.setArtist(musicCursor.getString(artista));
				musicList.add(file);
			}
		}
	}

	public void musicFileSelected(View view){
		musicService.setFile(Integer.parseInt(view.getTag(R.string.position).toString()));
		musicService.playFile();
		if(musicPaused){
		    setController();
		    musicPaused = false;
		  }
		  musicController.show(0);
	}

	private void setController(){
		musicController = new MusicController(this);
		musicController.setPrevNextListeners(next, prev);
		musicController.setMediaPlayer(this);
		musicController.setAnchorView(findViewById(R.id.lista_canciones));
		musicController.setEnabled(true);
	}

	private OnClickListener next = new OnClickListener() {
		@Override
		public void onClick(View v) {
			playNext();
		}
	};

	private OnClickListener prev = new OnClickListener() {
		@Override
		public void onClick(View v) {
			playPrevious();
		}
	};

	private void playNext() {
		musicService.next();
		if (musicPaused) {
			setController();
			musicPaused = false;
		}
		musicController.show(0);
	}

	private void playPrevious() {
		musicService.previous();
		if (musicPaused) {
			setController();
			musicPaused = false;
		}
		musicController.show(0);
	}

	/////////////////////////////////////////////////////////////////////////////////
	// Acá empiezan los métodos de MediaPlayerControl
	/////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void onDestroy() {
		stopService(playIntent);
		musicService = null;
		super.onDestroy();
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		if (musicService != null && musicBound && musicService.isPlaying())
			return musicService.getPosition();
		else
			return 0;
	}

	@Override
	public int getDuration() {
		if (musicService != null && musicBound && musicService.isPlaying())
			return musicService.getDuration();
		else
			return 0;
	}

	@Override
	public boolean isPlaying() {
		if(musicService != null && musicBound)
			return musicService.isPlaying();
		return false;
	}

	@Override
	public void pause() {
		musicPaused = true;
		musicService.pausePlayer();
	}

	@Override
	public void seekTo(int arg0) {
		musicService.seek(arg0);
	}

	@Override
	public void start() {
		musicService.go();		
	}

	/////////////////////////////////////////////////////////////////////////////////
	// Acciones del boton de localizacion
	/////////////////////////////////////////////////////////////////////////////////
	
	private OnClickListener onGeoBotonClick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			Location location = LocationHelper.getLocation(getApplicationContext());
			if (location != null) {
				double lat = location.getLatitude();
				double lon = location.getLongitude();
				Toast.makeText(getApplicationContext(), "Lat:" + lat + " - Long:" + lon , Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "No location providers available!", Toast.LENGTH_SHORT).show();
			}
		}
	};

	
}
