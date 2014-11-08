package com.ort.borgplayer.persistence;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ort.borgplayer.domain.TaggedSong;

public class LocalDb extends SQLiteOpenHelper {

	private static LocalDb instance = null;

	private static final String LOG = "LocalDb";

	// Database name
	private static final String DATABASE_NAME = "BORG_PLAYER_DB";

	// Database version, change when DDL changes
	private static final int DB_VERSION = 1;

	// Tables names
	private static final String TABLE_MARCADORES = "MARCADORES";

	// Column names Marcadores
	private static final String ID = "ID";
	private static final String ARTIST_NAME = "ARTIST_NAME";
	private static final String SONG_NAME = "SONG_NAME";
	private static final String LATITUDE = "LATITUDE";
	private static final String LONGITUDE = "LONGITUDE";
	private static final String CREATION_DATE = "CREATION_DATE";

	public LocalDb(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
	}

	public static LocalDb getInstance(Context ctx) {
		/** 
		 * use the application context as suggested by CommonsWare.
		 * this will ensure that you dont accidentally leak an Activitys
		 * context (see this article for more information: 
		 * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
		 */
		if (instance == null) {
			instance = new LocalDb(ctx.getApplicationContext());
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_MARCADORES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_MARCADORES);
		db.execSQL(CREATE_TABLE_MARCADORES);
	}

	private static final String CREATE_TABLE_MARCADORES = "CREATE TABLE "
			+ TABLE_MARCADORES + "(" + ID + " INTEGER PRIMARY KEY," + ARTIST_NAME
			+ " TEXT," + SONG_NAME + " TEXT," + LATITUDE + " REAL," + LONGITUDE + " REAL," + CREATION_DATE
			+ " DATETIME" + ")";

	public void agregarLocal(TaggedSong song) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();

			if (getTaggedSong(song.getId()) == null)
			{
				values.put(ID, song.getId());
				values.put(ARTIST_NAME, song.getArtist());
				values.put(SONG_NAME, song.getTitle());
				values.put(LATITUDE, song.getLatitude());
				values.put(LONGITUDE, song.getLongitude());
				values.put(CREATION_DATE, new java.util.Date().toString());

				db.insert(TABLE_MARCADORES, null, values);
			}
		} catch (SQLiteException e) {
			Log.e("LOG","Error al abrir la base en agregarLocal" + e.getMessage());
		} catch (Exception ee) {
			Log.e("LOG","Error al insertar un local en AgregarLocal" + ee.getMessage());			
		}
	}

	public TaggedSong getTaggedSong(Long localId) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_MARCADORES + " WHERE "
				+ ID + " = " + localId;

		Log.e(LOG, selectQuery);
		Cursor c = db.rawQuery(selectQuery, null);
		TaggedSong taggedSong = null;

		if (c.getCount() > 0) {
			c.moveToFirst();
			taggedSong = new TaggedSong();
			taggedSong.setId(c.getLong(c.getColumnIndex(ID)));
			taggedSong.setArtist(c.getString(c.getColumnIndex(ARTIST_NAME)));
			taggedSong.setTitle(c.getString(c.getColumnIndex(SONG_NAME)));
			taggedSong.setLatitude(c.getDouble(c.getColumnIndex(LATITUDE)));
			taggedSong.setLongitude(c.getDouble(c.getColumnIndex(LONGITUDE)));
			taggedSong.setCreationDate(Date.valueOf(c.getString(c.getColumnIndex(CREATION_DATE))));
		}

		return taggedSong;
	}

	public List<TaggedSong> getTaggedSongs() {
		List<TaggedSong> taggedSongs = new ArrayList<TaggedSong>();
		String selectQuery = "SELECT  * FROM " + TABLE_MARCADORES;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				TaggedSong taggedSong = new TaggedSong();
				taggedSong = new TaggedSong();
				taggedSong.setId(c.getLong(c.getColumnIndex(ID)));
				taggedSong.setArtist(c.getString(c.getColumnIndex(ARTIST_NAME)));
				taggedSong.setTitle(c.getString(c.getColumnIndex(SONG_NAME)));
				taggedSong.setLatitude(c.getDouble(c.getColumnIndex(LATITUDE)));
				taggedSong.setLongitude(c.getDouble(c.getColumnIndex(LONGITUDE)));
				taggedSong.setCreationDate(Date.valueOf(c.getString(c.getColumnIndex(CREATION_DATE))));

				taggedSongs.add(taggedSong);
			} while (c.moveToNext());
		}
		return taggedSongs;
	}


}
