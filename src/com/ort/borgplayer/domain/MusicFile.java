package com.ort.borgplayer.domain;

public class MusicFile {

	private Long id;
	
	private String title;
	
	private String artist;
	
	private String artPath;

	public MusicFile() {}

	public String getArtist() {
		return artist;
	}

	public String getArtPath() {
		return artPath;
	}
	
	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setArtPath(String artPath) {
		this.artPath = artPath;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
