package org.ubicompforall.CityExplorer.gui;

import org.ubicompforall.CityExplorer.R;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * This class is a superclass of PlanTabPoi and PlanTabTrip, and contains the methods they are sharing.
 * @author Kristian
 *
 */
public class PlanTabActivity extends ListActivity{	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trippoi);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.planmenu, menu);
		return true;
	}
}
