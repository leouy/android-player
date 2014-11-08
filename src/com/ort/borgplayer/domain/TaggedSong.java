package com.ort.borgplayer.domain;

import java.sql.Date;

public class TaggedSong {

	private Long id;
	
	private String artist;

	private String title;

	private double latitude;
	
	private double longitude;
	
	private Date creationDate;
	
	public TaggedSong() {}
	
	public String getArtist() {
		return artist;
	}
	
	public Date getCreationDate() {
		return creationDate;
	};

	public Long getId() {
		return id;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getTitle() {
		return title;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	

}
