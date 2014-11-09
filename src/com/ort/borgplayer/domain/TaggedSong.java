package com.ort.borgplayer.domain;

import java.sql.Date;

public class TaggedSong {

	private Long _ID;
	
	private Long songId;
	
	private String artist;

	private String title;

	private double latitude;
	
	private double longitude;
	
	private Date creationDate;
	
	public TaggedSong() {}
	
	
	public Long get_ID() {
		return _ID;
	}
	
	public String getArtist() {
		return artist;
	};

	public Date getCreationDate() {
		return creationDate;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public Long getSongId() {
		return songId;
	}

	public String getTitle() {
		return title;
	}

	public void set_ID(Long _ID) {
		this._ID = _ID;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setSongId(Long songId) {
		this.songId = songId;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	

}
