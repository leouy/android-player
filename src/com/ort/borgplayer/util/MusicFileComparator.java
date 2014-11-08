package com.ort.borgplayer.util;

import java.util.Comparator;

import com.ort.borgplayer.domain.MusicFile;


public class MusicFileComparator implements Comparator<MusicFile> {

	@Override
	public int compare(MusicFile lhs, MusicFile rhs) {
		int artistCompare = lhs.getArtist().compareTo(rhs.getArtist());
		if (artistCompare == 0) 
			return lhs.getTitle().compareTo(rhs.getTitle());
		else
			return artistCompare;
	}

}
