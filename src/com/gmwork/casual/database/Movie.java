package com.gmwork.casual.database;

/**
 * Contains Movie table meta data which is used by Database Helper class to
 * create the Movie table.
 * 
 * Also contains meta data that defines the Movie entity managed by the
 * associated ContentProvider.
 * 
 * @author gaurav
 * 
 */
public class Movie {

	// member variables
	private long mId;
	private String mMovie;
	private String mYear;
	private String mHint;

	public String getMovie() {
		return mMovie;
	}

	public void setMovie(String mMovie) {
		this.mMovie = mMovie;
	}

	public String getYear() {
		return mYear;
	}

	public void setYear(String mYear) {
		this.mYear = mYear;
	}

	public String getHint() {
		return mHint;
	}

	public void setHint(String mHint) {
		this.mHint = mHint;
	}

	public long getId() {
		return mId;
	}

	@Override
	public String toString() {
		return "Movie : " + mMovie + " , YearOfRelease : " + mYear
				+ " , Hint : " + mHint;
	}

}
