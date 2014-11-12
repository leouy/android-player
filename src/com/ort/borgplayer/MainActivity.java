package com.ort.borgplayer;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.ort.borgplayer.activity.MusicActivity;
import com.ort.borgplayer.activity.MusicGeoTagActivity;
import com.ort.borgplayer.activity.VoiceRecognitionActivity;
import com.ort.borgplayer.domain.GridArtistFile;
import com.ort.borgplayer.persistence.LocalDb;
import com.ort.borgplayer.widget.GridAdapter;


public class MainActivity extends Activity {

	private List<GridArtistFile> albumArt;

	@SuppressLint("UseSparseArrays") @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_gridview);
		albumArt = new ArrayList<GridArtistFile>();
		this.getAlbumArt();
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new GridAdapter(this, albumArt));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				String artist = v.getTag(R.string.grid_artist).toString();				
				Intent intent = new Intent(getApplicationContext() , MusicActivity.class);
				intent.putExtra("artistName", artist);
				startActivityForResult(intent, 0);				
			}
		});

		ImageView TagsView = (ImageView) findViewById(R.id.Tags);
		TagsView.setOnClickListener(onGeoClick);
		TagsView = (ImageView) findViewById(R.id.VoiceRecognition);
		TagsView.setOnClickListener(onVoiceClick);
		TagsView = (ImageView) findViewById(R.id.seeMap);
		TagsView.setOnClickListener(onMapClick);
	}

	private OnClickListener onGeoClick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Intent intent = new Intent(getApplicationContext() , MusicActivity.class);
			intent.putExtra("idList", LocalDb.getInstance(getApplicationContext()).getTaggedSongsInClause());
			startActivityForResult(intent, 0);

		}
	};

	private OnClickListener onVoiceClick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Intent intent = new Intent(getApplicationContext() , VoiceRecognitionActivity.class);
			startActivityForResult(intent, 0);	
		}
	};

	private OnClickListener onMapClick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Intent intent = new Intent(getApplicationContext() , MusicGeoTagActivity.class);
			startActivityForResult(intent, 0);	
		}
	};

	private void getAlbumArt() {
		ContentResolver musicResolver = getContentResolver();
		String[] projection = {android.provider.MediaStore.Audio.Albums._ID, android.provider.MediaStore.Audio.Albums.ALBUM_ART,
				android.provider.MediaStore.Audio.Artists.ARTIST};
		Cursor albumArtCursor = musicResolver.query(android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection,
				null, null, null);
		if (albumArtCursor != null) {

			while (albumArtCursor.moveToNext()) {
				GridArtistFile file = new GridArtistFile();
				file.setArtist(albumArtCursor.getString(2));
				if (albumArtCursor.getString(1) != null) {
					file.setPath(albumArtCursor.getString(1));
				} else {
					file.setDrawableId(R.drawable.borg_default_artist);
				}
				if (!albumArt.contains(file)) {
					albumArt.add(file);
				}
			}
		}
	}


}