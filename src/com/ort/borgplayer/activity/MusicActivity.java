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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;

import com.ort.borgplayer.R;
import com.ort.borgplayer.domain.MusicFile;
import com.ort.borgplayer.domain.TaggedSong;
import com.ort.borgplayer.persistence.LocalDb;
import com.ort.borgplayer.service.MusicService;
import com.ort.borgplayer.service.MusicService.MusicBinder;
import com.ort.borgplayer.util.LocationHelper;
import com.ort.borgplayer.util.MusicFileComparator;
import com.ort.borgplayer.widget.MusicController;
import com.ort.borgplayer.widget.MusicListAdapter;

public class MusicActivity extends Activity implements MediaPlayerControl {

	private List<MusicFile> musicList;

	private ListView musicListView;

	private ImageView btnGeoLoc;

	private ImageView btnLyrics;

	private MusicService musicService;

	private Intent playIntent;

	// Media Controller
	private MusicController musicController;

	// Bandera indica la pausa de la actividad
	private boolean appPaused = false;

	// Bandera indica la pausa del playback
	private boolean musicPaused = false;

	// Bandera que indica si la actividad esta unida al servicio
	private boolean musicBound = false;

	private int currentPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Obtiene los elementos de la vista
		musicListView = (ListView) findViewById(R.id.lista_canciones);
		btnGeoLoc = (ImageView) findViewById(R.id.localizarCancion);
		btnLyrics = (ImageView) findViewById(R.id.lyrics);
		
		// Instancia la lista de canciones
		musicList = new ArrayList<MusicFile>();
		
		// Obtiene las canciones
		this.getMusicList();
		
		// Setea los clicklistener de los botones de tagueo y letra
		btnGeoLoc.setOnClickListener(onGeoBotonClick);
		btnLyrics.setOnClickListener(onLyricsBotonClick);
		
		Collections.sort(musicList, new MusicFileComparator());
		
		// Instancia y setea el adapter de la lista
		MusicListAdapter adapter = new MusicListAdapter(this, musicList);
		musicListView.setAdapter(adapter);
		
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
	public void onBackPressed() {
	    // your code.
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
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_end:
			stopService(playIntent);
			musicService = null;
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getMusicList() {
		String artist = this.getIntent().getExtras().getString("artistName");
		String voiceRecogn = this.getIntent().getExtras().getString("voiceInput");
		String idList = this.getIntent().getExtras().getString("idList");

		ContentResolver musicResolver = getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String whereClause = "";
		if (artist != null) {
			whereClause = "artist = '" + artist +"'";
		} else if (voiceRecogn != null) {
			whereClause = "artist like '%" + voiceRecogn +"%' or title like '%" + voiceRecogn +"%'";
		} else if (idList != null) {
			whereClause = "_id in (" + idList + ")";
		} else {
			whereClause = null;
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
			musicCursor.close();
		}
	}

	public void musicFileSelected(View view){
		currentPos = Integer.parseInt(view.getTag(R.string.position).toString());
		musicService.setFile(currentPos);
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
		currentPos++;
		if (musicPaused) {
			setController();
			musicPaused = false;
		}
		musicController.show(0);
	}

	private void playPrevious() {
		musicService.previous();
		currentPos--;
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
			if (musicService.isPlaying()) {
				if (location != null) {
					double lat = location.getLatitude();
					double lon = location.getLongitude();
					MusicFile file = musicList.get(currentPos);
					TaggedSong song = new TaggedSong();
					song.setSongId(file.getId());
					song.setArtist(file.getArtist());
					song.setTitle(file.getTitle());
					song.setLatitude(lat);
					song.setLongitude(lon);
					boolean saved = LocalDb.getInstance(getApplicationContext()).saveTaggedSong(song);
					if (saved)
						Toast.makeText(getApplicationContext(), "Cancion tagueada correctamente!" , Toast.LENGTH_SHORT).show();
					else 
						Toast.makeText(getApplicationContext(), "Ha ocurrido un error, intente nuevamente" , Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "Habilite la ubicación!", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "El reproductor esta pausado.", Toast.LENGTH_SHORT).show();
			}
		}
	};


	/////////////////////////////////////////////////////////////////////////////////
	// Acciones del boton de lyrics
	/////////////////////////////////////////////////////////////////////////////////

	private OnClickListener onLyricsBotonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			MusicFile file = musicList.get(currentPos);
			Intent intent = new Intent(getApplicationContext() , LyricsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.putExtra("artistName", file.getArtist());
			intent.putExtra("songTitle", file.getTitle());
			startActivityForResult(intent, 0);
		}
	};


}
