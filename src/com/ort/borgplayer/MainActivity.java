package com.ort.borgplayer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.ort.borgplayer.domain.MusicFile;
import com.ort.borgplayer.widget.MusicListAdapter;


public class MainActivity extends Activity {

	private List<MusicFile> musicList;
	
	private ListView musicListView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicListView = (ListView) findViewById(R.id.lista_canciones);
        musicList = new ArrayList<MusicFile>();
        this.getMusicList();
        MusicListAdapter adapter = new MusicListAdapter(this, musicList);
        musicListView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void getMusicList() {
    	ContentResolver musicResolver = getContentResolver();
    	Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
    	if (musicCursor != null) {
    		int titulo = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
    		int id = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
    		int artista = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
    		while (musicCursor.moveToNext()) {
    			MusicFile file = new MusicFile();
    			file.setId(musicCursor.getLong(id));
    			file.setTitle(musicCursor.getString(titulo));
    			file.setArtist(musicCursor.getColumnName(artista));
    			musicList.add(file);
    		}
    	}
    }
    
    
}
