package org.ubicompforall.CityExplorer.gui;

import org.ubicompforall.CityExplorer.R;

import android.app.TabActivity;
import android.content.*;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.*;
import android.widget.*;

/**
 * This class keeps hold of the tabs used in planning mode.
 * @author Kristian Greve Hagen
 * 
 */
public class PlanActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planlayout);

		initTabs();
	}

	/**
	 * Initializes the tabs used in this activity.
	 */
	private void initTabs() {
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Reusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab

		intent = new Intent().setClass(this, PlanTabPoi.class);
		spec = tabHost.newTabSpec("poi").setIndicator("", res.getDrawable(R.drawable.plan_tab_poi_selector)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, PlanTabTrip.class);
		spec = tabHost.newTabSpec("trip").setIndicator("", res.getDrawable(R.drawable.plan_tab_trip_selector)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#000000"));
		tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#000000"));

		tabHost.setCurrentTab(0);
	}  
}
