package com.ort.borgplayer.activity;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.ort.borgplayer.R;

public class MusicGeoTagActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_tag_map);
		
		GoogleMap googleMap;
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	}
	
}
