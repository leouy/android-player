package com.ort.borgplayer.activity;

import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ort.borgplayer.R;
import com.ort.borgplayer.domain.TaggedSong;
import com.ort.borgplayer.persistence.LocalDb;
import com.ort.borgplayer.util.LocationHelper;

public class MusicGeoTagActivity extends Activity {

	private GoogleMap googleMap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_tag_map);
		
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		this.centerOnMyLocation();
		this.addTaggedSongsMarkers();
	}
	
	private void centerOnMyLocation() {
		googleMap.setMyLocationEnabled(true);

		Location location = LocationHelper.getLocation(this);
		if (location != null) {
			LatLng myLocation = null;
			if (location != null) {
				myLocation = new LatLng(location.getLatitude(),	location.getLongitude());
			}
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));
		}
	}
	
	private void addTaggedSongsMarkers() {
		LocalDb db = LocalDb.getInstance(getApplicationContext());
		List<TaggedSong> taggedSongs = db.getTaggedSongs();
		
		for (TaggedSong taggedSong : taggedSongs) {
			MarkerOptions mo = new MarkerOptions();
			LatLng latlng = new LatLng(taggedSong.getLatitude(), taggedSong.getLongitude());
			String markerText = taggedSong.getArtist() + " : " + taggedSong.getTitle();
			mo.position(latlng);
			mo.title(markerText);
			googleMap.addMarker(mo);
		}
		
	}
	
	
}
