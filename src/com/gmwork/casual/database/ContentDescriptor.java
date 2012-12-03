package com.gmwork.casual.database;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The Descriptor class is not part of the ContentProvider API. I use it as a
 * registry for meta data that describes the content exposed by the
 * ContentProvider class. The Descriptor class manages the following meta data:
 * 
 * 
 * URI Authority – the authority portion of the URI representing this entity. In
 * our example it is “com.favrestaurant.contentprovider”
 * 
 * URI Matcher – this is an internal registry used to map a URI path (serviced
 * by the ContentProvider) to an integer value.
 * 
 * For example, this class is called Restaurant. It exposes meta data such as
 * the entity name, supported URIs, etc. Class EntityClass.Cols – the entity
 * class provides an inner class called Cols. As you may have guessed, this
 * class exposes the name of the columns to exposed by the ContentProvider for
 * the entity.
 * 
 * @author gaurav
 * 
 */

public class ContentDescriptor {
	/**
	 * variables AUTHORITY and BASE_URI. Together these form the URI that
	 * identifies the ContentProvider. The URI is used by Android for
	 * registering the ContentProvider as part of the application. As you will
	 * see later, a ContentResolver class will locate and use the
	 * ContentProvider based on the provided URI.
	 */
	public static final String AUTHORITY = "com.example.database.movieprovider";
	public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
	public static final UriMatcher URI_MATCHER = buildUriMatcher();

	private ContentDescriptor() {

	}

	/**
	 * buildMatcher() creates an instance of URIMatcher for the ContentProvider.
	 * 
	 * @return
	 */
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = AUTHORITY;

		matcher.addURI(authority, Movie.PATH, Movie.PATH_TOKEN);
		matcher.addURI(authority, Movie.PATH_FOR_ID, Movie.PATH_FOR_ID_TOKEN);

		return matcher;
	}

	public static class Movie {
		// For Database Helper class
		public static final String TABLE_NAME = "movies";

		public static class Column {
			public static final String ID = BaseColumns._ID;
			public static final String MOVIE = "movie";
			public static final String YEAR = "year";
			public static final String HINT = "hint";
		}

		// For Content Provider class
		public static final String PATH = "movies";
		public static final int PATH_TOKEN = 100;
		public static final String PATH_FOR_ID = "movies/*";
		public static final int PATH_FOR_ID_TOKEN = 200;
		public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI
				.buildUpon().appendPath(PATH).build();
		public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.content.app";
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.content.app";
	}
}
