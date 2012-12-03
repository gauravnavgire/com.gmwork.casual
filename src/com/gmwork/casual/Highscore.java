package com.gmwork.casual;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TableLayout;
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

		TextView name = new TextView(this);
		TextView movebonus = new TextView(this);
		TextView timebonus = new TextView(this);
		TextView guessbonus = new TextView(this);
		TextView total = new TextView(this);
		TableLayout highscoreTable = (TableLayout) findViewById(R.id.highscore_table);

		Map<String, ?> highscores = mHighscorePref.getAll();

		Set<?> set = highscores.entrySet();
		Iterator<?> it = set.iterator();
		TableRow row = new TableRow(this);
		while (it.hasNext()) {
			Map.Entry<String, ?> scores = (Entry<String, ?>) it.next();
			Log.d("GAURAV", " KEY = " + scores.getKey() + " , VALUE = "
					+ scores.getValue());

			if (scores.getKey().equals("player")) {
				name.setText("" + scores.getValue());
				// row.addView(name, 0);
				row.addView(name);
			} else if (scores.getKey().equals("guessbonus")) {
				guessbonus.setText("" + scores.getValue());
				// row.addView(guessbonus, 3);
				row.addView(guessbonus);
			} else if (scores.getKey().equals("timeBonus")) {
				timebonus.setText("" + scores.getValue());
				// row.addView(timebonus, 2);
				row.addView(timebonus);
			} else if (scores.getKey().equals("movesBonus")) {
				movebonus.setText("" + scores.getValue());
				// row.addView(movebonus, 1);
				row.addView(movebonus);
			} else if (scores.getKey().equals("totalScore")) {
				total.setText("" + scores.getValue());
				// row.addView(total, 4);
				row.addView(total);
			}

		}
		highscoreTable.addView(row);

		// for (Entry<String, ?> scores : set) {
		// if (scores.getKey().equals("player")) {
		//
		// } else if (scores.getKey().equals("guessbonus")) {
		//
		// } else if (scores.getKey().equals("timeBonus")) {
		//
		// } else if (scores.getKey().equals("movesBonus")) {
		//
		// } else if (scores.getKey().equals("totalScore")) {
		//
		// }
		//
		// TableRow row = new TableRow(this);
		// row.addView(name, 1);
		// row.addView(movebonus, 2);
		// row.addView(timebonus, 3);
		// row.addView(guessbonus, 4);
		// row.addView(total, 5);
		// highscoreTable.addView(row);
		// }

	}
}
