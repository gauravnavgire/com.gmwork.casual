package com.gmwork.casual;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import com.gmwork.casual.database.ContentDescriptor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

public class Highscore extends Activity {
	private static final String HIGHSCORE = "highscore";
	private SharedPreferences mHighscorePref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.highscore);

		mHighscorePref = getSharedPreferences(HIGHSCORE, MODE_PRIVATE);

		TableRow row;
		TextView name;
		TextView movebonus;
		TextView timebonus;
		TextView guessbonus;
		TextView total;
		TableLayout highscoreTable = (TableLayout) findViewById(R.id.highscore_table);
		highscoreTable.setSoundEffectsEnabled(true);

		Cursor cur = getContentResolver()
				.query(ContentDescriptor.Highscore.CONTENT_URI, null, null,
						null, null);
		try {
			if (cur != null && cur.moveToFirst()) {

				do {
					row = new TableRow(this);
					row.setBackgroundColor(Color.parseColor("#ffffff"));

					name = new TextView(this);
					name.setBackgroundColor(Color.parseColor("#000000"));
					name.setWidth(100);
//					TableRow.LayoutParams params = (TableRow.LayoutParams) name
//							.getLayoutParams();
//					params.setMargins(1, 1, 1, 1);
//					name.setLayoutParams(params);
					name.setGravity(Gravity.CENTER_HORIZONTAL);

					movebonus = new TextView(this);
					movebonus.setBackgroundColor(Color.parseColor("#000000"));
//					params = (TableRow.LayoutParams) movebonus
//							.getLayoutParams();
//					params.setMargins(1, 1, 1, 1);
//					movebonus.setLayoutParams(params);
					movebonus.setGravity(Gravity.CENTER_HORIZONTAL);

					timebonus = new TextView(this);
					timebonus.setBackgroundColor(Color.parseColor("#000000"));
//					params = (TableRow.LayoutParams) timebonus
//							.getLayoutParams();
//					params.setMargins(1, 1, 1, 1);
//					timebonus.setLayoutParams(params);
					timebonus.setGravity(Gravity.CENTER_HORIZONTAL);

					guessbonus = new TextView(this);
					guessbonus.setBackgroundColor(Color.parseColor("#000000"));
//					params = (TableRow.LayoutParams) guessbonus
//							.getLayoutParams();
//					params.setMargins(1, 1, 1, 1);
//					guessbonus.setLayoutParams(params);
					guessbonus.setGravity(Gravity.CENTER_HORIZONTAL);

					total = new TextView(this);
					total.setBackgroundColor(Color.parseColor("#000000"));
//					params = (TableRow.LayoutParams) total
//							.getLayoutParams();
//					params.setMargins(1, 1, 1, 1);
//					total.setLayoutParams(params);
					total.setGravity(Gravity.CENTER_HORIZONTAL);

					row.setGravity(Gravity.CENTER);
					row.setLayoutParams(new TableRow.LayoutParams(
							TableRow.LayoutParams.MATCH_PARENT,
							TableRow.LayoutParams.WRAP_CONTENT));

					name.setText(""
							+ cur.getString(cur
									.getColumnIndex(ContentDescriptor.Highscore.Column.PLAYERNAME)));
					name.setLayoutParams(new TableRow.LayoutParams(
							TableRow.LayoutParams.MATCH_PARENT,
							TableRow.LayoutParams.WRAP_CONTENT));
					row.addView(name);

					movebonus
							.setText(""
									+ cur.getInt(cur
											.getColumnIndex(ContentDescriptor.Highscore.Column.MOVEBONUS)));
					movebonus.setLayoutParams(new TableRow.LayoutParams(
							TableRow.LayoutParams.MATCH_PARENT,
							TableRow.LayoutParams.WRAP_CONTENT));
					row.addView(movebonus);

					timebonus
							.setText(""
									+ cur.getInt(cur
											.getColumnIndex(ContentDescriptor.Highscore.Column.TIMEBONUS)));
					timebonus.setLayoutParams(new TableRow.LayoutParams(
							TableRow.LayoutParams.MATCH_PARENT,
							TableRow.LayoutParams.WRAP_CONTENT));
					row.addView(timebonus);

					guessbonus
							.setText(""
									+ cur.getInt(cur
											.getColumnIndex(ContentDescriptor.Highscore.Column.GUESSBONUS)));
					guessbonus.setLayoutParams(new TableRow.LayoutParams(
							TableRow.LayoutParams.MATCH_PARENT,
							TableRow.LayoutParams.WRAP_CONTENT));
					row.addView(guessbonus);

					total.setText(""
							+ cur.getInt(cur
									.getColumnIndex(ContentDescriptor.Highscore.Column.TOTALPOINTS)));
					total.setLayoutParams(new TableRow.LayoutParams(
							TableRow.LayoutParams.MATCH_PARENT,
							TableRow.LayoutParams.WRAP_CONTENT));
					row.addView(total);

					highscoreTable.addView(row, new TableLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
				} while (cur.moveToNext());
			}
		} finally {
			if (cur != null) {
				cur.close();
			}
		}
	}
}
