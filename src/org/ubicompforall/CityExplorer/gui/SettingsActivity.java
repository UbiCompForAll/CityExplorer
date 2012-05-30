/**
 * @contributor(s): Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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
 *
 */

package org.ubicompforall.CityExplorer.gui;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;
import org.ubicompforall.CityExplorer.map.LocationActivity;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SettingsActivity extends Activity implements OnClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startlayout);

		setButtonListeners( StartActivity.STARTBUTTONS, StartActivity.STARTBUTTON_IDS);
		RelativeLayout start = (RelativeLayout) findViewById(R.id.startView);
		//(Re-) Set button functionality
		if (start != null){
			start.setBackgroundResource(R.drawable.background_for_settings);

			Button b1 = (Button) findViewById(R.id.startButton1);
			b1.setText( getResources().getString(R.string.importdata) );
			Button b2 = (Button) findViewById(R.id.startButton2);
			b2.setText( getResources().getString(R.string.setlocation));
			//start.removeView( findViewById(R.id.startButton3) );
			Button b3 = (Button) findViewById( R.id.startButton3 );
			b3.setText( getResources().getString( R.string.preferences ) );
		}else{
			debug(0, "Couldn't find the startview");
		}
		//SearchButton-listeners
	}//onCreate

	
	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug

	public void setButtonListeners(Button[] buttons, int[] buttonIds) {
		if (buttons.length == buttonIds.length){
			for(Integer b=0; b<buttonIds.length; b++){
				buttons[b] = (Button) findViewById(buttonIds[b]);
				if (buttons[b] != null){
					buttons[b].setOnClickListener(this);
				}else{
					debug(0, "BUTTON["+(b+1)+"] was NULL for "+buttons);
				}//if button not found
			}//for each startButton
		}else{
			debug(0, "Mismatch between buttons[] and buttonsIds[]");
		}
	}//setStartButtons

	/***
	 * Settings Menu Buttons: 1) Import, 2) Set Location 3) ?
	 */
	@Override
	public void onClick(View v) {
		debug(2, "Clicked: "+v);
		if (v.getId() == R.id.startButton1){
			startActivity( new Intent( this, ImportActivity.class));

		}else if (v.getId() == R.id.startButton2){
			startActivity( new Intent( this, LocationActivity.class));

		}else if (v.getId() == R.id.startButton3){
			startActivity( new Intent( this, MyPreferencesActivity.class));
		}else{
			debug(0, "Unknown button clicked: "+v);
		}//if v== button-Plan|Explore|Import
		
		// Terminate activity after a specific settings activity has been selected.
		// Otherwise the user might be confused as the windows for start and settings
		// activities are quite similar.
		finish();
	}//onClick


	/* RS-111122: Moved to CityExplorer.java common Application settings */
}//class


