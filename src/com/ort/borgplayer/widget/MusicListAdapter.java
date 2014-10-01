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

		// ViewHolder pattern
		ViewHolder holder;
		// If convertView is null, inflate it
		if (convertView == null) {
			// Inflate
			convertView = this.musicInflater.inflate(R.layout.music, parent, false);
			// Set the view holder
			holder = new ViewHolder();
			holder.songTitle = (TextView)convertView.findViewById(R.id.song_title);
			holder.songArtist = (TextView)convertView.findViewById(R.id.song_artist);
			// Set holder as tag
			convertView.setTag(R.string.holder, holder);
		} else {
			holder = (ViewHolder) convertView.getTag(R.string.holder);
		}
		// Get music file by position
		MusicFile currentFile = musicFiles.get(position);
		convertView.setTag(R.string.position, position);
		if (currentFile != null) {
			// Get title and artist
			holder.songTitle.setText(currentFile.getTitle());
			holder.songArtist.setText(currentFile.getArtist());
		}
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView songTitle;
		TextView songArtist;
	}

}
