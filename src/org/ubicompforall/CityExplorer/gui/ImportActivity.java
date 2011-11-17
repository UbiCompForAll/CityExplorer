package org.ubicompforall.CityExplorer.gui;

import org.ubicompforall.CityExplorer.R;

import android.app.Activity;
import android.os.*;
import android.widget.TextView;

/**
 * This class tracks existing DBs.
 * @author Rune Sætre
 */
public class ImportActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.importview);
		TextView tv = (TextView) findViewById(R.id.importTV);
		tv.setText("WHOOPIE!");
	}//onCreate

}//class
