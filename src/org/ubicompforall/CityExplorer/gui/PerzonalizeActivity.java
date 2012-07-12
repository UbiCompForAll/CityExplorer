/**
 * @contributor(s): Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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

import java.io.File;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;

import org.ubicompforall.CityExplorer.gui.MyPreferencesActivity;
import org.ubicompforall.CityExplorer.buildingblock.CityExplorerCompositionFactory;

import org.ubicompforall.descriptor.UbiCompDescriptorPackage;
import org.ubicompforall.library.communication.CommunicationFactory;
import org.ubicompforall.simplelanguage.SimpleLanguagePackage;
import org.ubicompforall.simplelanguage.UserService;
import org.ubicompforall.simplelanguage.runtime.RuntimeEnvironment;
import org.ubicompforall.ubicomposer.android.ModelUtils;
import org.ubicompforall.ubicomposer.android.TaskListActivity;
import org.ubicompforall.ubicomposer.util.UserServiceUtils;
import org.ubicompforall.ubicomprun.android.RuntimeEnvironmentInstance;
import org.ubicompforall.ubicomprun.android.UserServiceExecutionService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class PerzonalizeActivity extends Activity implements OnClickListener{

	UserService userService;
	String userServiceFileName;
    private static final String FILE_EXTENSION = ".simplelanguage";    
    private static final String DB_EXTENSION = ".sqlite";    



	@Override
	public void onCreate(Bundle savedInstanceState) {
			
		super.onCreate(savedInstanceState);		
		// Same layout as Start activity
		setContentView(R.layout.startlayout);
		
		setUserService (this);			// settings for user service composition

		
	}//onCreate

	@Override
	protected void onResume() {
		super.onResume();
		
		if (userService == null) setUserService (this);
		
		setButtonListeners( StartActivity.STARTBUTTONS, StartActivity.STARTBUTTON_IDS);
		RelativeLayout start = (RelativeLayout) findViewById(R.id.startView);
		
		// Set button functionality
		if (start != null){
			start.setBackgroundResource(R.drawable.background_for_settings);

			Button b1 = (Button) findViewById(R.id.startButton1);
			b1.setText( getResources().getString(R.string.editUserService) );
			Button b2 = (Button) findViewById(R.id.startButton2);
			b2.setText( getResources().getString(R.string.startUserService));
			//start.removeView( findViewById(R.id.startButton3) );
			Button b3 = (Button) findViewById( R.id.startButton3 );
			b3.setText( getResources().getString( R.string.stopUserService ) );
		}else{
			debug(0, "Couldn't find the startview");
		}
	}//onResume

	/***
	 * Simply call debug method define by the City Explorer application
	 */
	private static void debug(int level, String message ) {
		CityExplorer.debug (level, message );		
	} //debug

	/***
	 * Associate listeners to buttons 
	 */	
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
			debug(0, "StartActivity.java: Mismatch between buttons[] and buttonsIds[]");
		}
				
	}//setButtonListeners

	/***
	 * Called when a button in a view has been clicked 
	 */	
	@Override
	public void onClick(View v) {
		debug(2, "Clicked: "+v );
		if (v.getId() == R.id.startButton1){  		// Edit user service
	    	if ( userServiceFileName != null) {
	    		Intent taskListIntent = new Intent(this, TaskListActivity.class);
	    		taskListIntent.setData(Uri.fromFile(getFileStreamPath(userServiceFileName)));
	    		this.startActivity(taskListIntent);
	    	}
		} else if (v.getId() == R.id.startButton2){ // Start tasks in a user service

			// Ensure that the runtime environment has registered what it needs
			RuntimeEnvironment env = RuntimeEnvironmentInstance.getRuntimeEnvironment();
			env.setUserService(userService);
			env.registerFactory(new CommunicationFactory()); // TODO: Find out how to avoid this hardcoding
			env.registerFactory(new CityExplorerCompositionFactory()); 			
			// Start the service by sending a startService intent
			Intent intent = new Intent(this, UserServiceExecutionService.class);
			startService(intent);

		} else if (v.getId() == R.id.startButton3){ // Stop tasks in a user service
			RuntimeEnvironment env = RuntimeEnvironmentInstance.getRuntimeEnvironment();
			env.setActive(false);
		} else{
			debug(0, "Unknown button clicked: "+v);
		}//if v== button-Plan|Explore|Import
	}//onClick


	/***
	 * Prepares the file needed for composition before calling the composition tool. 
	 * NB: The composition descriptors were copied by the Application.
	 */
	public void setUserService (Context context) {
		
//TODO: not sure that there should be one file per database... To be further discussed.

		// Build name of user service file
		String dbFileName = MyPreferencesActivity.getSelectedDbName (context); 		// Get current DbFileName
		userServiceFileName = dbFileName.replace(DB_EXTENSION, FILE_EXTENSION);
		
		// Create the file for storing user service if not already existing
		File file = this.getFileStreamPath(userServiceFileName);

		if(! file.exists()) {		// create the composition file and the user service
//TODO: the descriptor file should not be added by CreateModel
// Create Model sets the service name and add the Communication.ubicompdescriptor to the file	
			userService = ModelUtils.createModel (this, userServiceFileName.substring(0, userServiceFileName.lastIndexOf (FILE_EXTENSION))); 
			UserServiceUtils.addLibraryToUserService(context.getFileStreamPath("CityExplorer.ubicompdescriptor").getAbsolutePath(), userService);
			UserServiceUtils.saveUserService(getFileStreamPath(userServiceFileName).getAbsolutePath(), userService);			
		} else {					// open the file name
			@SuppressWarnings("unused")
			SimpleLanguagePackage pkg = SimpleLanguagePackage.eINSTANCE;
			@SuppressWarnings("unused")
			UbiCompDescriptorPackage pkg2 = UbiCompDescriptorPackage.eINSTANCE;
			userService = UserServiceUtils.loadUserService(getFileStreamPath(userServiceFileName).getAbsolutePath());			
		}		
	}//setUserService

}//class

