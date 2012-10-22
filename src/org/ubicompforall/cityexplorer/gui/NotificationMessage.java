/**
 * @contributor(s): Rune Sætre (NTNU)
 *
 * Copyright (C) 2011-2012 UbiCompForAll Consortium (SINTEF, NTNU)
 * for the UbiCompForAll project. http://www.sintef.no/Projectweb/UbiCompForAll/
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
package org.ubicompforall.cityexplorer.gui;

import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.cityexplorer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationMessage extends Activity {

	private static void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug

	// END STATIC METHODS
	/////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializes the activity screen etc.
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		debug(0, "Just show the message: ");
		setContentView( R.layout.notification );
		TextView tv = (TextView)findViewById(R.id.theNotificationMessage);
		Intent intent = getIntent();
		String message = intent.getStringExtra(CityExplorer.EXTRA_MESSAGE);
		tv.setText( message );
	}//onCreate

	@Override
	public void onResume(){
		super.onResume();
		//debug(1, "resume");
	} // onResume
	
	@Override
	public void onPause(){
		super.onPause();
	} // onPause

}//class NotificationMessage

