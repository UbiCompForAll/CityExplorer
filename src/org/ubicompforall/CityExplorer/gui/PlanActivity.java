/**
 * @contributor(s): Kristian Greve Hagen (NTNU), Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			23 May 2011
 * @revised:
 *
 * Copyright (C) 2011-2012 UbiCompForAll Consortium (SINTEF, NTNU)
 * for the UbiCompForAll project
 *
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 *
 * See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 */

/**
 * @description:
 * This class keeps hold of the tabs used in planning mode.
 * 
 */

package org.ubicompforall.CityExplorer.gui;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;

import android.app.TabActivity;
import android.content.*;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.*;
import android.widget.*;

public class PlanActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		debug(1, "PlanActivity create");
		setContentView(R.layout.tablayout);

		initTabs();
	}//onCreate
	
//	@Override
//	public void onStart(){
//		super.onStart();
//		debug(1, "Start");
//	}//onStart

	
	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug



	/**
	 * Initializes the tabs used in this activity.
	 */
	private void initTabs() {
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Reusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab

		intent = new Intent().setClass(this, PlanPoiTab.class);
		spec = tabHost.newTabSpec("poi").setIndicator("LOCATIONS", res.getDrawable(R.drawable.tab_selector)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, PlanTripTab.class);
		spec = tabHost.newTabSpec("trip").setIndicator("TOURS", res.getDrawable(R.drawable.tab_selector)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#000000"));
		tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#000000"));

		tabHost.setCurrentTab(0);
	}//initTabs
}//PlanActivity
