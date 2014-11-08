package com.ort.borgplayer.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ort.borgplayer.R;
import com.ort.borgplayer.domain.GridArtistFile;

public class GridAdapter extends BaseAdapter {

	private List<GridArtistFile> artistList;
	
	private LayoutInflater gridInflater;
	
	public GridAdapter(Context c, List<GridArtistFile> artistList) {
        this.artistList = artistList;
        this.gridInflater = LayoutInflater.from(c);
    }
	
	@Override
	public int getCount() {
		return artistList.size();
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
			convertView = this.gridInflater.inflate(R.layout.album_art, parent, false);
			// Set the view holder
			holder = new ViewHolder();
			holder.artistName = (TextView)convertView.findViewById(R.id.artistName);
			holder.albumArt = (ImageView)convertView.findViewById(R.id.albumArt);
			// Set holder as tag
			convertView.setTag(R.string.holder, holder);
		} else {
			holder = (ViewHolder) convertView.getTag(R.string.holder);
		}
		// Get music file by position
		GridArtistFile currentFile = artistList.get(position);
		convertView.setTag(R.string.grid_artist, currentFile.getArtist());
		convertView.setTag(R.string.grid_position, position);
		if (currentFile != null) {
			// Get title and artist
			Bitmap albumArtBitmap = BitmapFactory.decodeFile(currentFile.getPath());
			holder.albumArt.setImageBitmap(albumArtBitmap);
			holder.artistName.setText(currentFile.getArtist());
		}

		return convertView;
	}
	
	public List<GridArtistFile> getArtistList() {
		return artistList;
	}

	public void setArtistList(List<GridArtistFile> artistList) {
		this.artistList = artistList;
	}

	static class ViewHolder {
		TextView artistName;
		ImageView albumArt;
	}

	
}
