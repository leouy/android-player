package com.ort.borgplayer.activity;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ort.borgplayer.R;
import com.ort.borgplayer.domain.MusicDto;

public class LyricsActivity extends Activity {

	private TextView artistText;
	
	private TextView titleText;
	
	private TextView text;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lyrics);
		artistText = (TextView) findViewById(R.id.lyrics_view_artist);
		titleText = (TextView) findViewById(R.id.lyrics_view_title);
		text = (TextView) findViewById(R.id.lyrics_view);
		String artist = this.getIntent().getExtras().getString("artistName");
		String song = this.getIntent().getExtras().getString("songTitle");
		RequestParams params = new RequestParams();
		params.add("artist", artist);
		params.add("song", song);
		params.add("fmt", "json");
		invokeWS(params);
	}

	public void invokeWS(RequestParams params){
		// Make RESTful webservice call using AsyncHttpClient object
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://lyrics.wikia.com/api.php?func=getSong",params ,new AsyncHttpResponseHandler() {
			// When the response returned by REST has Http response code '200'
			@Override
			public void onSuccess(String response) {
				try {
					// Parse response to JSON
					String song = response.substring(response.indexOf("=") + 1).trim();
					song = song.replace("'", "\"");
					// JSON Object
					ObjectMapper mapper = new ObjectMapper();
					mapper.configure(Feature.INDENT_OUTPUT, true);
					MusicDto dto = mapper.readValue(song, MusicDto.class);
					// When the JSON response has status boolean value assigned with true
					if(dto.getLyrics().equals("Not found")){
						Toast.makeText(getApplicationContext(), "Lyrics " + dto.getLyrics(), Toast.LENGTH_LONG).show();
					} 
					// Else display error message
					else{
						Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
						artistText.setText(dto.getArtist());
						titleText.setText(dto.getSong());
						text.setText(dto.getLyrics());
					}
				} catch (JsonParseException e) {
					e.getMessage();
				} catch (JsonMappingException e) {
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
					e.printStackTrace();

				}
			}
			
			// When the response returned by REST has Http response code other than '200'
			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				// When Http response code is '404'
				if(statusCode == 404){
					Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
				} 
				// When Http response code is '500'
				else if(statusCode == 500){
					Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
				} 
				// When Http response code other than 404, 500
				else{
					Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	

}
