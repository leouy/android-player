package com.ort.borgplayer.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ort.borgplayer.R;
import com.ort.borgplayer.domain.MusicFile;

public class MusicListAdapter extends BaseAdapter {

	private List<MusicFile> musicFiles;
	
	private LayoutInflater musicInflater;
	
	public MusicListAdapter(Context context, List<MusicFile> musicFiles) {
		this.musicFiles = musicFiles;
		this.musicInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return this.musicFiles.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		holder = new ViewHolder();
		//map to song layout
		if (convertView == null)
			convertView = this.musicInflater.inflate(R.layout.music, parent, false);

		//get title and artist views
		holder.songTitle = (TextView)convertView.findViewById(R.id.song_title);
		holder.songArtist = (TextView)convertView.findViewById(R.id.song_artist);

		//get song using position
		MusicFile currentFile = musicFiles.get(position);
		//get title and artist strings
		holder.songTitle.setText(currentFile.getTitle());
		holder.songArtist.setText(currentFile.getArtist());
		//set position as tag
		convertView.setTag(position);
		return convertView;
	}
	
	static class ViewHolder {
		TextView songTitle;
		TextView songArtist;
	}

}
