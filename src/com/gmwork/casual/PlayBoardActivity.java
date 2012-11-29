package com.gmwork.casual;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
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
	private String mMovie;
	private CountDownTimer mCountdownTimer;
	private ScrollView mAplhaScrollView;

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

		/** Get the Movie **/
		mMovie = getMovie();

		final StringBuffer movieBuffer;
		mAplhaScrollView = (ScrollView) findViewById(R.id.alphascroll);
		mTriesView = (TextView) findViewById(R.id.tries);
		mCountdown = (TextView) findViewById(R.id.countdown);
		mSpeakerImageBtn = (ImageButton) findViewById(R.id.speaker);
		mLinearLayout = (LinearLayout) findViewById(R.id.playboardlayout);
		Animation backgroundAnimation = AnimationUtils.loadAnimation(this,
				R.anim.background_alpha);
		mLinearLayout.startAnimation(backgroundAnimation);

		mBollyLogo = (ImageView) findViewById(R.id.bollylogo);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this,
				R.anim.bollylogo_anim);
		mBollyLogo.startAnimation(hyperspaceJumpAnimation);

		mTableLayout = (TableLayout) findViewById(R.id.alphabet_table);
		mHiddenMovie = (TextView) findViewById(R.id.hiddenmovie);

		mStart = (Button) findViewById(R.id.start);
		mStart.setOnClickListener(this);

		int count = 0;
		char[] charMovieArray = mMovie.toCharArray();

		/**
		 * Starting the backgound music
		 * 
		 */
		mSpeakerImageBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isMusicPlaying) {
					Log.d(LOG_TAG, "Stopping the background music in OnClick");
					mediaPlayer.stop();
					isMusicPlaying = false;
				} else {
					Log.d(LOG_TAG, "Starting the background music in OnClick");
					try {
						mediaPlayer.prepare();
						mediaPlayer.start();
						isMusicPlaying = true;
					} catch (IllegalStateException e) {

						Log.d(LOG_TAG,
								"Couldn't load music from asset, "
										+ e.getMessage());
					} catch (IOException e) {

						Log.d(LOG_TAG,
								"Couldn't load music from asset, "
										+ e.getMessage());
					}

				}
			}
		});

		// Reset playfield
		mTries = 5;
		/** Set all buttons clickable **/
		for (int i = 0; i < mTableLayout.getChildCount(); i++) {
			mTableRow = (TableRow) mTableLayout.getChildAt(i);
			for (int j = 0; j < mTableRow.getChildCount(); j++) {
				mAlphaButton = (Button) mTableRow.getChildAt(j);
				if (mAlphaButton.isClickable()) {
					mAlphaButton.setClickable(true);
				}
			}
		}
		/** Get new Movie with a random logic **/
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

		movieBuffer = new StringBuffer(mHiddenMovie.getText());

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
								if (mCountdownTimer != null) {
									mCountdownTimer.cancel();
								}
								String wonmsg = getResources().getString(
										R.string.won_message)
										+ " " + mMovie;
								Builder dialog = new AlertDialog.Builder(
										PlayBoardActivity.this)
										.setIcon(
												android.R.drawable.ic_dialog_info)
										.setTitle(R.string.congrats_title)
										.setMessage(wonmsg)
										.setPositiveButton(
												R.string.continue_button,
												mDataButtonListener)
										.setNegativeButton(R.string.main_page,
												mDataButtonListener);
								dialog.show();
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
							Builder dialog = new AlertDialog.Builder(
									PlayBoardActivity.this)
									.setIcon(android.R.drawable.ic_dialog_info)
									.setTitle(R.string.congrats_title)
									.setMessage(R.string.won_message)
									.setPositiveButton(
											R.string.continue_button,
											mDataButtonListener)
									.setNegativeButton(R.string.main_page,
											mDataButtonListener);
							dismissDialog(DIALOG_GUESS_ID);
							dialog.show();

						} else {
							lost();
						}
					}
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
				Intent playIntent = new Intent(PlayBoardActivity.this,
						PlayBoardActivity.class);
				playIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(playIntent);
				finish();
				overridePendingTransition(0, 0);
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
			// mediaPlayer.start();
		}
	}

	protected void onPause() {
		super.onPause();
		// cancel the countdown timer and make note of the current coutn down
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
			mCountdownTimer = new CountDownTimer(30000, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					mCountdown.setText("Time : " + (millisUntilFinished / 1000)
							+ " secs");

				}

				@Override
				public void onFinish() {
					lost();
				}
			}.start();
			break;

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
