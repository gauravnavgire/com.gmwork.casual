package com.gmwork.casual.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * The onCreate() method is called when the provider is instantiated (by the
 * ContentResolver class). It is, in turn, used to bootstrap the database via
 * the RestaurantDatabase class (see above) instance db. The getType() method
 * uses the ContentDescriptor.URI_MATCHER (see ContentDescriptor above) to
 * lookup the MIME type for a given URI. All of the data access & update methods
 * (including query(), insert(), update(), and delete()) take a URI parameter.
 * The URI provides hints such as the entity (and cardinality) being queried or
 * updated. For instance, in our example, if the URI to passed to the query()
 * method looks like content://com.favrestaurant.contentprovider/restaurants/*
 * the method will return all restaurant rows in the database. This is
 * accomplished by using the ContentDescriptor.URI_MATCHER to determine how to
 * process the URI.
 * 
 * @author gaurav
 * 
 */
public class MovieProvider extends ContentProvider {
	public MovieDatabase mMovieDb;

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		final int match = ContentDescriptor.URI_MATCHER.match(uri);

		switch (match) {
		case ContentDescriptor.Movie.PATH_TOKEN:
			return ContentDescriptor.Movie.CONTENT_TYPE_DIR;
		case ContentDescriptor.Movie.PATH_FOR_ID_TOKEN:
			return ContentDescriptor.Movie.CONTENT_TYPE_ITEM;
		default:
			throw new UnsupportedOperationException("Uri : " + uri
					+ " is not supported");
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mMovieDb.getWritableDatabase();
		int token = ContentDescriptor.URI_MATCHER.match(uri);

		switch (token) {
		case ContentDescriptor.Movie.PATH_TOKEN: {
			long id = db.insert(ContentDescriptor.Movie.TABLE_NAME, null,
					values);
			return ContentDescriptor.Movie.CONTENT_URI.buildUpon()
					.appendPath(String.valueOf(id)).build();
		}
		default:
			throw new UnsupportedOperationException("URI: " + uri
					+ " not supported.");
		}
	}

	@Override
	public boolean onCreate() {
		Context ctx = getContext();
		mMovieDb = new MovieDatabase(ctx);
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mMovieDb.getReadableDatabase();
		int token = ContentDescriptor.URI_MATCHER.match(uri);

		switch (token) {
		case ContentDescriptor.Movie.PATH_TOKEN: {
			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			builder.setTables(ContentDescriptor.Movie.TABLE_NAME);
			return builder.query(db, null, null, null, null, null, null);
		}
		default:
			return null;
		}

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
