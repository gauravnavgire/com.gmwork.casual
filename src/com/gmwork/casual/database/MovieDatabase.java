package com.gmwork.casual.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MovieDatabase extends SQLiteOpenHelper {

	// database name and version
	private static final String DATABASE_NAME = "movies.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String CREATE_MOVIES_TABLE = "create table "
			+ ContentDescriptor.Movie.TABLE_NAME + "("
			+ ContentDescriptor.Movie.Column.ID
			+ " integer primary key autoincrement, "
			+ ContentDescriptor.Movie.Column.MOVIE + " text not null, "
			+ ContentDescriptor.Movie.Column.YEAR + " text, "
			+ ContentDescriptor.Movie.Column.HINT + " text);";
	
	private static final String CREATE_HIGHSCORE_TABLE = "create table "
		+ ContentDescriptor.Highscore.TABLE_NAME + "("
		+ ContentDescriptor.Highscore.Column.ID
		+ " integer primary key autoincrement, "
		+ ContentDescriptor.Highscore.Column.PLAYERNAME + " text not null, "
		+ ContentDescriptor.Highscore.Column.MOVEBONUS + " integer, "
		+ ContentDescriptor.Highscore.Column.TIMEBONUS + " integer, "
		+ ContentDescriptor.Highscore.Column.GUESSBONUS + " integer, "
		+ ContentDescriptor.Highscore.Column.TOTALPOINTS + " integer);";

	private static final String TABLE_DROP = "DROP TABLE IF EXISTS ";

	public MovieDatabase(Context context) {
		// mandatory super constructor call
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_MOVIES_TABLE);
		database.execSQL(CREATE_HIGHSCORE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MovieDatabase.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL(TABLE_DROP + ContentDescriptor.Movie.TABLE_NAME);
		onCreate(db);
	}

}
