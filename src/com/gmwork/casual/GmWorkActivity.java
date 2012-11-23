package com.gmwork.casual;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.gmwork.casual.database.ContentDescriptor;
import com.gmwork.casual.utilities.Constants;

public class GmWorkActivity extends Activity {

	static {

	}

	private Button mPlayBtn;
	private Button mHowToPlayBtn;
	private Button mAboutBtn;
	private Context mContext;
	private ImageButton mSpeakerImageBtn;
	private SoundPool soundPool;
	private int musicId = -1;
	private static String LOG_TAG = "GmWorkActivity";
	private MediaPlayer mediaPlayer;
	private boolean isMusicPlaying = true;
	private Cursor cur;
	private ContentResolver contentResolver;
	private String mInputFile;
	private Facebook facebook = new Facebook(Constants.FACEBOOK_APP_ID);
	private SharedPreferences mSharedPref;
	private AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);

		contentResolver = getContentResolver();

		/*
		 * Get existing access_token if any
		 */
		mSharedPref = getPreferences(MODE_PRIVATE);
		String access_token = mSharedPref.getString("access_token", null);
		long expires = mSharedPref.getLong("access_expires", 0);
		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		/*
		 * Only call authorize if the access_token has expired.
		 */
		if (!facebook.isSessionValid()) {
			/**
			 * facebook authorization
			 */
			facebook.authorize(this, Constants.FACEBOOK_PERMISSIONS,
					new DialogListener() {

						@Override
						public void onComplete(Bundle values) {
							// TODO Auto-generated method stub
							SharedPreferences.Editor editor = mSharedPref
									.edit();
							editor.putString("access_token",
									facebook.getAccessToken());
							editor.putLong("access_expires",
									facebook.getAccessExpires());
							editor.commit();

						}

						@Override
						public void onFacebookError(FacebookError e) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onError(DialogError e) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onCancel() {
							// TODO Auto-generated method stub

						}

					});
		}

		mInputFile = "/mnt/sdcard/movie.xls";
		// load movie DB
		new LoadDBTask().execute(this);

		/**
		 * Setting the background music
		 * 
		 */
		mediaPlayer = new MediaPlayer();

		/**
		 * Setting the audio tweeks
		 * 
		 */
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);

		// try {
		// AssetManager assetManager = getAssets();
		// AssetFileDescriptor descriptor = assetManager
		// .openFd("main_background_music.mp3");
		// musicId = soundPool.load(descriptor, 0);
		// mediaPlayer.setDataSource(descriptor.getFileDescriptor(),
		// descriptor.getStartOffset(), descriptor.getLength());
		// mediaPlayer.prepare();
		// mediaPlayer.setLooping(true);
		// } catch (IOException e) {
		// Log.d(LOG_TAG,
		// "Couldn't load sound effect from asset, " + e.getMessage());
		// mediaPlayer = null;
		//
		// } catch (IllegalStateException e) {
		// Log.d(LOG_TAG, "Couldn't load music from asset, " + e.getMessage());
		// mediaPlayer = null;
		//
		// }

		mPlayBtn = (Button) findViewById(R.id.play_btn);
		mHowToPlayBtn = (Button) findViewById(R.id.howtoplay_btn);
		mAboutBtn = (Button) findViewById(R.id.about_btn);
		mSpeakerImageBtn = (ImageButton) findViewById(R.id.speaker);
		mContext = this;

		/**
		 * Starting the backgound music
		 * 
		 */
		mSpeakerImageBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isMusicPlaying) {
					Log.d(LOG_TAG, "Stopping the background music in OnClick");
					if (mediaPlayer != null) {
						mediaPlayer.stop();
						isMusicPlaying = false;
					}
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

		mPlayBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (musicId != -1) {
					// soundPool.play(musicId, 1, 1, 0, 0, 1);
				}
				Intent playIntent = new Intent(mContext,
						PlayBoardActivity.class);
				playIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(playIntent);
			}
		});

		mHowToPlayBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent playIntent = new Intent(mContext,
						HowToPlayActivity.class);
				playIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(playIntent);
			}
		});

		mAboutBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Builder dialog = new AlertDialog.Builder(GmWorkActivity.this)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setTitle(R.string.about_title)
						.setMessage(R.string.about_message)
						.setNegativeButton(R.string.ok, mDataButtonListener);
				dialog.show();
			}
		});

	}

	private DialogInterface.OnClickListener mDataButtonListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_NEGATIVE:
				dialog.dismiss();
				break;
			}
		}

	};

	protected void onResume() {
		super.onResume();
		if (mediaPlayer != null) {
			// mediaPlayer.start();
		}
	}

	protected void onPause() {
		super.onPause();
		if (mediaPlayer != null) {
			mediaPlayer.pause();
			if (isFinishing()) {
				mediaPlayer.stop();
				mediaPlayer.release();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// facebook.authorizeCallback(requestCode, resultCode, data);
	}

	/**
	 * This class will make api call to get the policy from the database.
	 */
	private class LoadDBTask extends AsyncTask<Context, Void, Void> {
		private ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute() {
			// mProgressDialog = new ProgressDialog(GmWorkActivity.this,
			// ProgressDialog.THEME_HOLO_DARK);
			// mProgressDialog.setMessage("Polulating the database. Please wait");
			// mProgressDialog.show();

		}

		@Override
		protected void onPostExecute(Void result) {
			loadContent();
			// mProgressDialog.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Context... params) {
			readXlsFile();
			return null;
		}
	}

	private void loadContent() {
		cur = contentResolver.query(ContentDescriptor.Movie.CONTENT_URI, null,
				null, null, null);
	}

	private void readXlsFile() {
		if (mInputFile == null) {
			return;
		}
		File xlFile = new File(mInputFile);
		Log.d("****Content****", "The xsl file <" + mInputFile + "> exists ? "
				+ xlFile.exists());
		try {
			Workbook workbook = Workbook.getWorkbook(xlFile);
			Sheet sheet = workbook.getSheet(0);
			for (int j = 0; j < sheet.getRows(); j++) {
				StringBuilder movieString = new StringBuilder();
				for (int i = 0; i < sheet.getColumns(); i++) {
					Cell cell = sheet.getCell(i, j);
					movieString.append(cell.getContents());
					movieString.append(":");
				}
				saveContent(movieString);

			}

		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void saveContent(StringBuilder movieString) {

		String[] movieContent = movieString.toString().split(":");

		if (movieContent != null && movieContent.length > 0) {
			ContentValues cv = new ContentValues();
			cv.put(ContentDescriptor.Movie.Column.MOVIE, movieContent[0]);
			cv.put(ContentDescriptor.Movie.Column.YEAR, movieContent[1]);
			cv.put(ContentDescriptor.Movie.Column.HINT, movieContent[2]);
			contentResolver.insert(ContentDescriptor.Movie.CONTENT_URI, cv);
		}
	}

}