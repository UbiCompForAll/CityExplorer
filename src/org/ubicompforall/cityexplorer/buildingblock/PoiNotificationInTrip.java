/**
 * @contributor(s): Rune Sætre (NTNU), Jacqueline Floch (SINTEF)
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

package org.ubicompforall.cityexplorer.buildingblock;

import org.ubicompforall.cityexplorer.R;
import org.ubicompforall.simplelanguage.runtime.AbstractStepInstance;
import org.ubicompforall.simplelanguage.runtime.android.AndroidBuildingBlockInstance;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;

public class PoiNotificationInTrip extends AbstractStepInstance implements AndroidBuildingBlockInstance {
	Context context;

	@Override
	public void execute() {
		
		// Get parameter for building block
		String poiName = getStringPropertyValue ("poiName");
// TODO: Replace by domain object
//		DomainObjectReference poiRef = this.getDomainObjectReference("poiName");	
//		CityExplorer.debug(0, "Show notification "+ poiRef.getDisplayText() );

		//Context context = this;
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService( ns );

		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "Arriving at Poi: "+poiName; // 1-second "ticker" notification in the top bar
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
		notification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

		CharSequence contentTitle = "You have arrived at POI";
		CharSequence contentText = "Get bus-info about "+poiName;

		Intent showIntent = new Intent(context, org.ubicompforall.cityexplorer.gui.CalendarActivity.class);
		Time now = new Time();
		now.setToNow();
		showIntent.putExtra( "time", now.toString() );
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, showIntent, 0); 
	    //To make an Intent with no action, use this instead of Intent:
		//contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, new Intent(), 0);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent );
		mNotificationManager.notify(999, notification);
	}//execute

	@Override
	public void setContext(Context context) {
		this.context = context;
	}//setContext

}//class PoiNotification
