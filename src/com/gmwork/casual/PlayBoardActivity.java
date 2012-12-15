package com.gmwork.casual;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gmwork.casual.database.ContentDescriptor;

public class PlayBoardActivity extends Activity implements OnClickListener {
	private TableLayout mTableLayout;
	private static String LOG_TAG = "PlayBoardActivity";
	private TableRow mTableRow;
	private Button mAlphaButton;
	private Button mStart;
	private TextView mHiddenMovie;
	private int mIndex;
	private boolean isGameWon;
	private int mTries = 5;
	private LinearLayout mLinearLayout;
	private ImageView mBollyLogo;
	private TextView mTriesView;
	private TextView mCountdown;
	private MediaPlayer mediaPlayer;
	private boolean isMusicPlaying = true;
	private ImageButton mSpeakerImageBtn;
	static final int DIALOG_CANCEL_ID = 0;
	static final int DIALOG_GUESS_ID = 1;
	static final int DIALOG_PLAYER_NAME = 2;
	private String mMovie;
	private CountDownTimer mCountdownTimer;
	private ScrollView mAplhaScrollView;
	private long mMaxCountdownTime = 40000;
	private long mCountdownDecrement = 1000;
	private long mCurrentCountdownTime = -1;
	private static final String HIGHSCORE = "highscore";
	private static final String PLAYERNAME = "playername";
	private SharedPreferences mHighscorePref, mPlayerNamePref;
	private SharedPreferences.Editor mHighscoreEditor, mPlayerNameEditor;
	private boolean mNewgame;
	private Animation mLogoAnimation;
	private int mGuessBonus = 0;
	private long mTotalPoints = 0;
	private long mTimeBonus = 0;
	private long mMovesBonus = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.playboard);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		mAplhaScrollView = (ScrollView) findViewById(R.id.alphascroll);
		mTriesView = (TextView) findViewById(R.id.tries);
		mCountdown = (TextView) findViewById(R.id.countdown);
		mSpeakerImageBtn = (ImageButton) findViewById(R.id.speaker);
		mLinearLayout = (LinearLayout) findViewById(R.id.playboardlayout);
		Animation backgroundAnimation = AnimationUtils.loadAnimation(this,
				R.anim.background_alpha);
		mLinearLayout.startAnimation(backgroundAnimation);
		mBollyLogo = (ImageView) findViewById(R.id.bollylogo);
		mLogoAnimation = AnimationUtils.loadAnimation(this,
				R.anim.bollylogo_anim);
		mBollyLogo.startAnimation(mLogoAnimation);
		mTableLayout = (TableLayout) findViewById(R.id.alphabet_table);
		mHiddenMovie = (TextView) findViewById(R.id.hiddenmovie);
		mStart = (Button) findViewById(R.id.start);
		mStart.setOnClickListener(this);
		mSpeakerImageBtn.setOnClickListener(this);
		setupMusic();
		reset();
		gameProgress();

		mHighscorePref = getSharedPreferences(HIGHSCORE, MODE_PRIVATE);
		mHighscoreEditor = mHighscorePref.edit();

		mPlayerNamePref = getSharedPreferences(PLAYERNAME, MODE_PRIVATE);
		mPlayerNameEditor = mPlayerNamePref.edit();

		showDialog(DIALOG_PLAYER_NAME);
	}

	private void setupMusic() {
		mediaPlayer = new MediaPlayer();
		try {
			AssetManager assetManager = getAssets();
			AssetFileDescriptor descriptor = assetManager
					.openFd("main_background_music.mp3");
			mediaPlayer.setDataSource(descriptor.getFileDescriptor(),
					descriptor.getStartOffset(), descriptor.getLength());
			mediaPlayer.prepare();
			mediaPlayer.setLooping(true);
		} catch (IOException e) {
			Log.d(LOG_TAG,
					"Couldn't load sound effect from asset, " + e.getMessage());
			mediaPlayer = null;

		} catch (IllegalStateException e) {
			Log.d(LOG_TAG, "Couldn't load music from asset, " + e.getMessage());
			mediaPlayer = null;

		}

	}

	private void reset() {
		mAplhaScrollView.setClickable(false);
		mStart.setVisibility(View.VISIBLE);
		mHiddenMovie.setVisibility(View.GONE);
		// Reset playfield
		mCurrentCountdownTime = mMaxCountdownTime / 1000;
		mTries = 5;
		mCountdown.setText("Time : " + mCurrentCountdownTime + " secs");
		mTriesView.setText("Tries Left : " + mTries);
		/** Set all buttons clickable **/
		for (int i = 0; i < mTableLayout.getChildCount(); i++) {
			mTableRow = (TableRow) mTableLayout.getChildAt(i);
			for (int j = 0; j < mTableRow.getChildCount(); j++) {
				mAlphaButton = (Button) mTableRow.getChildAt(j);
				if (!mAlphaButton.isClickable()) {
					mAlphaButton.setVisibility(View.VISIBLE);
					mAlphaButton.setClickable(true);
					mAlphaButton.setTextColor(Color.parseColor("#68BB6B"));
				}
			}
		}
		/** Get new Movie with a random logic **/
		mMovie = getMovie();
		int count = 0;
		char[] charMovieArray = mMovie.toCharArray();
		mHiddenMovie.setText("");
		/** Set the hidden movie **/
		do {
			if (charMovieArray[count] != ' ') {
				mHiddenMovie.setText(mHiddenMovie.getText() + " _");
			} else {
				mHiddenMovie.setText(mHiddenMovie.getText() + "  ");
			}
			count++;
		} while (count != charMovieArray.length);
	}

	private void gameProgress() {
		final StringBuffer movieBuffer = new StringBuffer(
				mHiddenMovie.getText());
		Log.d(LOG_TAG, "Table childs or rows = " + mTableLayout.getChildCount());

		for (int i = 0; i < mTableLayout.getChildCount(); i++) {
			mTableRow = (TableRow) mTableLayout.getChildAt(i);
			for (int j = 0; j < mTableRow.getChildCount(); j++) {
				mAlphaButton = (Button) mTableRow.getChildAt(j);

				final CharSequence mAlphabet = mAlphaButton.getText();

				mAlphaButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.d(LOG_TAG, "Alphabet pressed is =" + mAlphabet);
						if (mMovie.contains(mAlphabet)) {
							int movieLength = mMovie.length();
							/** Show the hidden movie character **/
							for (int i = 0; i < movieLength; i++) {
								if (mMovie.charAt(i) == mAlphabet.charAt(0)) {
									int indexBuf = i + (i + 1);
									movieBuffer.setCharAt(indexBuf,
											mAlphabet.charAt(0));
								}

							}

							mHiddenMovie.setText(movieBuffer.toString());
							Log.d(LOG_TAG,
									"Alphabet pressed is and it is in the movie = "
											+ mAlphabet);

							String sText = mHiddenMovie.getText().toString()
									.replaceAll(" ", "");
							Log.d(LOG_TAG, "Hidden Movie sText = " + sText);
							if (sText.equals(mMovie.replace(" ", ""))) {
								isGameWon = true;
							} else {
								isGameWon = false;
							}
							if (isGameWon) {
								won();
							}
							Button button = (Button) v;
							button.setTextColor(getResources().getColor(
									android.R.color.transparent));
							button.setClickable(false);

						} else if (!mAlphabet.equals(getResources().getString(
								R.string.guess))) {
							mTries--;
							mTriesView.setText("Tries Left : " + mTries);
							if (mTries == 0) {
								if (mCountdownTimer != null) {
									mCountdownTimer.cancel();
								}
								lost();
							}

							Button button = (Button) v;
							button.setTextColor(getResources().getColor(
									android.R.color.transparent));
							button.setClickable(false);
						} else {

							showDialog(DIALOG_GUESS_ID);
						}

					}

				});
			}
		}

	}

	private void won() {

		String wonmsg = getResources().getString(R.string.won_message) + " "
				+ mMovie + " \n Total Points = "
				+ updateScore(mPlayerNamePref.getString(PLAYERNAME, null));
		Builder dialog = new AlertDialog.Builder(PlayBoardActivity.this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(R.string.congrats_title)
				.setMessage(wonmsg)
				.setPositiveButton(R.string.continue_button,
						mDataButtonListener)
				.setNegativeButton(R.string.main_page, mDataButtonListener);
		dialog.show();

	}

	private long updateScore(String playerName) {
		
		if (mCountdownTimer != null) {
			mCountdownTimer.cancel();
		}

		if (mCurrentCountdownTime != -1) {
			mTimeBonus = mCurrentCountdownTime * 5;
			mMovesBonus = mTries * 10;

			mTotalPoints = mNewgame ? mHighscorePref.getLong("totalScore", 0)
					+ mTimeBonus + mMovesBonus : mTimeBonus + mMovesBonus
					+ mGuessBonus;
		}

		ContentValues values = new ContentValues();
		values.put(ContentDescriptor.Highscore.Column.MOVEBONUS, mMovesBonus);
		values.put(ContentDescriptor.Highscore.Column.TIMEBONUS, mTimeBonus);
		values.put(ContentDescriptor.Highscore.Column.GUESSBONUS, mGuessBonus);
		values.put(ContentDescriptor.Highscore.Column.TOTALPOINTS, mTotalPoints);
		if (playerName != null) {
			String where = ContentDescriptor.Highscore.Column.PLAYERNAME + "=="
					+ playerName;
			getContentResolver().update(
					ContentDescriptor.Highscore.CONTENT_URI, values, where,
					null);
		}
		return mTotalPoints;
	}

	private String getMovie() {
		Cursor cur = getContentResolver().query(
				ContentDescriptor.Movie.CONTENT_URI, null, null, null, null);
		StringBuilder s = new StringBuilder();
		if (cur != null && cur.moveToFirst()) {

			int totalMovies = cur.getCount() - 1;
			int offset;
			if (totalMovies > 0) {
				offset = new Random().nextInt(totalMovies);
				cur.move(offset);
			}

			s.append(cur.getString(cur
					.getColumnIndex(ContentDescriptor.Movie.Column.MOVIE)));
			s.append(" | ");
			s.append(cur.getString(cur
					.getColumnIndex(ContentDescriptor.Movie.Column.YEAR)));
			s.append(" | ");
			s.append(cur.getString(cur
					.getColumnIndex(ContentDescriptor.Movie.Column.HINT)));
			s.append("\n\n");
			String movie = cur.getString(cur
					.getColumnIndex(ContentDescriptor.Movie.Column.MOVIE));

			movie = movie.toUpperCase(Locale.US);
			Log.d(LOG_TAG, "MOVIE = " + movie);
			return movie;

		}
		return null;
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		Dialog dialog = null;
		switch (id) {
		case DIALOG_CANCEL_ID:
			// do the work to define the cancel Dialog
			break;
		case DIALOG_GUESS_ID:
			// do the work to define the guess Dialog
			dialog = new Dialog(PlayBoardActivity.this);
			dialog.setContentView(R.layout.guessdialog);
			dialog.setTitle(R.string.guess_title);
			final EditText guessWord = (EditText) dialog
					.findViewById(R.id.guess_word);
			Button guessButton = (Button) dialog.findViewById(R.id.guess);
			Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
			cancelButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismissDialog(DIALOG_GUESS_ID);

				}
			});
			guessButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (guessWord != null) {
						if (guessWord.getText().toString().trim()
								.equalsIgnoreCase(mMovie)) {

							mHiddenMovie.setText(mMovie);
							mGuessBonus = 1000;
							won();

						} else {
							lost();
						}
					}
				}
			});

			break;
		case DIALOG_PLAYER_NAME:
			// do the work to define the guess Dialog
			dialog = new Dialog(PlayBoardActivity.this);
			dialog.setContentView(R.layout.guessdialog);
			dialog.setTitle(R.string.playername_title);
			dialog.setCancelable(false);
			final EditText playername = (EditText) dialog
					.findViewById(R.id.guess_word);
			TextView message = (TextView) dialog.findViewById(R.id.message);
			message.setText(R.string.playername_msg);
			Button name = (Button) dialog.findViewById(R.id.guess);
			Button cancel = (Button) dialog.findViewById(R.id.cancel);
			cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismissDialog(DIALOG_PLAYER_NAME);
					onBackPressed();
				}
			});
			name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mPlayerNameEditor.putString(PLAYERNAME, playername
							.getText().toString());
					mPlayerNameEditor.commit();
					dismissDialog(DIALOG_PLAYER_NAME);
					Toast.makeText(getApplicationContext(),
							"Welcome " + playername.getText().toString(),
							Toast.LENGTH_LONG).show();
					mNewgame = true;
					ContentValues values = new ContentValues();
					values.put(ContentDescriptor.Highscore.Column.MOVEBONUS, 0);
					values.put(ContentDescriptor.Highscore.Column.TIMEBONUS, 0);
					values.put(ContentDescriptor.Highscore.Column.GUESSBONUS, 0);
					values.put(ContentDescriptor.Highscore.Column.TOTALPOINTS,
							0);
					getContentResolver().insert(
							ContentDescriptor.Highscore.CONTENT_URI, values);
				}
			});

			break;
		default:
			dialog = null;
		}
		return dialog;

	}

	private DialogInterface.OnClickListener mDataButtonListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				dialog.dismiss();
				reset();
				gameProgress();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				dialog.dismiss();
				finish();
				break;
			}
		}

	};

	protected void onResume() {
		super.onResume();
		if (mCountdownTimer != null) {
			mCountdownTimer.start();
		}
		if (mediaPlayer != null) {
			mediaPlayer.start();
		}
	}

	protected void onPause() {
		super.onPause();
		// cancel the countdown timer and make note of the current count down
		// time
		if (mCountdownTimer != null) {
			mCountdownTimer.cancel();
		}
		if (mediaPlayer != null) {
			mediaPlayer.pause();
			if (isFinishing()) {
				mediaPlayer.stop();
				mediaPlayer.release();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start:
			mAplhaScrollView.setClickable(true);
			mStart.setVisibility(View.GONE);
			mHiddenMovie.setVisibility(View.VISIBLE);
			mHiddenMovie.startAnimation(mLogoAnimation);
			if (mCountdownTimer == null) {
				mCountdownTimer = new CountDownTimer(mMaxCountdownTime,
						mCountdownDecrement) {

					@Override
					public void onTick(long millisUntilFinished) {
						mCurrentCountdownTime = millisUntilFinished / 1000;
						mCountdown.setText("Time : " + mCurrentCountdownTime
								+ " secs");
					}

					@Override
					public void onFinish() {
						mCurrentCountdownTime = -1;
						lost();
					}
				}.start();
			} else {
				mCurrentCountdownTime = mMaxCountdownTime / 1000;
				mCountdown.setText("Time : " + mCurrentCountdownTime + " secs");
				mCountdownTimer.cancel();
				mCountdownTimer.start();
			}
			break;

		case R.id.speaker:

			if (isMusicPlaying) {
				Log.d(LOG_TAG, "Stopping the background music in OnClick");
				mediaPlayer.stop();
				isMusicPlaying = false;
				mSpeakerImageBtn.setBackground(getResources().getDrawable(
						R.drawable.speaker_off));
			} else {
				Log.d(LOG_TAG, "Starting the background music in OnClick");
				try {
					mediaPlayer.prepare();
					mediaPlayer.start();
					isMusicPlaying = true;
				} catch (IllegalStateException e) {

					Log.d(LOG_TAG,
							"Couldn't load music from asset, " + e.getMessage());
				} catch (IOException e) {

					Log.d(LOG_TAG,
							"Couldn't load music from asset, " + e.getMessage());
				}
				mSpeakerImageBtn.setBackground(getResources().getDrawable(
						R.drawable.speaker));
			}

		default:
			break;
		}

	}

	public void lost() {
		String lostmsg = getResources().getString(R.string.lost_message) + " "
				+ mMovie;
		Builder dialog = new AlertDialog.Builder(PlayBoardActivity.this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(R.string.game_lost).setMessage(lostmsg)
				.setNegativeButton(R.string.main_page, mDataButtonListener);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	protected void onDestroy() {
		if (mCountdownTimer != null) {
			mCountdownTimer.cancel();
		}
		super.onDestroy();
	}

}
